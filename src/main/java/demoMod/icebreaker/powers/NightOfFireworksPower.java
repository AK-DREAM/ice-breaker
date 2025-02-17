package demoMod.icebreaker.powers;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import demoMod.icebreaker.IceBreaker;
import demoMod.icebreaker.cards.lightlemon.tempCards.Spark;
import demoMod.icebreaker.interfaces.EnterOrExitExtraTurnSubscriber;
import demoMod.icebreaker.utils.PowerRegionLoader;

public class NightOfFireworksPower extends AbstractPower implements EnterOrExitExtraTurnSubscriber {
    public static final String POWER_ID = IceBreaker.makeID("NightOfFireworksPower");
    private static final PowerStrings powerStrings;
    public static final String NAME;
    public static final String[] DESC;

    public NightOfFireworksPower(AbstractCreature owner, int amount) {
        this.owner = owner;
        this.amount = amount;
        this.ID = POWER_ID;
        this.name = NAME;
        this.updateDescription();
        PowerRegionLoader.load(this, "FrostCountry");
    }

    @Override
    public void updateDescription() {
        this.description = String.format(DESC[0], this.amount);
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
        addToBot(new MakeTempCardInDrawPileAction(new Spark(), this.amount, true, true, false));
    }

    static {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        NAME = powerStrings.NAME;
        DESC = powerStrings.DESCRIPTIONS;
    }
}
