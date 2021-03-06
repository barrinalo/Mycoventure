package com.mycoventure.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Player extends CharacterEntity {

    int Money, Logs, Supplements, Bulkers, Waste;
    HashMap<String, Integer> Mushrooms;
    Vector<Spawn> SpawnAndCultures;
    Vector<Compost> TypesOfCompost;
    Vector<MushroomSource> MyGrowingMushrooms;
    public Player(float scale) {
        super(scale);

        StateTime = 0;
        Money = 0;
        Logs = 0;
        Supplements = 0;
        Bulkers = 0;
        Waste = 0;

        SpawnAndCultures = new Vector<Spawn>();
        TypesOfCompost = new Vector<Compost>();
        MyGrowingMushrooms = new Vector<MushroomSource>();
        Mushrooms = new HashMap<String, Integer>();
    }

    public void LoadFromSaveFile(PlayerSave temp, Gameplay Ref){
        setPosition(temp.Position_X, temp.Position_Y);
        Dir = temp.Direction;

        Money = temp.Money;
        Waste = temp.Waste;
        Bulkers = temp.Bulkers;
        Logs = temp.Logs;
        Supplements = temp.Supplements;
        SpawnAndCultures = temp.SpawnAndCultures;
        Mushrooms = temp.Mushrooms;
        TypesOfCompost = temp.TypesOfCompost;
        Vector<MushroomSave> SaveMushrooms = temp.MyGrowingMushrooms;
        for(MushroomSave m : SaveMushrooms) {
            MushroomSource newMush = new MushroomSource(scale,"","");
            newMush.LoadFromSave(m);
            if(Ref.MushroomDatabase.get(newMush.Name).Loggable.equals("Yes")) newMush.setAnimationSheets(Ref.GameReference.ResourceManager.get(newMush.Name + ".png", Texture.class), Ref.GameReference.ResourceManager.get("LogSubstrate.png", Texture.class), Ref.CellSize);
            else; //Load compost substrate background
            MyGrowingMushrooms.add(newMush);
        }
    }
    public PlayerSave ExportData() {
        PlayerSave temp = new PlayerSave();
        temp.Position_X = getX();
        temp.Position_Y = getY();
        temp.Money = Money;
        temp.Bulkers = Bulkers;
        temp.Logs = Logs;
        temp.Supplements = Supplements;
        temp.Direction = Dir;
        temp.Waste = Waste;
        temp.Mushrooms = Mushrooms;
        temp.SpawnAndCultures = SpawnAndCultures;
        temp.TypesOfCompost = TypesOfCompost;
        Vector<MushroomSave> SaveMushrooms = new Vector<MushroomSave>();
        for(MushroomSource m : MyGrowingMushrooms) SaveMushrooms.add(m.ExportData());
        temp.MyGrowingMushrooms = SaveMushrooms;
        return temp;
    }
    public void update(float delta, int CellSize, TiledMap CurrentMap) {
        Iterator it = TypesOfCompost.iterator();
        while(it.hasNext()) {
            Compost c = (Compost)it.next();
            if(c.Quantity == 0) it.remove();
        }
        it = MyGrowingMushrooms.iterator();
        while(it.hasNext()) {
            MushroomSource m = (MushroomSource)it.next();
            if(m.Location.equals(CurrentMap.getProperties().get("Name").toString() + ".tmx")) {
                m.update(delta, CellSize);
                if (m.Yield == 0 && m.SubstrateRemaining == 0) {
                    CurrentMap.getLayers().get("Sprites").getObjects().remove(m.tmo);
                    CurrentMap.getLayers().get("Sprites").getObjects().remove(m.Background);
                    it.remove();
                }
            }
        }
        it = Mushrooms.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(Integer.parseInt(pair.getValue().toString()) == 0) it.remove();
        }
        while(it.hasNext()) {
            MushroomSource m = (MushroomSource)it.next();
            if(m.Yield == 0 && m.SubstrateRemaining == 0) {
                CurrentMap.getLayers().get("Sprites").getObjects().remove(m.tmo);
                CurrentMap.getLayers().get("Sprites").getObjects().remove(m.Background);
                it.remove();
            }
        }
        if(IsMoving) {
            switch(Dir) {
                case LEFT:
                    Move(-delta * MoveSpeed * CellSize, 0, delta);
                    break;
                case RIGHT:
                    Move(delta * MoveSpeed * CellSize, 0, delta);
                    break;
                case UP:
                    Move(0, delta * MoveSpeed * CellSize, delta);
                    break;
                case DOWN:
                    Move(0, -delta * MoveSpeed * CellSize, delta);
                    break;
            }
        }
        else GetStatic(Dir);
    }
}
