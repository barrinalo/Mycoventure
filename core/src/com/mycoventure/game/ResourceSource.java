package com.mycoventure.game;

/**
 * Created by david.chong on 2015/04/29.
 */
public class ResourceSource extends AnimatedEntity implements Collectible, Examinable{
    String Name, ExamineInfo;
    int Money, Waste, Logs, Bulkers, Supplements;
    public ResourceSource(float scale, String Name, String ExamineInfo) {
        super(scale);
        Money = 0;
        Waste = 0;
        Logs = 0;
        Bulkers = 0;
        Supplements = 0;
        this.Name = Name;
        this.ExamineInfo = ExamineInfo;
    }

    @Override
    public void Collect(Player p) {
        p.Money += Money;
        p.Waste += Waste;
        p.Logs += Logs;
        p.Bulkers += Bulkers;
        p.Supplements += Supplements;

        Money = 0;
        Waste = 0;
        Logs = 0;
        Bulkers = 0;
        Supplements = 0;
    }

    @Override
    public String Examine() {
        return ExamineInfo;
    }

    public boolean Exhausted() {
        if(Bulkers == 0 && Waste == 0 && Money == 0 && Logs == 0 && Supplements == 0) return true;
        else return false;
    }
}
