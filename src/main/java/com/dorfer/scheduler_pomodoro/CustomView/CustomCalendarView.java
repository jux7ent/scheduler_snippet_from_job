package com.dorfer.scheduler_pomodoro.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.CalendarView;

public class CustomCalendarView extends CalendarView {

    private static final String TAG = CustomCalendarView.class.toString();

    public CustomCalendarView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
