package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Gameplay implements Screen, InputProcessor{
    static final float scale = 1f;
    static final int CellSize = 64;
    static final int CAM_WIDTH = 640;
    static final int CAM_HEIGHT = 640;
    static TiledMap CurrentMap;
    int tempx, tempy;
    static OrthogonalTiledMapRendererWithSprites MapRenderer;
    Mycoventure GameReference;

    //Initiate Controls
    Sprite ControlsUp;
    Sprite ControlsDown;
    Sprite ControlsLeft;
    Sprite ControlsRight;
    Sprite ControlsUse;
    Sprite ControlsInventory;
    Sprite ControlsCancel;

    //Interfaces
    String CurrentInterface;
    HashMap<String, InterfaceTemplate> Interfaces;

    //Player and Game Data
    Player player;
    GameState CurrentState;
    Vector<Entity>  ObjectsAndCharacters;
    HashMap<String, MushroomReference> MushroomDatabase;

    public Gameplay(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        //Interfaces
        CurrentInterface = "None";
        Interfaces = new HashMap<String, InterfaceTemplate>();
        Interfaces.put("Inventory", new InventoryInterface(this));

        //Set UI controls
        ControlsUp = new Sprite(GameReference.ResourceManager.get("ControlsUp.png", Texture.class));
        ControlsDown = new Sprite(GameReference.ResourceManager.get("ControlsDown.png", Texture.class));
        ControlsLeft = new Sprite(GameReference.ResourceManager.get("ControlsLeft.png", Texture.class));
        ControlsRight = new Sprite(GameReference.ResourceManager.get("ControlsRight.png", Texture.class));
        ControlsUse = new Sprite(GameReference.ResourceManager.get("ControlsUse.png", Texture.class));
        ControlsInventory = new Sprite(GameReference.ResourceManager.get("ControlsInventory.png", Texture.class));
        ControlsCancel = new Sprite(GameReference.ResourceManager.get("ControlsCancel.png", Texture.class));



        //Create Player
        player = new Player(scale);
        player.setAnimationSheets(GameReference.ResourceManager.get("Player.png", Texture.class), CellSize);

        //Begin loading saved data
        if(Gdx.files.isLocalStorageAvailable()) {
            if(Gdx.files.local("PlayerSave.myc").exists()) player.LoadFromSaveFile(new Json().fromJson(PlayerSave.class, Gdx.files.local("PlayerSave.myc").readString()));
            else player.setPosition(15 * CellSize,5 * CellSize);
            if(Gdx.files.local("GameState.myc").exists()) CurrentState = new Json().fromJson(GameState.class, Gdx.files.local("GameState.myc").readString());
        }

        if(CurrentState != null) {
            CurrentMap = GameReference.ResourceManager.get(CurrentState.CurrentMap ,TiledMap.class);
        }
        else {
            CurrentMap = GameReference.ResourceManager.get("Mycofarm.tmx", TiledMap.class); //Default starting location
        }
        //Load Databases
        MushroomDatabase = new HashMap<String, MushroomReference>();
        String MushText[] = Gdx.files.internal("Mushrooms.csv").readString().split("\n");
        for(int i = 1; i < MushText.length; i++) {
            String tmp[] = MushText[i].split(",");
            MushroomReference mref = new MushroomReference();
            mref.Name = tmp[0];
            mref.Examine = tmp[1];
            mref.Group = tmp[2].split(" ");
            mref.BaseYield = Integer.parseInt(tmp[3]);
            mref.BaseSpeed = Integer.parseInt(tmp[4]);
            mref.BaseHumidityPreference = Integer.parseInt(tmp[5]);
            mref.BaseSupplementPreference = Integer.parseInt(tmp[6]);
            mref.BaseEfficiency = Integer.parseInt(tmp[7]);
            mref.BaseTemperatureTrigger = Integer.parseInt(tmp[8].trim());
            MushroomDatabase.put(tmp[0], mref);
        }
        //Set map
        MapRenderer = new OrthogonalTiledMapRendererWithSprites(CurrentMap, 1 / scale);
        //Populate map
        CurrentMap.getLayers().get("Sprites").getObjects().add(player.tmo);
        //Set Camera
        GameReference.cam.setToOrtho(false,CAM_HEIGHT,CAM_WIDTH);
        GameReference.cam.update();
    }

    @Override
    public void render(float delta) {

        if (CurrentInterface.equals("None")) {
            //Check collisions with player
            if (player.IsMoving) {
                if (CheckTerrainCollision("Unwalkable", player, delta)) player.Stop();
                else if (CheckObjectCollision(player, delta)) player.Stop();
                else if (CheckExitEntrance(player, delta) != "")
                    ChangeMap(CheckExitEntrance(player, delta), CurrentMap.getProperties().get("Name").toString());
            }
            player.update(delta, CellSize);

            //Update controls position
            GameReference.cam.position.set(player.getX(), player.getY(), 0);
            ControlsUp.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.12f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            ControlsDown.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.01f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            ControlsLeft.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.01f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            ControlsRight.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.23f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            ControlsUse.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            ControlsInventory.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.89f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);

            //Start drawing
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            GameReference.cam.update();

            //Draw map stuff
            MapRenderer.setView(GameReference.cam);
            MapRenderer.render();

            //Draw non-map stuff
            GameReference.batch.setProjectionMatrix(GameReference.cam.combined);

            GameReference.batch.begin();
            ControlsUp.draw(GameReference.batch);
            ControlsDown.draw(GameReference.batch);
            ControlsLeft.draw(GameReference.batch);
            ControlsRight.draw(GameReference.batch);
            ControlsUse.draw(GameReference.batch);
            ControlsInventory.draw(GameReference.batch);
            GameReference.batch.end();
        }
        else {
            Interfaces.get(CurrentInterface).Update(delta);
            Interfaces.get(CurrentInterface).draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        GameReference.RATIO = width / height;
        GameReference.cam.viewportHeight = CAM_HEIGHT;
        GameReference.cam.viewportWidth = CAM_WIDTH;
        GameReference.cam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        if(Gdx.files.isLocalStorageAvailable()) {
            Json Serializer = new Json();

            //Begin packing game data for saving
            CurrentState = new GameState();
            CurrentState.CurrentMap = CurrentMap.getProperties().get("Name").toString() + ".tmx";
            //Save game state and player data
            Gdx.files.local("PlayerSave.myc").writeString(Serializer.toJson(player.ExportData()),false);
            Gdx.files.local("GameState.myc").writeString(new Json().toJson(CurrentState), false);
        }
    }

    @Override
    public void dispose() {

    }

    /*
    ###############################################################################################


    Input Functions


    ###############################################################################################
     */

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 WorldCoordinates = GameReference.cam.unproject(new Vector3(screenX, screenY, 0));
        if(CurrentInterface.equals("None")) {
            if (ControlsInventory.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                Interfaces.get("Inventory").Show();
            }
        }
        else {
            Interfaces.get(CurrentInterface).touchDown(screenX, screenY, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(CurrentInterface.equals("None")) {
            if (player.IsMoving) player.Stop();
        }
        else {
            Interfaces.get(CurrentInterface).touchUp(screenX, screenY, button);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(CurrentInterface.equals("None")) {
            Vector3 WorldCoordinates = GameReference.cam.unproject(new Vector3(screenX, screenY, 0));
            if (ControlsUp.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                player.IsMoving = true;
                player.Dir = CharacterEntity.UP;
            }
            else if (ControlsDown.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                player.IsMoving = true;
                player.Dir = CharacterEntity.DOWN;
            }
            else if (ControlsRight.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                player.IsMoving = true;
                player.Dir = CharacterEntity.RIGHT;
            }
            else if (ControlsLeft.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                player.IsMoving = true;
                player.Dir = CharacterEntity.LEFT;
            }
        }
        else {
            Interfaces.get(CurrentInterface).touchDragged(screenX, screenY, pointer);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /*
    ###############################################################################################


    My Functions


    ###############################################################################################
     */

    public void ChangeMap(String MapName, String LastMap) {
        CurrentMap.dispose();
        CurrentMap = GameReference.ResourceManager.get(MapName + ".tmx", TiledMap.class);

        //Reposition Player
        CurrentMap.getLayers().get("Sprites").getObjects().add(player.tmo);
        for(MapObject tmp : CurrentMap.getLayers().get("Exits And Entrances").getObjects()) {
            if(tmp.getProperties().get("From").toString().equals(LastMap)) {
                RectangleMapObject rect = (RectangleMapObject)tmp;
                Rectangle r = new Rectangle(rect.getRectangle());
                float xpos = r.x;
                float ypos = r.y;
                if(player.Dir == CharacterEntity.LEFT) xpos -= 1 * CellSize;
                else if(player.Dir == CharacterEntity.RIGHT) xpos += 1 * CellSize;
                else if(player.Dir == CharacterEntity.DOWN) ypos -= 1 * CellSize;
                else if(player.Dir == CharacterEntity.UP) ypos += 1 * CellSize;
                player.setPosition(xpos, ypos);
            }
        }
        //Set Map
        MapRenderer.setMap(CurrentMap);
        MapRenderer.setView(GameReference.cam);
        GameReference.cam.setToOrtho(false,CAM_WIDTH,CAM_HEIGHT);
        GameReference.cam.update();
    }
    public String CheckExitEntrance(CharacterEntity entity, float delta) {
        String MapName = "";
        Rectangle EntityRect = new Rectangle(entity.getBoundingRectangle());
        switch(entity.Dir) {
            case CharacterEntity.LEFT:
                EntityRect.x -= (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * CellSize);
                break;
        }
        for (MapObject tmp : CurrentMap.getLayers().get("Exits And Entrances").getObjects()) {
            if (tmp instanceof RectangleMapObject) {
                RectangleMapObject rect = (RectangleMapObject) tmp;
                Rectangle r = new Rectangle(rect.getRectangle());
                r.set(r.x / scale, r.y / scale, r.width / scale, r.height / scale);
                if (r.overlaps(EntityRect)) {
                    MapName = rect.getProperties().get("To").toString();
                    break;
                }
            }
        }
        return MapName;
    }
    public boolean CheckObjectCollision(CharacterEntity entity, float delta) {
        boolean WillCollide = false;
        Rectangle EntityRect = new Rectangle(entity.getBoundingRectangle());
        switch(entity.Dir) {
            case CharacterEntity.LEFT:
                EntityRect.x -= (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * CellSize);
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * CellSize);
                break;
        }
        for (MapObject tmp : CurrentMap.getLayers().get("Objects").getObjects()) {
            if (tmp instanceof RectangleMapObject) {
                RectangleMapObject rect = (RectangleMapObject) tmp;
                Rectangle r = new Rectangle(rect.getRectangle());
                r.set(r.x / scale, r.y / scale, r.width / scale, r.height / scale);
                if (r.overlaps(EntityRect)) {
                    WillCollide = true;
                    break;
                }
            }
        }
        return WillCollide;
    }
    public boolean CheckTerrainCollision(String TileProperty, CharacterEntity entity, float delta) {
        boolean WillCollide = false;
        Rectangle EntityRect = new Rectangle(entity.getBoundingRectangle());
        TiledMapTileLayer t = (TiledMapTileLayer)CurrentMap.getLayers().get("Terrain");
        switch(entity.Dir) {
            case CharacterEntity.LEFT:
                EntityRect.x -= (delta * entity.MoveSpeed * CellSize);
                if(EntityRect.x > 0) {
                    if (t.getCell((int) Math.floor(EntityRect.x / CellSize), (int) Math.floor(EntityRect.y / CellSize)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * CellSize);
                if(EntityRect.x + EntityRect.width < CurrentMap.getProperties().get("width", Integer.class) * CellSize) {
                    if (t.getCell((int) Math.floor((EntityRect.x + EntityRect.width) / CellSize), (int) Math.floor(EntityRect.y / CellSize)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * CellSize);
                if(EntityRect.y + EntityRect.height < CurrentMap.getProperties().get("height", Integer.class) * CellSize) {
                    if (t.getCell((int) Math.floor(EntityRect.x / CellSize), (int) Math.floor((EntityRect.y + EntityRect.height) / CellSize)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * CellSize);
                if(EntityRect.y > 0) {
                    if (t.getCell((int) Math.floor(EntityRect.x / CellSize), (int) Math.floor(EntityRect.y / CellSize)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
        }
        return WillCollide;
    }

    public void PopulateMap() {
       for(MapObject tmp : CurrentMap.getLayers().get("Spawn Points").getObjects()) {
           if(tmp.getProperties().get("Type").toString().equals("MushroomSource")) {
               RectangleMapObject rect = (RectangleMapObject) tmp;
               Rectangle r = rect.getRectangle();
               float xpos = r.getX() + r.getWidth() * (float)Math.random();
               float ypos = r.getY() + r.getHeight() * (float)Math.random();
               boolean intersect = false;
               Rectangle temprect = new Rectangle();
               temprect.x = xpos;
               temprect.y = ypos;
               temprect.width = CellSize;
               temprect.height
           }
       }
    }
}

