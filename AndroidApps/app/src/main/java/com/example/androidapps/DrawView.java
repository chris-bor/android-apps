package com.example.androidapps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

class DrawView extends View implements View.OnTouchListener {
    private Paint paint = new Paint();
    private List<Point> points = new ArrayList<>();

    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(Point point : points) {
            canvas.drawCircle(point.x, point.y, 30, paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Point point = new Point();
        point.x = (int) motionEvent.getX();
        point.y = (int) motionEvent.getY();
        points.add(point);
        invalidate();
        return true;
    }
}
