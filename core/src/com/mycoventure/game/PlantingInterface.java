package com.mycoventure.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by david.chong on 2015/04/29.
 */
public class PlantingInterface implements InterfaceTemplate {
    public static final int CHOOSE_INOCCULANT = 0;
    public static final int CHOOSE_SUBSTRATE = 1;
    Label Header;
    Sprite CancelButton, BackButton, ConfirmButton;
    Vector<InventoryEntry> Inocculants;
    Vector<InventoryEntry> Substrate;
    int State, Selection, tempy, ChosenSubstrate, ChosenInocculant;
    Gameplay Ref;
    Rectangle Pos;

    public PlantingInterface(Gameplay Ref) {
        this.Ref = Ref;
        Header = new Label("", new Label.LabelStyle(Ref.GameReference.ResourceManager.get("MediumFont", BitmapFont.class), Color.WHITE));
        CancelButton = new Sprite(Ref.GameReference.ResourceManager.get("ControlsCancel.png", Texture.class));
        BackButton = new Sprite(Ref.GameReference.ResourceManager.get("ControlsLeft.png", Texture.class));
        ConfirmButton = new Sprite(Ref.GameReference.ResourceManager.get("ControlsConfirm.png", Texture.class));
        Substrate = new Vector<InventoryEntry>();
        Inocculants = new Vector<InventoryEntry>();
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(204/255f, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Ref.GameReference.cam.update();

        if(Selection != 0) {
            Ref.s.setProjectionMatrix(Ref.GameReference.cam.combined);
            Ref.s.begin(ShapeRenderer.ShapeType.Filled);
            Ref.s.setColor(Color.YELLOW);
            Ref.s.rect(0, Selection * Ref.CellSize, Ref.CAM_WIDTH, Ref.CellSize);
            Ref.s.end();

        }

        Ref.GameReference.batch.setProjectionMatrix(Ref.GameReference.cam.combined);
        Ref.GameReference.batch.begin();

        if(State == CHOOSE_SUBSTRATE) {
            BackButton.draw(Ref.GameReference.batch);
            for(int i = 0; i < Substrate.size(); i++) Substrate.get(i).draw(Ref.GameReference.batch);
        }
        else if(State == CHOOSE_INOCCULANT) {
            for(int i = 0; i < Inocculants.size(); i++) Inocculants.get(i).draw(Ref.GameReference.batch);
        }
        CancelButton.draw(Ref.GameReference.batch);
        Header.draw(Ref.GameReference.batch, 1);
        ConfirmButton.draw(Ref.GameReference.batch);

        Ref.GameReference.batch.end();

    }

    @Override
    public void Update(float delta) {
        CancelButton.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.89f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.89f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
        ConfirmButton.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.78f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.89f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
        BackButton.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.67f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.89f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
        if(State == CHOOSE_INOCCULANT) Header.setText("Choose Inocculant");
        else if(State == CHOOSE_SUBSTRATE) Header.setText("Choose Substrate");
        Header.setBounds(Ref.GameReference.cam.position.x - Ref.CAM_WIDTH / 2f + 0.05f * Ref.CAM_WIDTH, Ref.GameReference.cam.position.y - Ref.CAM_HEIGHT / 2f + 0.9f * Ref.CAM_HEIGHT, 0.1f * Ref.CAM_WIDTH, 0.1f * Ref.CAM_HEIGHT);
    }

    @Override
    public void touchDragged(int screenX, int screenY, int pointer) {
        int dif = screenY - tempy;
        if(State == CHOOSE_INOCCULANT) {
            if (Inocculants.size() >= 9) {
                if (dif > 0) {
                    // scroll up
                    if (Ref.GameReference.cam.position.y + Ref.GameReference.cam.viewportHeight / 2 < 0) {
                        Ref.GameReference.cam.translate(0, screenY - tempy);
                        Ref.GameReference.cam.update();
                        tempy = screenY;
                    }
                } else if (dif < 0) {
                    //scroll down
                    Vector3 screencoords = Ref.GameReference.cam.project(new Vector3(0, Inocculants.lastElement().Display.getY(), 0));
                    if (screencoords.y / Ref.GameReference.cam.viewportHeight < 0) {
                        Ref.GameReference.cam.translate(0, screenY - tempy);
                        Ref.GameReference.cam.update();
                        tempy = screenY;
                    }
                }
            }
        }
        else if(State == CHOOSE_SUBSTRATE) {
            if (Substrate.size() >= 9) {
                if (dif > 0) {
                    // scroll up
                    if (Ref.GameReference.cam.position.y + Ref.GameReference.cam.viewportHeight / 2 < 0) {
                        Ref.GameReference.cam.translate(0, screenY - tempy);
                        Ref.GameReference.cam.update();
                        tempy = screenY;
                    }
                } else if (dif < 0) {
                    //scroll down
                    Vector3 screencoords = Ref.GameReference.cam.project(new Vector3(0, Substrate.lastElement().Display.getY(), 0));
                    if (screencoords.y / Ref.GameReference.cam.viewportHeight < 0) {
                        Ref.GameReference.cam.translate(0, screenY - tempy);
                        Ref.GameReference.cam.update();
                        tempy = screenY;
                    }
                }
            }
        }
    }

    @Override
    public void touchUp(int screenX, int screenY, int pointer) {
        Vector3 WorldCoordinates = Ref.GameReference.cam.unproject(new Vector3(screenX,screenY,0));
        Selection = (int)(Math.floor(WorldCoordinates.y / Ref.CellSize));
        if(State == CHOOSE_SUBSTRATE) {
            int tmpSelection = -Selection - 2;
            if(tmpSelection >= Substrate.size() || tmpSelection == -1) Selection = 0;
        }
        else if(State == CHOOSE_INOCCULANT) {
            int tmpSelection = -Selection - 2;
            if(tmpSelection >= Inocculants.size() || tmpSelection == -1) Selection = 0;
        }
    }

    @Override
    public void touchDown(int screenX, int screenY, int pointer) {
        tempy = screenY;
        Vector3 WorldCoordinates = Ref.GameReference.cam.unproject(new Vector3(screenX,screenY,0));
        if(CancelButton.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            Ref.CurrentInterface = "None";
            Hide();
        }
        else if(ConfirmButton.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y)) {
            if(State == CHOOSE_INOCCULANT && Selection != 0) {
                ChosenInocculant = -Selection - 2;
                Selection = 0;
                State = CHOOSE_SUBSTRATE;
            }
            else if(State == CHOOSE_SUBSTRATE && Selection != 0) {
                ChosenSubstrate = -Selection - 2;
                if(Inocculants.get(ChosenInocculant).ItemId != -1) Ref.player.SpawnAndCultures.get(Inocculants.get(ChosenInocculant).ItemId).Quantity -= 1;
                else Ref.player.Mushrooms.put(Inocculants.get(ChosenInocculant).Name.getText().toString(), Ref.player.Mushrooms.get(Inocculants.get(ChosenInocculant).Name.getText().toString()).intValue() - 1);
                if(Substrate.get(ChosenSubstrate).ItemId != -1) Ref.player.TypesOfCompost.get(Substrate.get(ChosenSubstrate).ItemId).Quantity -= 1;
                else Ref.player.Logs -= 1;
                String Mushname = Inocculants.get(ChosenInocculant).Name.getText().toString();
                MushroomSource m = new MushroomSource(Ref.scale, Mushname, Ref.MushroomDatabase.get(Mushname).Examine);
                if(Inocculants.get(ChosenInocculant).ItemId == -1) {
                    m.State = MushroomSource.STAGE_0;
                    m.ColonisationPercentage = 0;
                    m.OriginalSubstrate = 100;
                    m.SubstrateRemaining = 100;
                    if(Substrate.get(ChosenSubstrate).ItemId == -1) m.isLog = true;
                    else m.isLog = false;
                    if(Math.random() > 0.5f) m.HumidityPreference = (int)(Ref.MushroomDatabase.get(Mushname).BaseHumidityPreference - Math.random() * 10);
                    else m.HumidityPreference = (int)(Ref.MushroomDatabase.get(Mushname).BaseHumidityPreference + Math.random() * 10);
                    if(Math.random() > 0.5f) m.Efficiency = (int)(Ref.MushroomDatabase.get(Mushname).BaseEfficiency - Math.random() * 10);
                    else m.Efficiency = (int)(Ref.MushroomDatabase.get(Mushname).BaseEfficiency + Math.random() * 10);
                    if(Math.random() > 0.5f) m.SupplementPreference = (int)(Ref.MushroomDatabase.get(Mushname).BaseSupplementPreference - Math.random() * 10);
                    else m.SupplementPreference = (int)(Ref.MushroomDatabase.get(Mushname).BaseSupplementPreference + Math.random() * 10);
                    if(Math.random() > 0.5f) m.Speed = (int)(Ref.MushroomDatabase.get(Mushname).BaseSpeed - Math.random() * 10);
                    else m.Speed = (int)(Ref.MushroomDatabase.get(Mushname).BaseSpeed + Math.random() * 10);
                    if(Math.random() > 0.5f) m.TemperatureTrigger = (int)(Ref.MushroomDatabase.get(Mushname).BaseTemperatureTrigger - Math.random() * 10);
                    else m.TemperatureTrigger = (int)(Ref.MushroomDatabase.get(Mushname).BaseTemperatureTrigger + Math.random() * 10);

                    if(Substrate.get(ChosenSubstrate).ItemId == -1) m.setAnimationSheets(Ref.GameReference.ResourceManager.get(Mushname + ".png", Texture.class), Ref.GameReference.ResourceManager.get("LogSubstrate.png", Texture.class), Ref.CellSize);
                    else m.setAnimationSheets(Ref.GameReference.ResourceManager.get("CompostSubstrate.png", Texture.class), Ref.GameReference.ResourceManager.get("LogSubstrate.png", Texture.class), Ref.CellSize);
                    m.setBounds(Pos.x, Pos.y, Pos.width, Pos.height);
                    m.Location = Ref.CurrentState.CurrentMap;
                    Ref.player.MyGrowingMushrooms.add(m);
                    Ref.CurrentMap.getLayers().get("Sprites").getObjects().add(m.Background);
                    Ref.CurrentMap.getLayers().get("Sprites").getObjects().add(m.tmo);

                    Ref.CurrentInterface = "None";
                    Hide();
                }
                else {
                    m.State = MushroomSource.STAGE_0;
                    m.ColonisationPercentage = 0;
                    m.OriginalSubstrate = 100;
                    m.SubstrateRemaining = 100;
                    if(Substrate.get(ChosenSubstrate).ItemId == -1) m.isLog = true;
                    else m.isLog = false;



                    if(Substrate.get(ChosenSubstrate).ItemId == -1) m.setAnimationSheets(Ref.GameReference.ResourceManager.get(Mushname + ".png", Texture.class), Ref.GameReference.ResourceManager.get("LogSubstrate.png", Texture.class), Ref.CellSize);
                    else m.setAnimationSheets(Ref.GameReference.ResourceManager.get("CompostSubstrate.png", Texture.class), Ref.GameReference.ResourceManager.get("LogSubstrate.png", Texture.class), Ref.CellSize);
                    m.setBounds(Pos.x, Pos.y, Pos.width, Pos.height);
                    m.Location = Ref.CurrentState.CurrentMap;
                    Ref.player.MyGrowingMushrooms.add(m);
                    Ref.CurrentMap.getLayers().get("Sprites").getObjects().add(m.Background);
                    Ref.CurrentMap.getLayers().get("Sprites").getObjects().add(m.tmo);
                    Ref.CurrentInterface = "None";
                    Hide();
                }

            }
        }
        else if(BackButton.getBoundingRectangle().contains(WorldCoordinates.x,WorldCoordinates.y) && State == CHOOSE_SUBSTRATE) {
            State = CHOOSE_INOCCULANT;
            ChosenSubstrate = -1;
            ChosenInocculant = -1;
            Selection = 0;
            Ref.GameReference.cam.setToOrtho(false, Ref.CAM_WIDTH, Ref.CAM_HEIGHT);
            Ref.GameReference.cam.position.x = Ref.CAM_WIDTH / 2;
            Ref.GameReference.cam.position.y = -Ref.CAM_HEIGHT / 2;
        }

    }

