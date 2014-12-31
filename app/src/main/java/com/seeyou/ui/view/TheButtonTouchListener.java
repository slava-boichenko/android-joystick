package com.seeyou.ui.view;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class TheButtonTouchListener implements View.OnTouchListener {
    private final TheButton theButton;
    private boolean isDragging;
    private MotionEvent lastEvent;

    public TheButtonTouchListener(TheButton theButton){
        this.theButton = theButton;
    }



    long pressStartTime, pressDuration;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event == null && lastEvent == null){
            return false;
        }else if(event == null && lastEvent != null){
            event = lastEvent;
        }else{
            lastEvent = event;
        }
        //drag drop
        if ( event.getAction() == MotionEvent.ACTION_DOWN ){
            isDragging = true;
            pressStartTime = System.currentTimeMillis();
        }else if (event.getAction() == MotionEvent.ACTION_UP){
            isDragging = false;
            pressDuration = System.currentTimeMillis() - pressStartTime;
        }

        Point touchPoint = new Point((int)event.getX(), (int)event.getY());

        if(isDragging){
            theButton.translate(touchPoint);
        } else {
            theButton.resolveAction(pressDuration);
            theButton.resetTranslation();
        }

        return true;
    }
}
