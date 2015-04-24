package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by david.chong on 2015/04/24.
 */
public class LoadingScreen implements Screen {
    Mycoventure GameReference;
    Sprite LoadingBackground;
    Sprite LoadingProgress;
    public LoadingScreen(Mycoventure ref) {
        GameReference = ref;
    }

    @Override
    public void show() {
        LoadingBackground = new Sprite(GameReference.ResourceManager.get("LoadingBackground.png", Texture.class));
        LoadingBackground.setBounds(0, 0, GameReference.DEFAULT_WIDTH, GameReference.DEFAULT_HEIGHT);

        //Load Game Resources
        GameReference.ResourceManager.load("StartScreen.png", Texture.class);
        GameReference.ResourceManager.load("ControlsUse.png", Texture.class);
        GameReference.ResourceManager.load("ControlsUp.png", Texture.class);
        GameReference.ResourceManager.load("ControlsDown.png", Texture.class);
        GameReference.ResourceManager.load("ControlsLeft.png", Texture.class);
        GameReference.ResourceManager.load("ControlsRight.png", Texture.class);
        GameReference.ResourceManager.load("ControlsSettings.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        if(GameReference.ResourceManager.update()){
            GameReference.setScreen(GameReference.Start);
            hide();
            dispose();
            return;
        }
        float progress = GameReference.ResourceManager.getProgress();
        LoadingProgress = new Sprite(new TextureRegion(GameReference.ResourceManager.get("LoadingProgress.png", Texture.class), 0, 0, GameReference.ResourceManager.get("LoadingProgress.png", Texture.class).getWidth(), (int)(GameReference.ResourceManager.get("LoadingProgress.png", Texture.class).getHeight() * progress)));
        LoadingProgress.setBounds(0, 0, GameReference.cam.viewportWidth, GameReference.cam.viewportHeight * progress);

        GameReference.cam.update();
        GameReference.batch.setProjectionMatrix(GameReference.cam.combined);

        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GameReference.batch.begin();
        LoadingBackground.draw(GameReference.batch);
        LoadingProgress.draw(GameReference.batch);
        GameReference.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        GameReference.RATIO = (float)width / (float)height;
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
    }

    @Override
    public void dispose() {
        LoadingBackground = null;
        LoadingProgress = null;
    }
}
