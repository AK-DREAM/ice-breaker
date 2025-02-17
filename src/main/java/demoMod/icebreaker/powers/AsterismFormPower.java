package demoMod.icebreaker.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import demoMod.icebreaker.IceBreaker;
import demoMod.icebreaker.actions.SelectCardInCardGroupAction;
import demoMod.icebreaker.interfaces.EnterOrExitExtraTurnSubscriber;
import demoMod.icebreaker.utils.PowerRegionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class AsterismFormPower extends AbstractPower implements EnterOrExitExtraTurnSubscriber {
    public static final String POWER_ID = IceBreaker.makeID("AsterismFormPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    private final boolean upgraded;

    public AsterismFormPower(AbstractCreature owner, int amount, boolean upgraded) {
        this.owner = owner;
        this.ID = POWER_ID;
        this.name = NAME;
        this.amount = amount;
        this.upgraded = upgraded;
        if (this.upgraded) {
            this.ID += "+";
            this.name += "+";
        }
        this.updateDescription();
        PowerRegionLoader.load(this, "StarryForm");
    }

    @Override
    public void updateDescription() {
        this.description = String.format(this.upgraded ? DESC[1] : DESC[0], this.amount);
    }

    @Override
    public void atStartOfTurn() {
        triggerPower();
    }

    @Override
    public void onEnterExtraTurn() {
        triggerPower();
    }

    @Override
    public void onExitExtraTurn() {

    }

    private void triggerPower() {
        this.flash();
        addToBot(new SelectCardInCardGroupAction(1, card -> true, card -> {
            AbstractMonster m = null;
            if (card.target == AbstractCard.CardTarget.ENEMY) {
                m = AbstractDungeon.getRandomMonster();
            }

            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = (float)Settings.HEIGHT / 2.0F;
            if (m != null) {
                tmp.calculateCardDamage(m);
            }

            tmp.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, m, card.energyOnUse, true, true), true);
        }, getRandomCards()));
    }

    private CardGroup getRandomCards() {
        CardGroup ret = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        ArrayList<AbstractCard> allCards = CardLibrary.getAllCards();
        Collections.shuffle(allCards, new Random(AbstractDungeon.cardRandomRng.random.nextLong()));
        ret.group.addAll(allCards.subList(0, 15).stream().map(card -> {
            AbstractCard tmp = card.makeCopy();
            if (upgraded || AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
                tmp.upgrade();
            }
            return tmp;
        }).collect(Collectors.toList()));
        return ret;
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }
}
