package com.mycoventure.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;

/**
 * Created by David on 27/04/2015.
 */
public class MushroomSource extends AnimatedEntity implements Collectible, Examinable {
    public static final int ICON = 0;
    public static final int FINAL_FRUITS = 1;
    public static final int STAGE_0 = 2;
    public static final int STAGE_25 = 3;
    public static final int STAGE_50 = 4;
    public static final int STAGE_75 = 5;
    public static final int STAGE_100 = 6;
    public static final int PINNING = 7;
    public static final int DEAD = 8;
    public static final int SUBSTRATE = 9;
    public static final int SUBSTRATE_COLONIZED = 10;

    int Yield;
    int Efficiency, Speed, SupplementPreference, HumidityPreference, TemperatureTrigger;
    int ColonisationPercentage, SubstrateRemaining, OriginalSubstrate;
    int State;
    String Name, ExamineInfo;
    TextureMapObject Background;

    public MushroomSource(float scale, String Name, String ExamineInfo) {
        super(scale);
        this.Name = Name;
        this.ExamineInfo = ExamineInfo;
        Background = new TextureMapObject();
        SubstrateRemaining = 0;
        Efficiency = 0;
        Speed = 0;
        SupplementPreference = 0;
        HumidityPreference = 0;
        TemperatureTrigger = 0;
        ColonisationPercentage = 0;
        Yield = 0;
        OriginalSubstrate = 0;
    }
    @Override
    public void Collect(Player p) {
        if(p.Mushrooms.containsKey(Name)) p.Mushrooms.put(Name, p.Mushrooms.get(Name).intValue() + Yield);
        else p.Mushrooms.put(Name, Yield);
        p.Waste += OriginalSubstrate;
        Yield = 0;
    }

    @Override
    public String Examine() {
        switch(State){
            case ICON:
                return ExamineInfo;
            case FINAL_FRUITS:
                return ExamineInfo;
            case STAGE_0:
                return "There doesn't seem to be anything growing";
            case STAGE_25:
                return "There are white wisps covering the substrate";
            case STAGE_50:
                return "The substrate is matted with white patches";
            case STAGE_75:
                return "I can barely see the substrate now.";
            case STAGE_100:
                return "I think its ready for fruiting";
            case PINNING:
                return "There are lumps developing all over the mycelium";
            case DEAD:
                return "Lifeless";
            default:
                return "";
        }
    }

    @Override
    public void update(float delta, int CellSize) {
        GetStatic(State);
    }
    public void Reposition(float xdist, float ydist) {
        super.Reposition(xdist, ydist);
        Background.setX(getX());
        Background.setY(getY());
    }
    public void setAnimationSheets(Texture Mushroom, Texture Substrate, int CellSize) {
        super.setAnimationSheets(Mushroom, CellSize);
        TextureRegion[][] tmp = TextureRegion.split(Substrate, CellSize , CellSize);
        numrow = Substrate.getHeight() / CellSize;
        numcol = Substrate.getWidth() / CellSize;
        AnimationRate = 1.0f / (float) numcol;

        for(int i = 0; i < numrow; i++) {
            TextureRegion[] WalkFrames = new TextureRegion[numcol];
            for (int j = 0; j < numcol; j++) {
                WalkFrames[j] = tmp[i][j];
            }
            AnimationSheets.add(new Animation(AnimationRate, WalkFrames));
        }

    }
}
