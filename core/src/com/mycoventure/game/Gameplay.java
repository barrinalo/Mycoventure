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

/**
 * Created by david.chong on 2015/04/24.
 */
public class Gameplay implements Screen, InputProcessor{
    static final float scale = 64f;
    static final int CAM_WIDTH = 10;
    static final int CAM_HEIGHT = 10;
    static TiledMap CurrentMap;
    static OrthogonalTiledMapRendererWithSprites MapRenderer;
    Mycoventure GameReference;

    //Initiate Controls
    Sprite ControlsUp;
    Sprite ControlsDown;
    Sprite ControlsLeft;
    Sprite ControlsRight;
    Sprite ControlsUse;

    //Player
    Player player;

    public Gameplay(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        ControlsUp = new Sprite(GameReference.ResourceManager.get("ControlsUp.png", Texture.class));
        ControlsDown = new Sprite(GameReference.ResourceManager.get("ControlsDown.png", Texture.class));
        ControlsLeft = new Sprite(GameReference.ResourceManager.get("ControlsLeft.png", Texture.class));
        ControlsRight = new Sprite(GameReference.ResourceManager.get("ControlsRight.png", Texture.class));
        ControlsUse = new Sprite(GameReference.ResourceManager.get("ControlsUse.png", Texture.class));

        player = new Player(GameReference.ResourceManager, scale);

        CurrentMap = GameReference.ResourceManager.get("Mycofarm.tmx", TiledMap.class); // Replace with map from save file in the future
        MapRenderer = new OrthogonalTiledMapRendererWithSprites(CurrentMap, 1 / scale);
        CurrentMap.getLayers().get("Sprites").getObjects().add(player.tmo);
        GameReference.cam.setToOrtho(false,CAM_HEIGHT,CAM_WIDTH);
        GameReference.cam.update();
    }

    @Override
    public void render(float delta) {
        //Check collisions with player

        if(player.IsMoving) {
            boolean canwalk = true;
            Rectangle playerrect = new Rectangle(player.getBoundingRectangle());
            TiledMapTileLayer t = (TiledMapTileLayer)CurrentMap.getLayers().get("Terrain");
            if (player.Direction == "Left") {
                playerrect.x -= delta;
                if(playerrect.x > 0) {
                    if (t.getCell((int) playerrect.x, (int) playerrect.y).getTile().getProperties().containsKey("Unwalkable"))canwalk = false;
                }
                else canwalk = false;
            }
            else if (player.Direction == "Right") {
                playerrect.x += delta;
                if(t.getCell((int)(playerrect.x + playerrect.width), (int)playerrect.y).getTile().getProperties().containsKey("Unwalkable")) canwalk = false;
            }
            else if (player.Direction == "Up") {
                playerrect.y += delta;
                if(t.getCell((int)playerrect.x, (int)(playerrect.y + playerrect.height)).getTile().getProperties().containsKey("Unwalkable")) canwalk = false;
            }
            else if (player.Direction == "Down") {
                playerrect.y -= delta;
                if(playerrect.y > 0) {
                    if(t.getCell((int)playerrect.x, (int)playerrect.y).getTile().getProperties().containsKey("Unwalkable")) canwalk = false;
                }
                else canwalk = false;
            }

            for (MapObject tmp : CurrentMap.getLayers().get("Collision").getObjects()) {
                if (tmp instanceof RectangleMapObject) {
                    RectangleMapObject rect = (RectangleMapObject) tmp;
                    Rectangle r = new Rectangle(rect.getRectangle());
                    r.set(r.x / scale, r.y / scale, r.width / scale, r.height / scale);
                    if (r.overlaps(playerrect)) {
                        canwalk = false;
                        break;
                    }
                }
            }
            if (!canwalk) player.Stop();
        }
        player.update(delta);

        //Update controls position
        GameReference.cam.position.set(player.getX(), player.getY(),0);
        ControlsUp.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.12f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsDown.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.01f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsLeft.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.01f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsRight.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.23f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsUse.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);


        //Start drawing
        Gdx.gl.glClearColor(0,0,0,1);
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
        GameReference.batch.end();

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
        if(ControlsUp.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.Move("Up");
        }
        if(ControlsDown.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.Move("Down");
        }
        if(ControlsRight.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.Move("Right");
        }
        if(ControlsLeft.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            player.Move("Left");
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.Stop();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

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

    public void ChangeMap(String MapName) {
        CurrentMap.dispose();
        CurrentMap = GameReference.ResourceManager.get(MapName, TiledMap.class);
        CurrentMap.getLayers().get("Objects").getObjects().add(player.tmo);
        MapRenderer.setMap(CurrentMap);
        MapRenderer.setView(GameReference.cam);
        GameReference.cam.setToOrtho(false,10,10);
        GameReference.cam.update();
    }
}