    @Override
    public void Show() {};

    public void Show(Rectangle rect) {

        Ref.CurrentInterface = "Planting";
        State = CHOOSE_INOCCULANT;
        Pos = rect;
        Selection = 0;
        ChosenSubstrate = -1;
        ChosenInocculant = -1;
        Ref.GameReference.cam.setToOrtho(false, Ref.CAM_WIDTH, Ref.CAM_HEIGHT);
        Ref.GameReference.cam.position.x = Ref.CAM_WIDTH / 2;
        Ref.GameReference.cam.position.y = -Ref.CAM_HEIGHT / 2;

        //Fill up substrates;
        int pos = 2;
        if(Ref.player.Logs != 0) {
            Substrate.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Log.png", Texture.class), "Logs", "Something might grow on this", Ref.player.Logs, Ref.GameReference.ResourceManager, Ref.CellSize));
            Substrate.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }

        for(int i = 0; i < Ref.player.TypesOfCompost.size(); i++) {
            Compost c = Ref.player.TypesOfCompost.get(i);
            Substrate.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Compost.png", Texture.class), "Compost",
                    "Bulk - " + Float.toString(c.BulkPercentage) + "%\nSupplement - "
                            + Float.toString(c.SupplementPercentage) + "%", c.Quantity, Ref.GameReference.ResourceManager, Ref.CellSize));
            Substrate.lastElement().ItemId = i;
            Substrate.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }

        pos = 2;
        //Fill up inocculants
        for(int i = 0; i < Ref.player.SpawnAndCultures.size(); i++) {
            Spawn s = Ref.player.SpawnAndCultures.get(i);
            if(s.Quantity != 0) Inocculants.add(new InventoryEntry(Ref.GameReference.ResourceManager.get("Spawn.png", Texture.class), s.Name, s.Description, s.Quantity, Ref.GameReference.ResourceManager, Ref.CellSize));
            Inocculants.lastElement().ItemId = i;
            Inocculants.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }
        Iterator it = Ref.player.Mushrooms.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Inocculants.add(new InventoryEntry(Ref.GameReference.ResourceManager.get(pair.getKey() + ".png", Texture.class), pair.getKey().toString(), Ref.MushroomDatabase.get(pair.getKey().toString()).Examine, Integer.parseInt(pair.getValue().toString()), Ref.GameReference.ResourceManager, Ref.CellSize));
            Inocculants.lastElement().Format(pos, Ref.CellSize);
            pos++;
        }
    }

    @Override
    public void Hide() {
        Substrate.clear();
        Inocculants.clear();
        Pos = null;
        Selection = 0;
        ChosenSubstrate = -1;
        ChosenInocculant = -1;
    }
}
