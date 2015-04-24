package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by david.chong on 2015/04/24.
 */
public class Gameplay implements Screen, InputProcessor{
    static final int CAM_WIDTH = 25;
    static final int CAM_HEIGHT = 25;
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

        player = new Player(GameReference.ResourceManager);
        player.setBounds(GameReference.cam.position.x, GameReference.cam.position.y, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        player.setOriginCenter();
    }

    @Override
    public void render(float delta) {
        GameReference.cam.update();
        GameReference.batch.setProjectionMatrix(GameReference.cam.combined);

        //Update controls position
        ControlsUp.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.12f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsDown.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.125f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.01f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsLeft.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.01f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsRight.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.23f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);
        ControlsUse.setBounds(GameReference.cam.position.x - CAM_WIDTH / 2f + 0.89f * CAM_WIDTH, GameReference.cam.position.y - CAM_HEIGHT / 2f + 0.06f * CAM_HEIGHT, 0.1f * CAM_WIDTH, 0.1f * CAM_HEIGHT);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
}
