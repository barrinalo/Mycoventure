package com.mycoventure.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Mycoventure extends Game {
    static final int DEFAULT_WIDTH = 100;
    static final int DEFAULT_HEIGHT = 100;
    static float RATIO;

	AssetManager ResourceManager;
    SpriteBatch batch;
    OrthographicCamera cam;

    LoadingScreen Loading;
    StartScreen Start;
    Gameplay MainGame;

	@Override
	public void create () {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        RATIO = h / w;

        cam = new OrthographicCamera(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();

        ResourceManager = new AssetManager();
        ResourceManager.load("LoadingBackground.png", Texture.class);
        ResourceManager.load("LoadingProgress.png", Texture.class);
        ResourceManager.finishLoading();

        Loading = new LoadingScreen(this);
        Start = new StartScreen(this);
        MainGame = new Gameplay(this);

        setScreen(Loading);
	}

	@Override
	public void render () {
        super.render();
	}
}
