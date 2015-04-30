package com.mycoventure.game;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/27.
 */
public class PlayerSave {
    public float Position_X, Position_Y;
    int Money, Logs, Supplements, Bulkers, Waste, Direction;
    HashMap<String, Integer> Mushrooms;
    Vector<Spawn> SpawnAndCultures;
    Vector<Compost> TypesOfCompost;
    Vector<MushroomSave> MyGrowingMushrooms;

}
