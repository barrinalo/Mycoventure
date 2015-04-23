package com.mycoventure.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Mycoventure extends ApplicationAdapter {
    static final int WORLD_WIDTH = 100;
    static final int WORLD_HEIGHT = 100;

	AssetManager res;
    SpriteBatch batch;
    OrthographicCamera cam;
    Sprite LoadingSprite;

	@Override
	public void create () {
        res = new AssetManager();
        batch = new SpriteBatch();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        res.load("LoadingScreen.png", Texture.class);
	}

	@Override
	public void render () {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(res.update()) {
            //Go to main screen;
        }

        float progress = res.getProgress();
        if(res.isLoaded("LoadingScreen.png") && LoadingSprite == null) {
            LoadingSprite = new Sprite(res.get("LoadingScreen.png", Texture.class));
            LoadingSprite.setSize(WORLD_WIDTH, WORLD_HEIGHT);
            System.out.println("Loaded");
        }
        if(LoadingSprite != null) {
            batch.begin();
            LoadingSprite.draw(batch);
            batch.end();
            System.out.println("Drawing");
        }
	}
}
