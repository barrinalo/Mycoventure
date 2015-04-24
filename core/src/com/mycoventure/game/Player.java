package com.mycoventure.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Player extends Sprite {

    static int Money, Logs, Supplements, Bulkers, Waste;
    static int MoveSpeed;
    static String Direction;
    static float StateTime;

    HashMap<String, Integer> Mushrooms;
    HashMap<String, TextureRegion[]> WalkSheets;
    Vector<Spawn> SpawnAndCultures;

    public Player(AssetManager ResourceManager) {
        StateTime = 0;
        Money = 0;
        Logs = 0;
        Supplements = 0;
        Bulkers = 0;
        Waste = 0;
        MoveSpeed = 1;

        SpawnAndCultures = new Vector<Spawn>();
        Mushrooms = new HashMap<String, Integer>();
        WalkSheets = new HashMap<String, TextureRegion[]>();

        TextureRegion[][] tmp = TextureRegion.split(ResourceManager.get("PlayerLeft.png",Texture.class), ResourceManager.get("PlayerLeft.png",Texture.class).getWidth() / 4, 1);
        TextureRegion[] WalkFrames = new TextureRegion[4];
        for(int i = 0; i < 1; i++){
            for(int j = 0; j < 4; j++){
                WalkFrames[j] = tmp[i][j];
            }
        }
        WalkSheets.put("Left", WalkFrames);

        tmp = TextureRegion.split(ResourceManager.get("PlayerRight.png",Texture.class), ResourceManager.get("PlayerRight.png",Texture.class).getWidth() / 4, 1);
        WalkFrames = new TextureRegion[4];
        for(int i = 0; i < 1; i++){
            for(int j = 0; j < 4; j++){
                WalkFrames[j] = tmp[i][j];
            }
        }
        WalkSheets.put("Right", WalkFrames);

        tmp = TextureRegion.split(ResourceManager.get("PlayerUp.png",Texture.class), ResourceManager.get("PlayerUp.png",Texture.class).getWidth() / 4, 1);
        WalkFrames = new TextureRegion[4];
        for(int i = 0; i < 1; i++){
            for(int j = 0; j < 4; j++){
                WalkFrames[j] = tmp[i][j];
            }
        }
        WalkSheets.put("Up", WalkFrames);

        tmp = TextureRegion.split(ResourceManager.get("PlayerDown.png",Texture.class), ResourceManager.get("PlayerDown.png",Texture.class).getWidth() / 4, 1);
        WalkFrames = new TextureRegion[4];
        for(int i = 0; i < 1; i++){
            for(int j = 0; j < 4; j++){
                WalkFrames[j] = tmp[i][j];
            }
        }
        WalkSheets.put("Down", WalkFrames);

    }

    public boolean LoadFromSaveFile(){
        return false;
    }

    @Override
    public void draw(Batch batch, float delta) {
        StateTime += delta;
        if(StateTime > 1) StateTime = 0;
        int frame = (int) Math.floor(4 * StateTime);
        batch.draw(WalkSheets.get(Direction)[frame], super.getX(), super.getY(), super.getWidth(), super.getHeight());
    }
}
