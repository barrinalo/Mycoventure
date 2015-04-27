package com.mycoventure.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Player extends CharacterEntity {

    int Money, Logs, Supplements, Bulkers, Waste;
    HashMap<String, Integer> Mushrooms;
    Vector<Spawn> SpawnAndCultures;
    Vector<Compost> TypesOfCompost;

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
        Mushrooms = new HashMap<String, Integer>();
    }

    public void LoadFromSaveFile(PlayerSave temp){
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

        return temp;
    }
    public void update(float delta, int CellSize) {
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
