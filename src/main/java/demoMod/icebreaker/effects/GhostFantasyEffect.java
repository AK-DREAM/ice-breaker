package demoMod.icebreaker.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class GhostFantasyEffect extends AbstractGameEffect {
    float x;
    float y;

    public GhostFantasyEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.duration = 0.5F;
        this.scale = 0.0F;
    }

    @Override
    public void update() {
        if (this.duration == 0.5F) {
            CardCrawlGame.sound.playA("BUFF_2", -0.6F);
        }

        this.scale -= Gdx.graphics.getDeltaTime();
        if (this.scale < 0.0F) {
            this.scale = 0.05F;
            AbstractDungeon.effectsQueue.add(new SwirlyGhostEffect(this.x + MathUtils.random(-150.0F, 150.0F) * Settings.scale, this.y + MathUtils.random(-150.0F, 150.0F) * Settings.scale));
            AbstractDungeon.effectsQueue.add(new SwirlyGhostEffect(this.x + MathUtils.random(-150.0F, 150.0F) * Settings.scale, this.y + MathUtils.random(-150.0F, 150.0F) * Settings.scale));
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
