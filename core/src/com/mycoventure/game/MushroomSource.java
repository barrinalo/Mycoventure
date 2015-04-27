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
    }
    @Override
    public void Collect(Player p) {
        if(p.Mushrooms.containsKey(Name)) p.Mushrooms.replace(Name, p.Mushrooms.get(Name).intValue() + Yield);
        else p.Mushrooms.put("Name", Yield);
    }

    @Override
    public String Examine() {
        return ExamineInfo;
    }
}
