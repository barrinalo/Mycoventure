package com.mycoventure.game;

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

    public CharacterEntity(float scale) {
        super(scale);
        IsMoving = false;
        Dir = DOWN;
        MoveSpeed = 1;
    }

    public void Move(float xdist, float ydist, float delta) {
        Reposition(xdist, ydist);
        Animate(delta, Dir);
    }

    public void Stop() {
        IsMoving = false;
        GetStatic(Dir);
    }
}
