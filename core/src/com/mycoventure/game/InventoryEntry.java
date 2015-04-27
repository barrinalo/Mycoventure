package com.mycoventure.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by david.chong on 2015/04/27.
 */
public class InventoryEntry {
    Sprite Display;
    Label Name, Description, Quantity;

    public InventoryEntry(Texture Display, String Name, String Description, int Quantity, AssetManager res) {
        this.Display = new Sprite(Display);
        this.Name = new Label(Name, new Label.LabelStyle(res.get("MediumFont", BitmapFont.class), Color.WHITE));
        this.Description = new Label(Description, new Label.LabelStyle(res.get("SmallFont", BitmapFont.class), Color.WHITE));
        this.Quantity = new Label(Integer.toString(Quantity), new Label.LabelStyle(res.get("MediumFont", BitmapFont.class), Color.WHITE));

        this.Name.setWrap(true);
        this.Description.setWrap(true);
        this.Quantity.setWrap(true);

        this.Name.setEllipsis(true);
        this.Description.setEllipsis(true);
        this.Quantity.setEllipsis(true);

    }
    public void Format(int position, int CellSize) {
        Display.setBounds(0.5f * CellSize, -position * CellSize, CellSize, CellSize);
        Name.setBounds(1.6f * CellSize, -position * CellSize, 2f * CellSize, CellSize);
        Description.setBounds(3.8f * CellSize, -position * CellSize, 5f * CellSize, CellSize);
        Quantity.setBounds(9f * CellSize, -position * CellSize, 0.5f * CellSize, CellSize);
    }
    public void draw(SpriteBatch b) {
        Display.draw(b);
        Name.draw(b, 1);
        Description.draw(b, 1);
        Quantity.draw(b, 1);
    }
}
