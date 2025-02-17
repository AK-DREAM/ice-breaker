package demoMod.icebreaker.cards.lightlemon;

import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.icebreaker.IceBreaker;
import demoMod.icebreaker.actions.SelectCardInCardGroupAction;
import demoMod.icebreaker.enums.AbstractCardEnum;
import demoMod.icebreaker.interfaces.CardAddToDeckSubscriber;
import demoMod.icebreaker.interfaces.TriggerFetterSubscriber;
import demoMod.icebreaker.powers.ExtraTurnPower;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractLightLemonCard extends CustomCard implements CardAddToDeckSubscriber, CustomSavable<List<String>>, TriggerFetterSubscriber {
    public int m2 = 0;
    public int baseM2 = 0;
    public boolean isM2Upgraded = false;
    public boolean extraEffectOnExtraTurn = false;
    public boolean isFetter = false;
    public boolean isBottom = false;
    public List<UUID> fetterTarget = new ArrayList<>();
    private final List<AbstractCard> myCardsToPreview = new ArrayList<>();
    protected Predicate<AbstractCard> fetterFilter = card -> true;
    public int fetterAmount = 1; // 要不还是public吧？
    private float previewTimer = 0.0F;

    public boolean ConnectionOfMeteor = false;

    public AbstractLightLemonCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, AbstractCardEnum.ICEBREAKER, rarity, target);
    }

    public boolean isM2Buffed() {
        return m2 > baseM2;
    }

    public void upgradeM2(int amount) {
        this.baseM2 += amount;
        this.m2 = this.baseM2;
        isM2Upgraded = true;
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (AbstractDungeon.player.hasPower(ExtraTurnPower.POWER_ID) && extraEffectOnExtraTurn) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void onTriggerFetter() {

    }

    @Override
    public void onOtherCardTriggerFetter(AbstractCard playedCard, List<AbstractCard> fetterCards) {

    }

    @Override
    public void onTriggerFetterFailed() {

    }

    @Override
    public List<String> onSave() {
        return fetterTarget.stream().map(UUID::toString).collect(Collectors.toList());
    }

    @Override
    public Type savedType() {
        return new TypeToken<List<String>>(){}.getType();
    }

    @Override
    public void onLoad(List<String> s) {
        fetterTarget = s.stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void loadCardsToPreview() {
        if (AbstractDungeon.player == null) return;
        for (AbstractCard card1 : AbstractDungeon.player.masterDeck.group) {
            if (fetterTarget.contains(card1.uuid)) {
                if (card1 instanceof AbstractLightLemonCard) {
                    this.myCardsToPreview.add(((AbstractLightLemonCard) card1).makeStatEquivalentCopyWithoutPreviewCard());
                } else {
                    this.myCardsToPreview.add(card1.makeSameInstanceOf());
                }
            }
        }
    }

    private AbstractCard makeStatEquivalentCopyWithoutPreviewCard() { //防止两张牌互相羁绊时出现递归调用的情况
        AbstractCard card = super.makeStatEquivalentCopy();
        card.uuid = this.uuid;
        // 复制的uuid相同来触发"发光显示目前在抽牌堆中的牌的效果"
        if (card instanceof AbstractLightLemonCard) {
            AbstractLightLemonCard lightLemonCard = (AbstractLightLemonCard) card;
            lightLemonCard.fetterTarget = this.fetterTarget;
        }
        return card;
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard card = super.makeStatEquivalentCopy();
        if (card instanceof AbstractLightLemonCard) {
            AbstractLightLemonCard lightLemonCard = (AbstractLightLemonCard) card;
            lightLemonCard.fetterTarget = this.fetterTarget;

            // modified to simplify the code
            lightLemonCard.loadCardsToPreview();
            lightLemonCard.ConnectionOfMeteor = this.ConnectionOfMeteor;
        }
        return card;
    }

    @Override
    public void onAddToMasterDeck() {
        if (isFetter) {
            IceBreaker.addToBot(new SelectCardInCardGroupAction(Math.min(fetterAmount, AbstractDungeon.player.masterDeck.size()),
                    card -> {
                        return card != this && this.fetterFilter.test(card)
                                && !this.fetterTarget.contains(card.uuid); // don't add duplicated cards
                    },
                    card -> {
                        this.fetterTarget.add(card.uuid);
                        this.myCardsToPreview.add(card.makeSameInstanceOf());
                        // add its copy, not the card itself
                        // same uuid just for convenience
                        card.stopGlowing();
                    },
                    AbstractDungeon.player.masterDeck));
        }
    }

    private boolean hovered = false;
    @Override
    public void hover() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
            // make fettered cards glow when viewing master deck
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (fetterTarget.contains(c.uuid)) {
                    c.beginGlowing();
                }
            }
        }
        if (!this.hovered) {
            this.hovered = true;
        }
        super.hover();
    }
    @Override
    public void unhover() {
        if (this.hovered) {
            // stop glowing
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    c.stopGlowing();
                }
            }
            this.hovered = false;
        }
        super.unhover();
    }
    @Override
    public void renderCardTip(SpriteBatch sb) {
        super.renderCardTip(sb);
        previewTimer += Gdx.graphics.getDeltaTime();
        if (previewTimer > 0.75F) {
            previewTimer = 0.0F;
            if (this.cardsToPreview != null) {
                this.myCardsToPreview.add(this.cardsToPreview);
                this.cardsToPreview = null;
            }
            if (!this.myCardsToPreview.isEmpty()) {
                this.cardsToPreview = this.myCardsToPreview.remove(0);
                this.cardsToPreview.stopGlowing();
                if (AbstractDungeon.player != null && AbstractDungeon.player.hoveredCard == this) {
                    // set glow color of the previewing card to gold if you can successfully grab it from draw pile
                    for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                        if (c.uuid.equals(this.cardsToPreview.uuid)) {
                            this.cardsToPreview.glowColor = Color.GOLD.cpy();
                            this.cardsToPreview.beginGlowing();
                        }
                    }
                }
            }
        }
    }
}
