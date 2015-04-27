package com.mycoventure.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.objects.TextureMapObject;

/**
 * Created by david.chong on 2015/04/27.
 */
public class Entity extends Sprite{
    TextureMapObject tmo;
    float scale;
    boolean Blocking, IsMovable;
    public Entity(float scale) {
        tmo = new TextureMapObject();
        this.scale = scale;
        setSize(64 / scale,64 / scale);
        setOriginCenter();
        Blocking = true;
        IsMovable = false;
    }

    public void Reposition(float xdist, float ydist) {
        translate(xdist, ydist);
        tmo.setX(getX());
        tmo.setY(getY());
    }
}
