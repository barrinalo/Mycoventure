package com.mycoventure.game;

/**
 * Created by David on 27/04/2015.
 */
public class MushroomSource extends AnimatedEntity implements Collectible, Examinable {
    int Yield;
    int Efficiency, Speed, SupplementPreference, HumidityPreference, TemperatureTrigger;
    int ColonisationPercentage, SubstrateRemaining;
    String Name, ExamineInfo;

    public MushroomSource(float scale, String Name, String ExamineInfo) {
        super(scale);
        this.Name = Name;
        this.ExamineInfo = ExamineInfo;
        SubstrateRemaining = 0;
        Efficiency = 0;
        Speed = 0;
        SupplementPreference = 0;
        HumidityPreference = 0;
        TemperatureTrigger = 0;
        ColonisationPercentage = 0;
        Yield = 0;
    }
    @Override
    public void Collect(Player p) {
        if(p.Mushrooms.containsKey(Name)) p.Mushrooms.put(Name, p.Mushrooms.get(Name).intValue() + Yield);
        else p.Mushrooms.put(Name, Yield);
        Yield = 0;
    }

    @Override
    public String Examine() {
        return ExamineInfo;
    }

    @Override
    public void update(float delta, int CellSize) {
        GetStatic(0);
    }
}
