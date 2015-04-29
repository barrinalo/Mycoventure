package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

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

        //Load Maps
        GameReference.ResourceManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        GameReference.ResourceManager.load("Mycofarm.tmx", TiledMap.class);
        GameReference.ResourceManager.load("PathToTown.tmx", TiledMap.class);
        GameReference.ResourceManager.load("Town.tmx", TiledMap.class);
        GameReference.ResourceManager.load("AsceticHut.tmx", TiledMap.class);

        //Load Startscreen Stuff
        GameReference.ResourceManager.load("StartScreen.png", Texture.class);

        //Load UI Controls
        GameReference.ResourceManager.load("ControlsUse.png", Texture.class);
        GameReference.ResourceManager.load("ControlsUp.png", Texture.class);
        GameReference.ResourceManager.load("ControlsDown.png", Texture.class);
        GameReference.ResourceManager.load("ControlsLeft.png", Texture.class);
        GameReference.ResourceManager.load("ControlsRight.png", Texture.class);
        GameReference.ResourceManager.load("ControlsSettings.png", Texture.class);
        GameReference.ResourceManager.load("ControlsInventory.png", Texture.class);
        GameReference.ResourceManager.load("ControlsCancel.png", Texture.class);
        GameReference.ResourceManager.load("ControlsExaminable.png", Texture.class);
        GameReference.ResourceManager.load("ControlsPlant.png", Texture.class);
        GameReference.ResourceManager.load("ControlsConfirm.png", Texture.class);

        //Load Player
        GameReference.ResourceManager.load("Player.png", Texture.class);

        //Load InventoryIcons
        GameReference.ResourceManager.load("Compost.png", Texture.class);
        GameReference.ResourceManager.load("Culture.png", Texture.class);
        GameReference.ResourceManager.load("Spawn.png", Texture.class);
        GameReference.ResourceManager.load("Money.png", Texture.class);
        GameReference.ResourceManager.load("Log.png", Texture.class);
        GameReference.ResourceManager.load("Supplements.png", Texture.class);
        GameReference.ResourceManager.load("Bulkers.png", Texture.class);

        //Load Mushrooms
        GameReference.ResourceManager.load("Golden Oyster.png", Texture.class);
        GameReference.ResourceManager.load("Shiitake.png", Texture.class);

        //Load other objects
        GameReference.ResourceManager.load("WoodlandsLog.png", Texture.class);

        //Load Fonts
        GameReference.ResourceManager.setLoader(BitmapFont.class, new FreetypeFontLoader(new InternalFileHandleResolver()));
        GameReference.ResourceManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
        FreetypeFontLoader.FreeTypeFontLoaderParameter FontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        FontParams.fontFileName = "ASMAN.TTF";
        FontParams.fontParameters.size = 24;
        FontParams.fontParameters.color = Color.WHITE;
        GameReference.ResourceManager.load("SmallFont", BitmapFont.class, FontParams);
        FreetypeFontLoader.FreeTypeFontLoaderParameter FontParams2 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        FontParams2.fontFileName = "ASMAN.TTF";
        FontParams2.fontParameters.size = 36;
        FontParams2.fontParameters.color = Color.WHITE;
        GameReference.ResourceManager.load("MediumFont", BitmapFont.class, FontParams2);
        
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
