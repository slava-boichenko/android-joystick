package com.seeyou.ui.view.blind;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import com.seeyou.R;

import java.util.ArrayList;
import java.util.List;

public class BlindView extends FrameLayout {

    private Bitmap mUndistortedBitmap;
    private Canvas mUndistortedCanvas;
    private BitmapDrawable mBgDrawable;
    private Paint mBlindPaint;
    private final Camera mCamera = new Camera();

    boolean isInBlindMode = true;
    boolean initBmpAndCanvas = true;

    private final int BLIND_TICK_MAX = 700;
    private int blindTick;


    public BlindView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public BlindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlindView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBlindPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mBlindPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Called by draw to draw the child views. This may be overridden by derived
     * classes to gain control just before its children are drawn (but after its
     * own view has been drawn).
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawCustomStuff(canvas);
    }

    /**
     * Called from layout when this view should assign a size and position to
     * each of its children.
     */
    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        super.onLayout(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int blindHeight = getResources().getDimensionPixelSize(R.dimen.blind_height);
        setupBlinds(blindHeight);

        /*for (int i = 0; i < blindList.size(); i++) {
            float x = (-55f + (float) i / ((float) blindList.size() - 1f) * 110f);
            blindList.get(i).setRotations(x, 0f, 0f);
        }*/
    }

    private void drawCustomStuff(Canvas screenCanvas) {


        if (!isInBlindMode || (isInBlindMode && initBmpAndCanvas)) {
            if (isInBlindMode && initBmpAndCanvas) {
                mUndistortedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                mUndistortedCanvas = new Canvas(mUndistortedBitmap);
            }

            Canvas canvasToDraw = isInBlindMode ? mUndistortedCanvas : screenCanvas;
            drawUndistorted(canvasToDraw);

            if (isInBlindMode)
                drawBlinds(screenCanvas);
        }


        //drawUndistorted(screenCanvas);
    }

    private void drawBackground(Canvas canvas){
        if (mBgDrawable != null) {
            mBgDrawable.draw(canvas);
        } else
            canvas.drawColor(0x00AAAAAA);
    }

    private void drawUndistorted(Canvas canvas) {
        drawBackground(canvas);
        super.dispatchDraw(canvas);
    }

    private void drawBlinds(Canvas canvas) {
        drawBackground(canvas);

        for (BlindInfo blind : blindList) {
          if (blind.getStartTickDelay() > blindTick)
               break;
            blind.updateState(blindTick);
            drawBlind(blind, canvas);
        }
    }


    private void drawBlind(BlindInfo info, Canvas canvas) {
        final float xRotation = info.getRotationX();
        final float alpha = info.getAlpha();

        final float yRotation = info.getRotationY();
        final float zRotation = info.getRotationZ();

        final int width = info.getWidth();
        final int height = info.getHeight();
        final int coordX = info.getLeft();
        final int coordY = info.getTop();

        mBlindPaint.setAlpha((int)(alpha * 255));

        canvas.save();
        mCamera.save();

        mCamera.rotateX(xRotation);

        canvas.translate(coordX + (width / 2f), coordY + (height / 2f));

        Matrix cameraMatrix = new Matrix();
        mCamera.getMatrix(cameraMatrix);
        canvas.concat(cameraMatrix);

        Rect src = info.getBounds();
        RectF dst = new RectF(-(width / 2f), -(height / 2f), width / 2f, height / 2f);
        canvas.drawBitmap(mUndistortedBitmap, src, dst, mBlindPaint);


        canvas.restore();
        mCamera.restore();
    }

    public void setBlindMode(boolean isBlind) {
        isInBlindMode = isBlind;
        invalidate();
    }

    private List<BlindInfo> blindList;

    private void setupBlinds(int blindHeight) {
        if (blindHeight <= 0)
            throw new IllegalArgumentException("blindheight must be >0");

        int accumulatedHeight = 0;
        final int viewWidth = getWidth();

        blindList = new ArrayList<BlindInfo>();

        int blindCount = getHeight() / blindHeight + 1;
        int singleDelay = BLIND_TICK_MAX - 10;

        int pos = 0;
        do {
            blindList.add(new BlindInfo(0, accumulatedHeight, viewWidth, accumulatedHeight + blindHeight, singleDelay * (pos++)));
            accumulatedHeight += blindHeight;
            //break;
        } while (accumulatedHeight < getHeight());
    }

    //
    // Public interface
    public void setBackground(int id) {
        mBgDrawable = (BitmapDrawable) getResources().getDrawable(id);
        centerBgDrawable();
    }

    @Override
    public void setBackground(Drawable background) {
        mBgDrawable = (BitmapDrawable) background;
        centerBgDrawable();
    }

    private void centerBgDrawable() {
        if (mBgDrawable != null) {
            final DisplayMetrics dm = getResources().getDisplayMetrics();
            mBgDrawable.setTargetDensity(dm);
            mBgDrawable.setGravity(android.view.Gravity.CENTER);
            mBgDrawable.setBounds(0, 0, dm.widthPixels, dm.heightPixels);
        }
        postInvalidate();
    }

    public void setTick(int tick){
        blindTick = tick;
        postInvalidateOnAnimation();
       // invalidate();
    }

}

