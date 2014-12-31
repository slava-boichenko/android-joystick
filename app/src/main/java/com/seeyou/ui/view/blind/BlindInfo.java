package com.seeyou.ui.view.blind;

import android.graphics.Rect;
import android.util.Log;

public class BlindInfo {
    private final Rect mBounds;
    private float mRotationX, mRotationY, mRotationZ;


    private static final int MAX_ROTATION_X = 180;
    private int mStartTickDelay;
    private int transformationDurationInTicks = 2048;



    public BlindInfo(int left, int top, int right, int bottom) {
        this(left, top, right, bottom, 0);
    }

    public BlindInfo(int left, int top, int right, int bottom, int startTickDelay) {
        mBounds = new Rect(left, top, right, bottom);
        mStartTickDelay = startTickDelay;
    }

    public int getHeight() {
        return mBounds.height();
    }

    public int getWidth() {
        return mBounds.width();
    }

    public int getLeft() {
        return mBounds.left;
    }

    public int getRight() {
        return mBounds.right;
    }

    public int getTop() {
        return mBounds.top;
    }

    public int getBottom() {
        return mBounds.bottom;
    }

    public Rect getBounds(){
        return mBounds;
    }

    public float getRotationX() {
        return mRotationX ;
    }

    public float getRotationY() {
        return mRotationY ;
    }

    public float getRotationZ() {
        return mRotationZ ;
    }

    public int getStartTickDelay() {
        return mStartTickDelay ;
    }

    public void setRotationX(float rotationX){
        mRotationX = rotationX;
    }

    float mAlpha = 0f;
    public void setAlpha(float alpha){
        mAlpha = alpha;
    }
    public float getAlpha(){
        return mAlpha;
    }

    public void updateState(int currentTick){
        if(currentTick <= mStartTickDelay)
            return;

        int localCurrent = currentTick - mStartTickDelay;
        float factor = localCurrent > transformationDurationInTicks ? 1f : localCurrent / (float)transformationDurationInTicks;

        setAlpha(factor * 1f);
        setRotationX(MAX_ROTATION_X * (factor - 1f));

        Log.e("TICK", "tick :" + currentTick + " alpha " + getAlpha() + " rot " + getRotationX());
    }

    public void setRotations(float rotationX, float rotationY, float rotationZ){
        mRotationX = rotationX;
        mRotationY = rotationY;
        mRotationZ = rotationZ;
    }

}
