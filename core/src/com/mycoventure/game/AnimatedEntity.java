package com.mycoventure.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Vector;

/**
 * Created by david.chong on 2015/04/27.
 */
public class AnimatedEntity extends Entity {

   Vector<Animation> AnimationSheets;
    float AnimationRate;
    float StateTime;
    int numrow, numcol;
    public AnimatedEntity(float scale) {
        super(scale);
        StateTime = 0;
        AnimationSheets = new Vector<Animation>();
    }

    public void setAnimationSheets(Texture t, int CellSize) {

        TextureRegion[][] tmp = TextureRegion.split(t, CellSize , CellSize);
        numrow = t.getHeight() / CellSize;
        numcol = t.getWidth() / CellSize;
        AnimationRate = 1.0f / (float) numcol;

        for(int i = 0; i < numrow; i++) {
            TextureRegion[] WalkFrames = new TextureRegion[numcol];
            for (int j = 0; j < numcol; j++) {
                WalkFrames[j] = tmp[i][j];
            }
            AnimationSheets.add(new Animation(AnimationRate, WalkFrames));
        }

    }

    public void Animate(float delta, int Index) {
        StateTime += delta;
        if(StateTime > numcol * AnimationRate) StateTime = 0;
        tmo.setTextureRegion(AnimationSheets.get(Index).getKeyFrame(StateTime));
    }

    public void GetStatic(int Index) {
        StateTime = 0;
        tmo.setX(getX());
        tmo.setY(getY());
        tmo.setTextureRegion(AnimationSheets.get(Index).getKeyFrame(StateTime));
    }
}
