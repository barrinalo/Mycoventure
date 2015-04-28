package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by David on 27/04/2015.
 */
public class InventoryInterface implements InterfaceTemplate {
    int tempy;
    Label Header;
    Sprite CancelButton;
    public Vector<InventoryEntry> InventoryList;
    Gameplay Ref;
    public InventoryInterface(Gameplay Ref) {
        this.Ref = Ref;
        Header = new Label("Inventory", new Label.LabelStyle(Ref.GameReference.ResourceManager.get("MediumFont", BitmapFont.class), Color.WHITE));
        CancelButton = new Sprite(Ref.GameReference.ResourceManager.get("ControlsCancel.png", Texture.class));
        InventoryList = new Vector<InventoryEntry>();
    }

    @Override
    public void Show() {
        Ref.CurrentInterface = "Inventory";
        Ref.GameReference.cam.setToOrtho(false, Ref.CAM_WIDTH, Ref.CAM_HEIGHT);
        Ref.GameReference.cam.position.x = Ref.CAM_WIDTH / 2;
        Ref.GameReference.cam.position.y = -Ref.CAM_HEIGHT / 2;

        int pos = 2;
        for(Compost c : Ref.player.TypesOfCompost) {
            InventoryList.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Compost.png", Texture.class), "Compost",
                    "Bulk - " + Float.toString(c.BulkPercentage) + "%\nSupplement - "
                            + Float.toString(c.SupplementPercentage) + "%", c.Quantity, Ref.GameReference.ResourceManager));
            InventoryList.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }
        for(Spawn s : Ref.player.SpawnAndCultures) {
            if(s.Culture) InventoryList.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Culture.png", Texture.class), s.Name, s.Description, s.Quantity, Ref.GameReference.ResourceManager));
            else InventoryList.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Spawn.png", Texture.class), s.Name, s.Description, s.Quantity, Ref.GameReference.ResourceManager));
            InventoryList.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }
        Iterator it = Ref.player.Mushrooms.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            InventoryList.add(new InventoryEntry(Ref.GameReference.ResourceManager.get(pair.getKey() + ".png", Texture.class), pair.getKey().toString(), Ref.MushroomDatabase.get(pair.getKey().toString()).Examine, Integer.parseInt(pair.getValue().toString()), Ref.GameReference.ResourceManager));
            InventoryList.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }
    }
    @Override
    public void Hide() {
        InventoryList.clear();
    }
    @Override
    public void draw() {
        Gdx.gl.glClearColor(204/255f, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Ref.GameReference.batch.setProjectionMatrix(Ref.GameReference.cam.combined);
        Ref.GameReference.cam.update();
        Ref.GameReference.batch.begin();
        for(int i = 0; i < InventoryList.size(); i++) {
            Vector3 screencoords = Ref.GameReference.cam.project(new Vector3(0, InventoryList.get(i).Display.getY(), 0));
            if(screencoords.y / Ref.GameReference.cam.viewportHeight <= 0.9f) InventoryList.get(i).draw(Ref.GameReference.batch);
        }
        Header.draw(Ref.GameReference.batch, 1);
        CancelButton.draw(Ref.GameReference.batch);
        Ref.GameReference.batch.end();

    }
    @Override
    public void Update(float delta) {
        CancelButton.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.89f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.89f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
        Header.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.05f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.9f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
    }

    @Override
    public void touchDragged(int screenX, int screenY, int pointer) {
        int dif = screenY - tempy;
        if(InventoryList.size() >= 9) {
            if (dif > 0) {
                // scroll up
                if (Ref.GameReference.cam.position.y + Ref.GameReference.cam.viewportHeight / 2 < 0) {
                    Ref.GameReference.cam.translate(0, screenY - tempy);
                    Ref.GameReference.cam.update();
                    tempy = screenY;
                }
            } else if (dif < 0) {
                //scroll down
                Vector3 screencoords = Ref.GameReference.cam.project(new Vector3(0, InventoryList.lastElement().Display.getY(), 0));
                if (screencoords.y / Ref.GameReference.cam.viewportHeight < 0) {
                    Ref.GameReference.cam.translate(0, screenY - tempy);
                    Ref.GameReference.cam.update();
                    tempy = screenY;
                }
            }
        }
    }

    @Override
    public void touchUp(int screenX, int screenY, int pointer) {

    }

    @Override
    public void touchDown(int screenX, int screenY, int pointer) {
        tempy = screenY;
        Vector3 WorldCoordinates = Ref.GameReference.cam.unproject(new Vector3(screenX,screenY,0));
        if(CancelButton.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            Ref.CurrentInterface = "None";
            Hide();
        }
    }
}
