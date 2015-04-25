package com.mycoventure.game;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by David on 25/04/2015.
 */
public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererWithSprites(TiledMap map, float scale) {
        super(map, scale);
    }

    @Override
    public void renderObject(MapObject object) {
        if(object instanceof TextureMapObject) {
            TextureMapObject textureObj = (TextureMapObject) object;
            batch.draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(), textureObj.getTextureRegion().getRegionWidth() * unitScale, textureObj.getTextureRegion().getRegionHeight() * unitScale);
        }
    }

}
