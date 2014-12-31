package com.seeyou.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.seeyou.R;

import java.util.HashMap;
import java.util.Map;

public class TheButton extends View {

    Paint innerPaint, outerPaint, middlePaint, textPaint;

    float innerRadius, outerRadius, middleStrokeWidth, strokeWidth, padding;
    int translationX, translationY;

    Point lastTouch;

    private String text;

    public TheButton(Context context) {
        super(context);
        init();
    }

    public TheButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TheButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        Resources res = getResources();
        innerRadius = res.getDimensionPixelSize(R.dimen.tb_inner_radius);
        outerRadius = res.getDimensionPixelSize(R.dimen.tb_outer_radius);
        strokeWidth = res.getDimensionPixelSize(R.dimen.tb_stroke_width);
        middleStrokeWidth = outerRadius - innerRadius;
        padding = res.getDimensionPixelSize(R.dimen.tb_padding);

        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setColor(res.getColor(android.R.color.white));
        innerPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setColor(res.getColor(android.R.color.white));
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth(strokeWidth);

        middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middlePaint.setColor(res.getColor(R.color.background_grey));
        middlePaint.setStyle(Paint.Style.STROKE);
        middlePaint.setStrokeWidth(middleStrokeWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(res.getColor(android.R.color.holo_green_light));
        textPaint.setStrokeWidth(4);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(res.getDimensionPixelSize(R.dimen.tb_text_size));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int)(outerRadius * 3 + padding);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int px = measuredWidth / 2;
        int py = measuredHeight / 2;


        canvas.drawCircle(px, py, (outerRadius+innerRadius) / 2, middlePaint);
        canvas.drawCircle(px, py, outerRadius, outerPaint);


        canvas.save();
        canvas.translate(translationX, translationY);
        canvas.drawCircle(px, py, innerRadius, innerPaint);

        if(!TextUtils.isEmpty(text)) {
            float textWidth = textPaint.measureText(text);
            float textHeight = textPaint.measureText("w");
            canvas.drawText(text, px - textWidth / 2, py + textHeight / 2, textPaint);
        }
        canvas.restore();

    }


    int clickThreshold = 50;

    public void translate(Point touchPoint){
        lastTouch = touchPoint;

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int px = measuredWidth / 2;
        int py = measuredHeight / 2;

        int touchX = touchPoint.x;
        int touchY = touchPoint.y;

        int x = touchX - px;
        int y = touchY - py;

        if (Math.abs(x) > innerRadius/2)
           x = (x < 0 ? -1 : 1) * (int) innerRadius / 2;

        if(Math.abs(y) > innerRadius/2)
           y = (y < 0 ? -1 : 1) * (int)innerRadius/2;

        translationX = x;
        translationY = y;



        invalidate();
    }

    final String separator = "  |  ";
    public void setText(String left, String right){
        StringBuilder builder = new StringBuilder();

        boolean hasLeft = !TextUtils.isEmpty(left);
        boolean hasRight = !TextUtils.isEmpty(right);

        if (hasLeft) {
            builder.append(left);
            if (hasRight) {
                builder.append(separator).append(right);
            }
        } else if (hasRight) {
            builder.append(right);
        }

        text = builder.toString();
    }

    public void resetTranslation(){
        translationX = translationY = 0;
        invalidate();
    }

    public void resolveAction(long duration) {
        Action action = Action.NONE;
        float offset = innerRadius/2;

        if(duration <200)
            action = Action.CLICK;
        else if(Math.abs(translationX) == Math.abs(translationY))
            action = Action.NONE;
        else if(Math.abs(translationX) == offset){
            if(translationX > 0)
                action = Action.RIGHT;
            else
                action = Action.LEFT;
        } else if(Math.abs(translationY) == offset){
            if(translationY > 0)
                action = Action.DOWN;
            else
                action = Action.UP;
        }
        TheButtonCommand command = getButtonCommandForAction(action);
        if(command != null)
            command.execute();
    }

    Map<Action, TheButtonCommand> commandsMap = new HashMap<Action, TheButtonCommand>(5);
    public void setButtonCommand(Action action, TheButtonCommand command){
        commandsMap.put(action, command);
    }

    public TheButtonCommand getButtonCommandForAction(Action action){
        return commandsMap.get(action);
    }

    public static enum Action{
        NONE, LEFT, RIGHT, UP, DOWN, CLICK;
    }

    public interface TheButtonCommand{
        void execute();
    }
}
