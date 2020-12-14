package com.dorfer.scheduler_pomodoro.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircularIndicatorView extends View {

    private static final String TAG = CircularIndicatorView.class.getName();

    private static final int DEFAULT_FG_COLOR = 0xffff0000;
    private static final int PRESSED_FG_COLOR = 0xff0000ff;
    private static final int DEFAULT_BG_COLOR = 0xffa0a0a0;

    private Paint foregroundPaint;
    private Paint backgroundPaint;
    private float selectedAngle;
    private Path clipPath;
    private Canvas mCanvas;

    private int mHorMargin, mVerMargin, mCircleSize;

    public CircularIndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        foregroundPaint = new Paint();
        foregroundPaint.setColor(DEFAULT_FG_COLOR);
        foregroundPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(DEFAULT_BG_COLOR);
        backgroundPaint.setStyle(Paint.Style.FILL);

        selectedAngle = 280;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        int circleSize = getWidth(); //because height > width(no landscape orientation)

        if (getHeight() < circleSize)
            circleSize = getHeight();

        circleSize *= 0.75;

        int horMargin = (getWidth() - circleSize) / 2;
        int verMargin = (getHeight() - circleSize) / 2;

        if (clipPath == null) {
            int clipWidth = (int) (circleSize * 0.80);

            int clipX = (getWidth() - clipWidth) / 2;
            int clipY = (getHeight() - clipWidth) / 2;
            clipPath = new Path();
            clipPath.addArc(clipX, clipY,
                    clipX + clipWidth, clipY + clipWidth,
                    0, 360);
        }

        canvas.clipRect(0, 0, getWidth(), getHeight());
        canvas.clipPath(clipPath, Region.Op.DIFFERENCE);

        canvas.save();
        canvas.rotate(-90, getWidth() / 2, getHeight() / 2);

        canvas.drawArc(horMargin, verMargin,
                horMargin + circleSize, verMargin + circleSize,
                0, 360,
                true, backgroundPaint);

        canvas.drawArc(horMargin, verMargin,
                horMargin + circleSize, verMargin + circleSize,
                0, selectedAngle,
                true, foregroundPaint);

        canvas.restore();
    }

    public void setAngle(float percentage, boolean isUI) {
        percentage = 1 - percentage;
        Log.d("myTag", "setAngle: " + percentage * 360);
        selectedAngle = (percentage * 360);

        if (isUI) {
            invalidate();
        }
        else {
            postInvalidate();
        }
    }

    public void setAngle(float angle) {
        setAngle(angle, true);
    }
}
