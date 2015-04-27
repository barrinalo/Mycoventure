package com.mycoventure.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Vector;

/**
 * Created by David on 27/04/2015.
 */
public interface InterfaceTemplate {

    public void draw();
    public void Update(float delta);
    public void touchDragged(int screenX, int screenY, int pointer);
    public void touchUp(int screenX, int screenY, int pointer);
    public void touchDown(int screenX, int screenY, int pointer);
    public void Show();
    public void Hide();

}
