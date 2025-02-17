package demoMod.icebreaker.cards.lightlemon;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import demoMod.icebreaker.IceBreaker;
import demoMod.icebreaker.actions.SelectCardInHandAction;
import demoMod.icebreaker.powers.ExtraTurnPower;

public class Oracle extends AbstractLightLemonCard {
    public static final String ID = IceBreaker.makeID("Oracle");

    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String IMG_PATH = "cards/AirCut.png";

    private static final CardType TYPE = CardType.SKILL;
    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.NONE;

    private static final int COST = 1;

    public Oracle() {
        super(ID, NAME, IceBreaker.getResourcePath(IMG_PATH), COST, DESCRIPTION, TYPE, RARITY, TARGET);
        this.tags.add(CardTags.HEALING);
        this.baseBlock = this.block = 5;
        this.baseMagicNumber = this.magicNumber = 1;
        this.extraEffectOnExtraTurn = true;
        this.isFetter = true;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            this.upgradeName();
            this.upgradeBlock(3);
            this.upgradeMagicNumber(1);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
        if (p.hasPower(ExtraTurnPower.POWER_ID)) {
            addToBot(new SelectCardInHandAction(this.magicNumber, card -> true, card -> card.selfRetain = true));
        } else {
            if (!AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic("Runic Pyramid") && !AbstractDungeon.player.hasPower("Equilibrium")) {
                addToBot(new SelectCardInHandAction(this.magicNumber, card -> true, card -> card.retain = true));
            }
        }
    }
}
