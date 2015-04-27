package com.mycoventure.game;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Spawn {
    String Name, Description;
    int Quantity;
    boolean Culture;

    public Spawn(){};
    public Spawn(String Name, int Quantity, boolean Culture, String Description) {
        this.Name = Name;
        this.Quantity = Quantity;
        this.Culture = Culture;
        this.Description = Description;
    }
    //Include growth stats here

}
