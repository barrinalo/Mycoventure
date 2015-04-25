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
public class Player extends Sprite {

    int Money, Logs, Supplements, Bulkers, Waste, numcol, numrow;
    float MoveSpeed, scale;
    boolean IsMoving;
    String Direction;
    float StateTime;
    TextureMapObject tmo;

    HashMap<String, Integer> Mushrooms;
    HashMap<String, Animation> WalkSheets;
    Vector<Spawn> SpawnAndCultures;

    public Player(AssetManager ResourceManager, float s) {
        setBounds(15, 5, 1, 1);
        setOriginCenter();
        Direction = "Down";
        StateTime = 0;
        Money = 0;
        Logs = 0;
        Supplements = 0;
        Bulkers = 0;
        Waste = 0;
        MoveSpeed = 1 / 6f;
        scale = s;
        IsMoving = false;

        SpawnAndCultures = new Vector<Spawn>();
        Mushrooms = new HashMap<String, Integer>();
        WalkSheets = new HashMap<String, Animation>();

        numcol = ResourceManager.get("Player.png",Texture.class).getWidth() / (int)s;
        numrow = ResourceManager.get("Player.png",Texture.class).getHeight() / (int)s;

        TextureRegion[][] tmp = TextureRegion.split(ResourceManager.get("Player.png",Texture.class), (int)s , (int)s);

        for(int i = 0; i < numrow; i++){
            TextureRegion[] WalkFrames = new TextureRegion[numcol];
            for(int j = 0; j < numcol; j++){
                WalkFrames[j] = tmp[i][j];
            }
            if(i == 0) WalkSheets.put("Left", new Animation(MoveSpeed, WalkFrames));
            if(i == 1) WalkSheets.put("Right", new Animation(MoveSpeed, WalkFrames));
            if(i == 2) WalkSheets.put("Up", new Animation(MoveSpeed, WalkFrames));
            if(i == 3) WalkSheets.put("Down", new Animation(MoveSpeed, WalkFrames));
        }

        tmo = new TextureMapObject();
        tmo.setTextureRegion(WalkSheets.get(Direction).getKeyFrame(StateTime));
        tmo.setX(getX());
        tmo.setY(getY());
        tmo.setName("Player");
    }

    public boolean LoadFromSaveFile(){
        return false;
    }
    public void Move(String dir) {
        Direction = dir;
        IsMoving = true;
    }
    public void Stop() {
        IsMoving = false;
        StateTime = 0;
    }
    public void update(float delta) {
        if(IsMoving) {
            StateTime += delta;
            if(StateTime > numcol * MoveSpeed) StateTime = 0;
            if(Direction == "Left") {
                translate(-delta, 0);
                tmo.setX(getX());
                tmo.setY(getY());
            }
            else if(Direction == "Right") {
                translate(delta, 0);
                tmo.setX(getX());
                tmo.setY(getY());
            }
            else if(Direction == "Up") {
                translate(0, delta);
                tmo.setX(getX());
                tmo.setY(getY());
            }
            else if(Direction == "Down") {
                translate(0, -delta);
                tmo.setX(getX());
                tmo.setY(getY());
            }
        }
        tmo.setTextureRegion(WalkSheets.get(Direction).getKeyFrame(StateTime));
    }
}
