package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

import java.util.Arrays;
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
    public static final float DROUGHT = 0.1f;
    public static final float NORMAL = 0.5f;
    public static final float RAINING = 0.6f;
    public static final float AFTER_RAIN = 0.9f;

    public static final float ONE_DAY = 300;
    public static final float WORLD_UPDATE_INTERVAL = 60;
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
    Sprite ControlsExaminable;
    Sprite ControlsPlant;

    //Interfaces
    Label ExamineNote;
    String CurrentInterface;
    HashMap<String, InterfaceTemplate> Interfaces;

    //Player and Game Data
    Player player;
    GameState CurrentState;
    Vector<Entity>  ObjectsAndCharacters;
    HashMap<String, MushroomReference> MushroomDatabase;
    HashMap<String, ResourceReference> ResourceDatabase;
    ShapeRenderer s;

    public Gameplay(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        s = new ShapeRenderer();
        Gdx.input.setInputProcessor(this);
        //Interfaces
        CurrentInterface = "None";
        Interfaces = new HashMap<String, InterfaceTemplate>();
        Interfaces.put("Inventory", new InventoryInterface(this));
        Interfaces.put("Planting", new PlantingInterface(this));
        ExamineNote = new Label("", new Label.LabelStyle(GameReference.ResourceManager.get("SmallFont", BitmapFont.class), Color.RED));
        ExamineNote.setWrap(true);
        ExamineNote.setWidth(CAM_WIDTH * 0.5f);
        ExamineNote.setAlignment(Align.bottom);

        //Set UI controls
        ControlsUp = new Sprite(GameReference.ResourceManager.get("ControlsUp.png", Texture.class));
        ControlsDown = new Sprite(GameReference.ResourceManager.get("ControlsDown.png", Texture.class));
        ControlsLeft = new Sprite(GameReference.ResourceManager.get("ControlsLeft.png", Texture.class));
        ControlsRight = new Sprite(GameReference.ResourceManager.get("ControlsRight.png", Texture.class));
        ControlsUse = new Sprite(GameReference.ResourceManager.get("ControlsUse.png", Texture.class));
        ControlsInventory = new Sprite(GameReference.ResourceManager.get("ControlsInventory.png", Texture.class));
        ControlsCancel = new Sprite(GameReference.ResourceManager.get("ControlsCancel.png", Texture.class));
        ControlsExaminable = new Sprite(GameReference.ResourceManager.get("ControlsExaminable.png", Texture.class));
        ControlsPlant = new Sprite(GameReference.ResourceManager.get("ControlsPlant.png", Texture.class));

        //Create Player
        player = new Player(scale);
        player.setAnimationSheets(GameReference.ResourceManager.get("Player.png", Texture.class), CellSize);
        player.SetVisionWhenLookingUp(CellSize, CellSize / 4);

        //Load Databases
        MushroomDatabase = new HashMap<String, MushroomReference>();
        ResourceDatabase = new HashMap<String, ResourceReference>();
        String MushText[] = Gdx.files.internal("Mushrooms.csv").readString().split("\n");
        for(int i = 1; i < MushText.length; i++) {
            String tmp[] = MushText[i].split(",");
            MushroomReference mref = new MushroomReference();
            mref.Name = tmp[0];
            mref.Examine = tmp[1];
            mref.Group = tmp[2].split(" ");
            mref.BaseYield = Integer.parseInt(tmp[3].trim());
            mref.BaseSpeed = Integer.parseInt(tmp[4].trim());
            mref.BaseHumidityPreference = Integer.parseInt(tmp[5].trim());
            mref.BaseSupplementPreference = Integer.parseInt(tmp[6].trim());
            mref.BaseEfficiency = Integer.parseInt(tmp[7].trim());
            mref.BaseTemperatureTrigger = Integer.parseInt(tmp[8].trim());
            mref.Rarity = Integer.parseInt(tmp[9]);
            mref.Loggable = tmp[10].trim();
            MushroomDatabase.put(tmp[0], mref);
        }
        String ResourceText [] = Gdx.files.internal("Resources.csv").readString().split("\n");
        for(int i = 1; i < ResourceText.length; i++) {
            String tmp[] = ResourceText[i].split(",");
            ResourceReference rref = new ResourceReference();
            rref.Name = tmp[0];
            rref.Type = tmp[1];
            rref.Examine = tmp[2].trim();
            ResourceDatabase.put(tmp[0], rref);
        }

        //Begin loading saved data
        if(Gdx.files.isLocalStorageAvailable()) {
            if(Gdx.files.local("PlayerSave.myc").exists()) player.LoadFromSaveFile(new Json().fromJson(PlayerSave.class, Gdx.files.local("PlayerSave.myc").readString()), this);
            else player.setPosition(15 * CellSize,5 * CellSize);
            if(Gdx.files.local("GameState.myc").exists()) CurrentState = new Json().fromJson(GameState.class, Gdx.files.local("GameState.myc").readString());
        }

        if(CurrentState != null) {
            CurrentMap = GameReference.ResourceManager.get(CurrentState.CurrentMap ,TiledMap.class);
        }
        else {
            CurrentMap = GameReference.ResourceManager.get("Mycofarm.tmx", TiledMap.class); //Default starting location
            //Set world parameters default
            CurrentState = new GameState();
            CurrentState.CurrentMap = "Mycofarm.tmx";
            CurrentState.CurrentTime = 0;
            CurrentState.LastUpdate = 0;
            CurrentState.MushroomSpawnChance = NORMAL;
            CurrentState.ResourceSpawnChance = NORMAL;
            CurrentState.CurrentTemperature = 20f;
        }

        //Prepare holder for sprites
        ObjectsAndCharacters = new Vector<Entity>();

        //Set map
        MapRenderer = new OrthogonalTiledMapRendererWithSprites(CurrentMap, 1 / scale);

        //Populate Map
        ChangeMap(CurrentState.CurrentMap.substring(0, CurrentState.CurrentMap.length()-4), "");

        //Set Camera
        GameReference.cam.setToOrtho(false, CAM_HEIGHT, CAM_WIDTH);
        GameReference.cam.update();
    }

    @Override
    public void render(float delta) {

        if (CurrentInterface.equals("None")) {
            UpdateWorld(delta);

            //Update Objects and Characters
            Iterator Objit = ObjectsAndCharacters.iterator();
            while(Objit.hasNext()) {
                Entity e = (Entity)Objit.next();
                if(e instanceof MushroomSource) {
                    MushroomSource m = (MushroomSource) e;
                    m.update(delta, CellSize);
                    if(m.Yield == 0 && m.SubstrateRemaining == 0) {
                        CurrentMap.getLayers().get("Sprites").getObjects().remove(m.tmo);
                        CurrentMap.getLayers().get("Sprites").getObjects().remove(m.Background);
                        Objit.remove();
                    }
                }
                else if(e instanceof ResourceSource) {
                    ResourceSource r = (ResourceSource) e;
                    r.update(delta, CellSize);
                    if(r.Exhausted()) {
                        CurrentMap.getLayers().get("Sprites").getObjects().remove(e.tmo);
                        Objit.remove();
                    }
                }
            }

            //Check collisions with player
            if (player.IsMoving) {
                if (CheckTerrainCollision("Unwalkable", player, delta)) player.Stop();
                else if (CheckObjectCollision(player, delta)) player.Stop();
                else if(CheckSpriteCollision(player, delta)) player.Stop();
                else if (CheckExitEntrance(player, delta) != "")
                    ChangeMap(CheckExitEntrance(player, delta), CurrentMap.getProperties().get("Name").toString());
            }
            player.update(delta, CellSize, CurrentMap);

            //Update controls position
            UpdateControls(delta);

            //Start drawing
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            GameReference.cam.update();

            //Draw map stuff
            MapRenderer.setView(GameReference.cam);
            MapRenderer.render();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            //Draw translucent rect for ambience and night day
            s.setProjectionMatrix(GameReference.cam.combined);
            s.begin(ShapeRenderer.ShapeType.Filled);
            s.setColor(new Color(0,0,0,0.75f * (float)Math.sin((double)(CurrentState.CurrentTime / 300) * Math.PI)));
            s.rect(GameReference.cam.position.x - CAM_WIDTH / 2f, GameReference.cam.position.y - CAM_HEIGHT / 2f, CAM_WIDTH, CAM_HEIGHT );
            s.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            //Draw non-map stuff
            GameReference.batch.setProjectionMatrix(GameReference.cam.combined);

            GameReference.batch.begin();
            ControlsUp.draw(GameReference.batch);
            ControlsDown.draw(GameReference.batch);
            ControlsLeft.draw(GameReference.batch);
            ControlsRight.draw(GameReference.batch);
            ControlsUse.draw(GameReference.batch);
            ControlsInventory.draw(GameReference.batch);
            ControlsExaminable.draw(GameReference.batch);
            ControlsPlant.draw(GameReference.batch);
            ExamineNote.draw(GameReference.batch, 1);
            GameReference.batch.end();

            s.setProjectionMatrix(GameReference.cam.combined);
            s.begin(ShapeRenderer.ShapeType.Line);
            s.rect(player.getX(),player.getY(),player.getWidth(),player.getHeight());
            for(MushroomSource m : player.MyGrowingMushrooms) {
                if(m.Location.equals(CurrentState.CurrentMap)) s.rect(m.getX(),m.getY(),m.getWidth(),m.getHeight());
            }
            s.end();
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
        ExamineNote.setText(""); // Clear examine upon some other action.
        if(CurrentInterface.equals("None")) {
            if (ControlsInventory.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                Interfaces.get("Inventory").Show();
            }
            else if(ControlsUse.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                for(Entity e : ObjectsAndCharacters) {
                    if(e instanceof Collectible && e.getBoundingRectangle().overlaps(player.Vision)) {
                        Collectible collectible = (Collectible)e;
                        collectible.Collect(player);
                    }
                }
                for(MushroomSource m : player.MyGrowingMushrooms) {
                    if(m.Location.equals(CurrentState.CurrentMap) && m.getBoundingRectangle().overlaps(player.Vision)) {
                        m.Collect(player);
                    }
                }
            }
            else if(ControlsExaminable.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                for(Entity e : ObjectsAndCharacters) {
                    if(e instanceof Examinable && e.getBoundingRectangle().overlaps(player.Vision)) {
                        Examinable examinable = (Examinable)e;
                        ExamineNote.setText(examinable.Examine());
                        ExamineNote.setPosition(player.getX() - CAM_WIDTH / 4 + player.getWidth() / 2, player.getY() + player.getHeight());
                    }
                }
                for(MushroomSource m : player.MyGrowingMushrooms) {
                    if(m.Location.equals(CurrentState.CurrentMap) && m.getBoundingRectangle().overlaps(player.Vision)) {
                        ExamineNote.setText(m.Examine());
                        ExamineNote.setPosition(player.getX() - CAM_WIDTH / 4 + player.getWidth() / 2, player.getY() + player.getHeight());
                    }
                }
            }
            else if(ControlsPlant.getBoundingRectangle().contains(WorldCoordinates.x, WorldCoordinates.y)) {
                Rectangle temp = new Rectangle();
                temp.x = player.getX();
                temp.y = player.getY();
                temp.width = CellSize;
                temp.height = CellSize;
                switch(player.Dir) {
                    case CharacterEntity.LEFT:
                        temp.x -= CellSize;
                        break;
                    case CharacterEntity.RIGHT:
                        temp.x += CellSize;
                        break;
                    case CharacterEntity.UP:
                        temp.y += CellSize;
                        break;
                    case CharacterEntity.DOWN:
                        temp.y -= CellSize;
                        break;
                }
                if(CheckStaticObjCollision(temp)) {
                    PlantingInterface p = (PlantingInterface)Interfaces.get("Planting");
                    p.Show(temp);
                }
                else {
                    ExamineNote.setText("I can't plant there");
                    ExamineNote.setPosition(player.getX() - CAM_WIDTH / 4 + player.getWidth() / 2, player.getY() + player.getHeight());
                }
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
        //Clear Objects and Sprites and Map
        if(!LastMap.equals("")) {
            for (Entity e : ObjectsAndCharacters)
                CurrentMap.getLayers().get("Sprites").getObjects().remove(e.tmo);
            ObjectsAndCharacters.clear();
            CurrentMap.dispose();
        }
        //Change to new map
        CurrentMap = GameReference.ResourceManager.get(MapName + ".tmx", TiledMap.class);
        CurrentState.CurrentMap = MapName + ".tmx";

        if(!LastMap.equals("")) {
            //Reposition Player
            CurrentMap.getLayers().get("Sprites").getObjects().add(player.tmo);
            for (MapObject tmp : CurrentMap.getLayers().get("Exits And Entrances").getObjects()) {
                if (tmp.getProperties().get("From").toString().equals(LastMap)) {
                    RectangleMapObject rect = (RectangleMapObject) tmp;
                    Rectangle r = new Rectangle(rect.getRectangle());
                    String dir = rect.getProperties().get("Facing").toString();
                    float xpos = (float) Math.floor(r.x / CellSize) * CellSize;
                    float ypos = (float) Math.floor(r.y / CellSize) * CellSize;
                    if (dir.equals("Left")) {
                        xpos -= 1 * CellSize;
                        player.Dir = Player.LEFT;
                    } else if (dir.equals("Right")) {
                        xpos += 1 * CellSize;
                        player.Dir = Player.RIGHT;
                    } else if (dir.equals("Down")) {
                        ypos -= 1 * CellSize;
                        player.Dir = Player.DOWN;
                    } else if (dir.equals("Up")) {
                        ypos += 1 * CellSize;
                        player.Dir = Player.UP;
                    }
                    player.setPosition(xpos, ypos);
                }
            }
        }
        else CurrentMap.getLayers().get("Sprites").getObjects().add(player.tmo);
        //Spawn Stuff
        //SpawnMushroomsAndResources();

        //Add sprites to map
        for(Entity e : ObjectsAndCharacters) CurrentMap.getLayers().get("Sprites").getObjects().add(e.tmo);
        for(MushroomSource m : player.MyGrowingMushrooms) {
            if (m.Location.equals(CurrentState.CurrentMap)) {
                CurrentMap.getLayers().get("Sprites").getObjects().add(m.Background);
                CurrentMap.getLayers().get("Sprites").getObjects().add(m.tmo);
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
    public boolean CheckSpriteCollision(CharacterEntity entity, float delta) {
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
        for(Entity e : ObjectsAndCharacters) {
            if(e.getBoundingRectangle().overlaps(EntityRect) && e.Blocking) WillCollide = true;
            else if(e.getBoundingRectangle().overlaps(EntityRect) && e.Blocking && e.IsMovable) {
                //do further check here for moving objects
            }
        }
        return WillCollide;
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
        for (MushroomSource m : player.MyGrowingMushrooms) {
            if(CurrentState.CurrentMap.equals(m.Location)) {
                if(m.getBoundingRectangle().overlaps(EntityRect)) {
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

    public boolean CheckStaticObjCollision(Rectangle temp) {
        boolean Plantable = true;
        TiledMapTileLayer t = (TiledMapTileLayer)CurrentMap.getLayers().get("Terrain");
        if(t.getCell((int)(temp.x / CellSize),(int)(temp.y / CellSize)).getTile().getProperties().containsKey("Unwalkable")) Plantable = false;

        if(Plantable) {
            for(MapObject obj : CurrentMap.getLayers().get("Objects").getObjects()) {
                if(obj instanceof RectangleMapObject) {
                    RectangleMapObject robj = (RectangleMapObject)obj;
                    if(robj.getRectangle().overlaps(temp)) {
                        Plantable = false;
                        break;
                    }
                }
            }
        }

        if(Plantable) {
            for(Entity e : ObjectsAndCharacters) {
                if(e.getBoundingRectangle().overlaps(temp)) {
                    Plantable = false;
                    break;
                }
            }
        }

        if(Plantable) {
            for(MapObject obj : CurrentMap.getLayers().get("Exits And Entrances").getObjects()) {
                if(obj instanceof RectangleMapObject) {
                    RectangleMapObject robj = (RectangleMapObject)obj;
                    if(robj.getRectangle().overlaps(temp)) {
                        Plantable = false;
                        break;
                    }
                }
            }
        }

        if(Plantable) {
            for(MushroomSource m : player.MyGrowingMushrooms) {
                if(m.getBoundingRectangle().overlaps(temp)) {
                    Plantable = false;
                    break;
                }
            }
        }
        return Plantable;
    }

    public void SpawnMushroomsAndResources() {
       for(MapObject tmp : CurrentMap.getLayers().get("Spawn Points").getObjects()) {
           if(tmp.getProperties().get("Type").toString().equals("ResourceSource")) {
                if(Math.random() < CurrentState.ResourceSpawnChance) {
                    RectangleMapObject rect = (RectangleMapObject) tmp;
                    Rectangle r = rect.getRectangle();
                    float xpos = r.getX() + r.getWidth() * (float) Math.random();
                    float ypos = r.getY() + r.getHeight() * (float) Math.random();
                    boolean intersect = false;
                    Rectangle temprect = new Rectangle();
                    temprect.x = xpos;
                    temprect.y = ypos;
                    temprect.width = CellSize;
                    temprect.height = CellSize;
                    for (Entity e : ObjectsAndCharacters) {
                        if (e.getBoundingRectangle().overlaps(temprect)) {
                            intersect = true;
                            break;
                        }
                    }
                    if(!intersect) {
                        String resourcename = rect.getProperties().get("Name").toString();
                        ResourceSource tempr = new ResourceSource(scale, resourcename, ResourceDatabase.get(resourcename).Examine);
                        tempr.setAnimationSheets(GameReference.ResourceManager.get(resourcename + ".png", Texture.class), CellSize);
                        if(rect.getProperties().containsKey("Money")) tempr.Money = (int)(Integer.parseInt(rect.getProperties().get("Money").toString()) * Math.random() + 1);
                        if(rect.getProperties().containsKey("Bulkers")) tempr.Bulkers = (int)(Integer.parseInt(rect.getProperties().get("Bulkers").toString()) * Math.random() + 1);
                        if(rect.getProperties().containsKey("Supplements")) tempr.Supplements = (int)(Integer.parseInt(rect.getProperties().get("Supplements").toString()) * Math.random() + 1);
                        if(rect.getProperties().containsKey("Waste")) tempr.Waste = (int)(Integer.parseInt(rect.getProperties().get("Waste").toString()) * Math.random() + 1);
                        if(rect.getProperties().containsKey("Logs")) tempr.Logs = (int)(Integer.parseInt(rect.getProperties().get("Logs").toString()) * Math.random() + 1);
                        tempr.setBounds(xpos, ypos, CellSize, CellSize);
                        tempr.GetStatic(0);
                        ObjectsAndCharacters.add(tempr);
                        CurrentMap.getLayers().get("Sprites").getObjects().add(tempr.tmo);
                    }
                }
           }
           else if(tmp.getProperties().get("Type").toString().equals("MushroomSource")) {
               if (Math.random() < CurrentState.MushroomSpawnChance) {
                   RectangleMapObject rect = (RectangleMapObject) tmp;
                   Rectangle r = rect.getRectangle();
                   float xpos = r.getX() + r.getWidth() * (float) Math.random();
                   float ypos = r.getY() + r.getHeight() * (float) Math.random();
                   boolean intersect = false;
                   Rectangle temprect = new Rectangle();
                   temprect.x = xpos;
                   temprect.y = ypos;
                   temprect.width = CellSize;
                   temprect.height = CellSize;
                   for (Entity e : ObjectsAndCharacters) {
                       if (e.getBoundingRectangle().overlaps(temprect)) {
                           intersect = true;
                           break;
                       }
                   }
                   //if chosen spawn point does not intersect with other objects/characters create mushroom source
                   if (!intersect) {
                       String[] Groups = rect.getProperties().get("Group").toString().split(" ");
                       Vector<MushroomReference> spawnlist = new Vector<MushroomReference>();
                       Iterator it = MushroomDatabase.entrySet().iterator();
                       while (it.hasNext()) {
                           Map.Entry pair = (Map.Entry) it.next();
                           MushroomReference tmpmush = (MushroomReference) pair.getValue();
                           for (int i = 0; i < Groups.length; i++) {
                               if (Arrays.asList(tmpmush.Group).contains(Groups[i]) && !spawnlist.contains(tmpmush)) {
                                   for (int j = 0; j < tmpmush.Rarity; j++) spawnlist.add(tmpmush);
                                   break;
                               }
                           }
                       }
                       int index = (int) (Math.random() * spawnlist.size());
                       MushroomSource newMushSource = new MushroomSource(scale, spawnlist.get(index).Name, spawnlist.get(index).Examine);
                       newMushSource.Yield = (int) (Math.random() * spawnlist.get(index).BaseYield + 1);
                       newMushSource.SubstrateRemaining = 0;
                       newMushSource.setBounds(xpos, ypos, CellSize, CellSize);
                       newMushSource.setAnimationSheets(GameReference.ResourceManager.get(newMushSource.Name + ".png", Texture.class), CellSize);
                       newMushSource.State = MushroomSource.FINAL_FRUITS;
                       newMushSource.GetStatic();
                       ObjectsAndCharacters.add(newMushSource);
                       CurrentMap.getLayers().get("Sprites").getObjects().add(newMushSource.Background);
                       CurrentMap.getLayers().get("Sprites").getObjects().add(newMushSource.tmo);
                   }
               }
           }
       }
    }

    public void UpdateControls(float delta) {
        GameReference.cam.position.set(player.getX(), player.getY(), 0);
        ControlsUp.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.12f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsDown.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.01f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsLeft.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.01f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsRight.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.23f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsInventory.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.89f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);

        //Optional controls place out of screen if not in use
        ControlsUse.setBounds(GameReference.cam.position.x + CAM_WIDTH, GameReference.cam.position.y + CAM_HEIGHT, 1, 1);
        ControlsExaminable.setBounds(GameReference.cam.position.x + CAM_WIDTH, GameReference.cam.position.y + CAM_HEIGHT, 1, 1);
        ControlsPlant.setBounds(GameReference.cam.position.x + CAM_WIDTH, GameReference.cam.position.y + CAM_HEIGHT, 1, 1);

        //Check player vision
        Vector<String> queue = new Vector<String>();
        for(Entity e : ObjectsAndCharacters) {
            if(e.getBoundingRectangle().overlaps(player.Vision)) {
                if (e instanceof Collectible) queue.add("Collectible");
                if (e instanceof Examinable) queue.add("Examinable");
                break;
            }
        }
        for(MushroomSource m : player.MyGrowingMushrooms) {
            if(m.Location.equals(CurrentState.CurrentMap) && m.getBoundingRectangle().overlaps(player.Vision)) {
                if (m.State == MushroomSource.FINAL_FRUITS) queue.add("Collectible");
               queue.add("Examinable");
                break;
            }
        }
        if(CurrentState.CurrentMap.equals("Mycofarm.tmx")) queue.add("Plantable");
        for(int i = 0; i < queue.size(); i++) {
            if(queue.get(i).toString().equals("Collectible")) {
                ControlsUse.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + (0.89f - (float)i * 0.11f) * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.05f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            }
            else if(queue.get(i).toString().equals("Examinable")) {
                ControlsExaminable.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + (0.89f - (float)i * 0.11f) * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.05f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            }
            else if(queue.get(i).toString().equals("Plantable")) {
                ControlsPlant.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + (0.89f - (float)i * 0.11f) * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.05f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
            }
        }
    }

    public void UpdateWorld(float delta) {
        CurrentState.CurrentTime += delta;
        if(CurrentState.CurrentTime - CurrentState.LastUpdate > WORLD_UPDATE_INTERVAL) {
            CurrentState.LastUpdate = CurrentState.CurrentTime;
            if(CurrentState.MushroomSpawnChance >= NORMAL && CurrentState.MushroomSpawnChance < AFTER_RAIN) {
                CurrentState.MushroomSpawnChance = (float)Math.random() * 0.1f + 0.9f;
            }
            else CurrentState.MushroomSpawnChance = (float)Math.random() * 0.89f;

            //Update world
            SpawnMushroomsAndResources();

        }

        if(CurrentState.CurrentTime > ONE_DAY) {
            CurrentState.CurrentTime = 0;
            CurrentState.LastUpdate = 0;
            for(MushroomSource m : player.MyGrowingMushrooms) {
                if(!m.isLog && m.State >= MushroomSource.STAGE_0 && m.State < MushroomSource.STAGE_100) {
                    m.ColonisationPercentage += 25 + m.Efficiency;
                    if(m.ColonisationPercentage > 100) m.ColonisationPercentage = 100;
                    m.State++;
                    m.SubstrateRemaining -= (10 - m.Efficiency);
                }
                else if(!m.isLog && m.State == MushroomSource.STAGE_100) {
                    //do check here for temperature conditions
                    m.State = MushroomSource.PINNING;
                }
                else if(m.isLog && m.ColonisationPercentage < 100) {
                    m.ColonisationPercentage += 25 + m.Efficiency;
                    if(m.ColonisationPercentage > 100) m.ColonisationPercentage = 100;
                    m.SubstrateRemaining -= (10 - m.Efficiency);
                }
                else if(m.isLog && m.ColonisationPercentage == 100 && m.State == MushroomSource.STAGE_0) {
                    //do check here for temperature conditions
                    m.State = MushroomSource.PINNING;
                }
                else if(m.State == MushroomSource.PINNING) m.Yield = (int)(Math.round(Math.random() * m.Yield + 1));

            }
        }
    }
}

