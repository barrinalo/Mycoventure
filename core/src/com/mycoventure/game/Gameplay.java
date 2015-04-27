package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Gameplay implements Screen, InputProcessor{
    static final float scale = 1f;
    static final float ConvertToCell = 64f;
    static final int CAM_WIDTH = 640;
    static final int CAM_HEIGHT = 640;
    static TiledMap CurrentMap;
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

    //Inventory stuff
    boolean Inventory;
    Vector<InventoryEntry> InventoryList;

    //Player and Game Data
    Player player;
    GameState CurrentState;
    public Gameplay(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        //Set UI controls
        Inventory = false;
        ControlsUp = new Sprite(GameReference.ResourceManager.get("ControlsUp.png", Texture.class));
        ControlsDown = new Sprite(GameReference.ResourceManager.get("ControlsDown.png", Texture.class));
        ControlsLeft = new Sprite(GameReference.ResourceManager.get("ControlsLeft.png", Texture.class));
        ControlsRight = new Sprite(GameReference.ResourceManager.get("ControlsRight.png", Texture.class));
        ControlsUse = new Sprite(GameReference.ResourceManager.get("ControlsUse.png", Texture.class));
        ControlsInventory = new Sprite(GameReference.ResourceManager.get("ControlsInventory.png", Texture.class));
        ControlsCancel = new Sprite(GameReference.ResourceManager.get("ControlsCancel.png", Texture.class));

        //Create Inventory
        InventoryList = new Vector<InventoryEntry>();

        //Create Player
        player = new Player(scale);
        player.setWalkSheets(GameReference.ResourceManager.get("Player.png", Texture.class));
        for(int i = 0; i < 1; i++) {
            Compost tmp = new Compost();
            tmp.BulkPercentage = 50f;
            tmp.SupplementPercentage = 50f;
            tmp.Quantity = 1;
            player.TypesOfCompost.add(tmp);
        }
        //Begin loading saved data
        if(Gdx.files.isLocalStorageAvailable()) {
            if(Gdx.files.local("PlayerSave.myc").exists()) player.LoadFromSaveFile(new Json().fromJson(PlayerSave.class, Gdx.files.local("PlayerSave.myc").readString()));
            else player.setPosition(15 * 64,5 * 64);
            if(Gdx.files.local("GameState.myc").exists()) CurrentState = new Json().fromJson(GameState.class, Gdx.files.local("GameState.myc").readString());
        }

        if(CurrentState != null) {
            CurrentMap = GameReference.ResourceManager.get(CurrentState.CurrentMap ,TiledMap.class);
        }
        else {
            CurrentMap = GameReference.ResourceManager.get("Mycofarm.tmx", TiledMap.class); //Default starting location
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
        if (!Inventory) {
            //Check collisions with player
            if (player.IsMoving) {
                if (CheckTerrainCollision("Unwalkable", player, delta)) player.Stop();
                else if (CheckObjectCollision(player, delta)) player.Stop();
                else if (CheckExitEntrance(player, delta) != "")
                    ChangeMap(CheckExitEntrance(player, delta), CurrentMap.getProperties().get("Name").toString());
            }
            player.update(delta * ConvertToCell);

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
            Gdx.gl.glClearColor(204/255f, 0, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            GameReference.batch.setProjectionMatrix(GameReference.cam.combined);
            GameReference.cam.update();

            GameReference.batch.begin();
            for(int i = 0; i < InventoryList.size(); i++) InventoryList.get(i).draw(GameReference.batch);
            ControlsCancel.draw(GameReference.batch);
            GameReference.batch.end();
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
        Vector3 WorldCoordinates = GameReference.cam.unproject(new Vector3(screenX,screenY,0));

        if(ControlsInventory.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y) && !Inventory) {
            GameReference.cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
            GameReference.cam.position.x = 5;
            GameReference.cam.position.y = 0;
            Inventory = true;
            ControlsCancel.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.89f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);

            int pos = -1;
            for(Compost c : player.TypesOfCompost) {
                InventoryList.add(new InventoryEntry(GameReference.ResourceManager.get("ControlsCancel.png", Texture.class), "Compost",
                        "Bulk - " + Float.toString(c.BulkPercentage) + ", Supplement - "
                                + Float.toString(c.SupplementPercentage), c.Quantity, GameReference.ResourceManager));
                InventoryList.lastElement().Format(pos);
                pos--;
            }
            for(Spawn s : player.SpawnAndCultures) {
                if(s.Culture) InventoryList.add(new InventoryEntry(GameReference.ResourceManager.get("Culture.png", Texture.class), s.Name, s.Description, s.Quantity, GameReference.ResourceManager));
                else InventoryList.add(new InventoryEntry(GameReference.ResourceManager.get("Spawn.png", Texture.class), s.Name, s.Description, s.Quantity, GameReference.ResourceManager));
                InventoryList.lastElement().Format(pos);
                pos--;
            }
            Iterator it = player.Mushrooms.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                InventoryList.add(new InventoryEntry(GameReference.ResourceManager.get(pair.getKey() + ".png", Texture.class), pair.getKey().toString(), "", Integer.parseInt(pair.getValue().toString()), GameReference.ResourceManager));
                it.remove();
                InventoryList.lastElement().Format(pos);
                pos--;
            }
        }
        else if(ControlsCancel.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y) && Inventory) {
            Inventory = false;
            InventoryList.clear();
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(player.IsMoving) player.Stop();

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 WorldCoordinates = GameReference.cam.unproject(new Vector3(screenX,screenY,0));
        if(ControlsUp.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.IsMoving = true;
            player.Dir = CharacterEntity.UP;
        }
        if(ControlsDown.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.IsMoving = true;
            player.Dir = CharacterEntity.DOWN;
        }
        if(ControlsRight.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.IsMoving = true;
            player.Dir = CharacterEntity.RIGHT;
        }
        if(ControlsLeft.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.IsMoving = true;
            player.Dir = CharacterEntity.LEFT;
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
                if(player.Dir == CharacterEntity.LEFT) xpos -= 1 * ConvertToCell;
                else if(player.Dir == CharacterEntity.RIGHT) xpos += 1 * ConvertToCell;
                else if(player.Dir == CharacterEntity.DOWN) ypos -= 1 * ConvertToCell;
                else if(player.Dir == CharacterEntity.UP) ypos += 1 * ConvertToCell;
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
                EntityRect.x -= (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * ConvertToCell);
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
                EntityRect.x -= (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * ConvertToCell);
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * ConvertToCell);
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
                EntityRect.x -= (delta * entity.MoveSpeed * ConvertToCell);
                if(EntityRect.x > 0) {
                    if (t.getCell((int) Math.floor(EntityRect.x / ConvertToCell), (int) Math.floor(EntityRect.y / ConvertToCell)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.RIGHT:
                EntityRect.x += (delta * entity.MoveSpeed * ConvertToCell);
                if(EntityRect.x + EntityRect.width < CurrentMap.getProperties().get("width", Integer.class) * ConvertToCell) {
                    if (t.getCell((int) Math.floor((EntityRect.x + EntityRect.width) / ConvertToCell), (int) Math.floor(EntityRect.y / ConvertToCell)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.UP:
                EntityRect.y += (delta * entity.MoveSpeed * ConvertToCell);
                if(EntityRect.y + EntityRect.height < CurrentMap.getProperties().get("height", Integer.class) * ConvertToCell) {
                    if (t.getCell((int) Math.floor(EntityRect.x / ConvertToCell), (int) Math.floor((EntityRect.y + EntityRect.height) / ConvertToCell)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
            case CharacterEntity.DOWN:
                EntityRect.y -= (delta * entity.MoveSpeed * ConvertToCell);
                if(EntityRect.y > 0) {
                    if (t.getCell((int) Math.floor(EntityRect.x / ConvertToCell), (int) Math.floor(EntityRect.y / ConvertToCell)).getTile().getProperties().containsKey(TileProperty))WillCollide = true;
                }
                else WillCollide = true;
                break;
        }
        return WillCollide;
    }
}

