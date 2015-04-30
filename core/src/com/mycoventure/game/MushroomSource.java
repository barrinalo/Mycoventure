package com.mycoventure.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;

/**
 * Created by David on 27/04/2015.
 */
public class MushroomSource extends AnimatedEntity implements Collectible, Examinable, Sample {
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
    int Efficiency, Speed, SupplementPreference, HumidityPreference, TemperatureTrigger, BaseYield;
    int ColonisationPercentage, SubstrateRemaining, OriginalSubstrate;
    int State;
    String Name, ExamineInfo, Location;
    boolean isLog;
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
        if(State == FINAL_FRUITS) {
            if (p.Mushrooms.containsKey(Name))
                p.Mushrooms.put(Name, p.Mushrooms.get(Name).intValue() + Yield);
            else p.Mushrooms.put(Name, Yield);
            SubstrateRemaining -= (10 - Efficiency);
            if (SubstrateRemaining < 0) {
                p.Waste += OriginalSubstrate;
                SubstrateRemaining = 0;
            }
            if(isLog) State = STAGE_0;
            else State = STAGE_100;
            Yield = 0;
        }
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
                return "I can see bumps forming on the surface!";
            case DEAD:
                return "Lifeless";
            default:
                return "";
        }
    }

    @Override
    public void update(float delta, int CellSize) {
        if(Yield != 0) State = FINAL_FRUITS;

        GetStatic(State);
        Background.setX(getX());
        Background.setY(getY());
        if(ColonisationPercentage == 100 && !isLog) Background.setTextureRegion(AnimationSheets.get(SUBSTRATE_COLONIZED).getKeyFrame(StateTime));
        else if(OriginalSubstrate == 0) Background.setTextureRegion(AnimationSheets.get(STAGE_0).getKeyFrame(StateTime));
        else Background.setTextureRegion(AnimationSheets.get(SUBSTRATE).getKeyFrame(StateTime));
    }
    public void GetStatic() {
        GetStatic(State);
        Background.setX(getX());
        Background.setY(getY());
        if(ColonisationPercentage == 100 && !isLog) Background.setTextureRegion(AnimationSheets.get(SUBSTRATE_COLONIZED).getKeyFrame(StateTime));
        else if(OriginalSubstrate == 0) Background.setTextureRegion(AnimationSheets.get(STAGE_0).getKeyFrame(StateTime));
        else Background.setTextureRegion(AnimationSheets.get(SUBSTRATE).getKeyFrame(StateTime));
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

    public MushroomSave ExportData() {
        MushroomSave m = new MushroomSave();
        m.ColonisationPercentage = ColonisationPercentage;
        m.Efficiency = Efficiency;
        m.ExamineInfo = ExamineInfo;
        m.HumidityPreference = HumidityPreference;
        m.isLog = isLog;
        m.Location = Location;
        m.Name = Name;
        m.OriginalSubstrate = OriginalSubstrate;
        m.Speed = Speed;
        m.State = State;
        m.SubstrateRemaining = SubstrateRemaining;
        m.SupplementPreference = SupplementPreference;
        m.TemperatureTrigger = TemperatureTrigger;
        m.Yield = Yield;
        m.xpos = getX();
        m.ypos = getY();
        m.BaseYield = BaseYield;
        return m;
    }
    public void LoadFromSave(MushroomSave m) {
        ColonisationPercentage = m.ColonisationPercentage;
        Efficiency = m.Efficiency;
        ExamineInfo = m.ExamineInfo;
        HumidityPreference = m.HumidityPreference;
        isLog = m.isLog;
        Location = m.Location;
        Name = m.Name;
        OriginalSubstrate = m.OriginalSubstrate;
        Speed = m.Speed;
        State = m.State;
        SubstrateRemaining = m.SubstrateRemaining;
        SupplementPreference = m.SupplementPreference;
        TemperatureTrigger = m.TemperatureTrigger;
        BaseYield = m.BaseYield;
        Yield = m.Yield;
        setPosition(m.xpos,m.ypos);
    }

    @Override
    public void Sample(Player p, Gameplay Ref) {
        if(OriginalSubstrate != 0) {
            Spawn tmp = new Spawn();
            tmp.Name = Name;
            tmp.Description = ExamineInfo;
            tmp.Efficiency = Efficiency;
            tmp.Culture = true;
            tmp.HumidityPreference = HumidityPreference;
            tmp.Speed = Speed;
            tmp.TemperatureTrigger = TemperatureTrigger;
            tmp.Yield = BaseYield;
            tmp.SupplementPreference = SupplementPreference;
        }
        else {

        }
    }
}
