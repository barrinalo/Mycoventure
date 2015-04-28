package com.mycoventure.game;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by david.chong on 2015/04/27.
 */
public class CharacterEntity extends AnimatedEntity{
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;

    public boolean IsMoving;
    public int Dir;
    public float MoveSpeed;
    public Rectangle Vision;
    int VisionWidth, VisionHeight;

    public CharacterEntity(float scale) {
        super(scale);
        IsMoving = false;
        Dir = DOWN;
        MoveSpeed = 1;
        VisionWidth = 0;
        VisionHeight = 0;
        Vision = new Rectangle();
    }

    public void Move(float xdist, float ydist, float delta) {
        Reposition(xdist, ydist);
        if(VisionWidth != 0 && VisionHeight != 0) {
            switch(Dir) {
                case LEFT:
                    Vision.width = VisionHeight;
                    Vision.height = VisionWidth;
                    Vision.x = getX() - VisionHeight;
                    Vision.y = getY();
                    break;
                case RIGHT:
                    Vision.width = VisionHeight;
                    Vision.height = VisionWidth;
                    Vision.x = getX() + getWidth();
                    Vision.y = getY();
                    break;
                case UP:
                    Vision.width = VisionWidth;
                    Vision.height = VisionHeight;
                    Vision.x = getX();
                    Vision.y = getY() + getHeight();
                    break;
                case DOWN:
                    Vision.width = VisionWidth;
                    Vision.height = VisionHeight;
                    Vision.x = getX();
                    Vision.y = getY() - VisionHeight;
                    break;
            }
        }
        Animate(delta, Dir);
    }

    public void Stop() {
        IsMoving = false;
        GetStatic(Dir);
        if(VisionWidth != 0 && VisionHeight != 0) {
            switch(Dir) {
                case LEFT:
                    Vision.width = VisionHeight;
                    Vision.height = VisionWidth;
                    Vision.x = getX() - VisionHeight;
                    Vision.y = getY();
                    break;
                case RIGHT:
                    Vision.width = VisionHeight;
                    Vision.height = VisionWidth;
                    Vision.x = getX() + getWidth();
                    Vision.y = getY();
                    break;
                case UP:
                    Vision.width = VisionWidth;
                    Vision.height = VisionHeight;
                    Vision.x = getX();
                    Vision.y = getY() + getHeight();
                    break;
                case DOWN:
                    Vision.width = VisionWidth;
                    Vision.height = VisionHeight;
                    Vision.x = getX();
                    Vision.y = getY() - VisionHeight;
                    break;
            }
        }
    }

    public void SetVisionWhenLookingUp(int w, int h) {
        VisionWidth = w;
        VisionHeight = h;
    }
}
