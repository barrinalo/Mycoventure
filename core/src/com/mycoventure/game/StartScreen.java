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
public class StartScreen implements Screen, InputProcessor {

    Mycoventure GameReference;
    Sprite WelcomeSprite;
    Sprite Settings;

    public StartScreen(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        WelcomeSprite = new Sprite(GameReference.ResourceManager.get("StartScreen.png", Texture.class));
        WelcomeSprite.setBounds(0,0, GameReference.cam.viewportWidth, GameReference.cam.viewportHeight);
        Settings = new Sprite(GameReference.ResourceManager.get("ControlsSettings.png", Texture.class));
        Settings.setBounds(0.05f * GameReference.DEFAULT_WIDTH, 0.85f * GameReference.DEFAULT_HEIGHT, 0.1f * GameReference.DEFAULT_WIDTH, 0.1f * GameReference.DEFAULT_HEIGHT);
    }

    @Override
    public void render(float delta) {
        GameReference.cam.update();
        GameReference.batch.setProjectionMatrix(GameReference.cam.combined);

        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameReference.batch.begin();
        WelcomeSprite.draw(GameReference.batch);
        Settings.draw(GameReference.batch);
        GameReference.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        GameReference.RATIO = width / height;
        GameReference.cam.viewportHeight = GameReference.DEFAULT_HEIGHT;
        GameReference.cam.viewportWidth = GameReference.DEFAULT_WIDTH;
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
        hide();
        GameReference.setScreen(GameReference.MainGame);
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
