package ru.apptrust.robotacademyremotecontrolapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rares on 14.05.2017.
 */

public class SpeedTrackSlider extends View {
    public Canvas canvas;
    Bitmap trackBitmap;
    Bitmap pointerBitmap;
    float x,y;
    public int speed = 0;

    public SpeedTrackSlider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        trackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.speedtrack);
        pointerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.canvas == null) {
            trackBitmap = Bitmap.createScaledBitmap(trackBitmap, canvas.getWidth(), canvas.getHeight(), false);
            pointerBitmap = Bitmap.createScaledBitmap(pointerBitmap, trackBitmap.getWidth() / 3, trackBitmap.getWidth() / 3, false);
        }
        canvas.drawBitmap(trackBitmap, 0, 0, new Paint());
        x = canvas.getWidth() / 2 - (pointerBitmap.getWidth() / 2);

        if ( this.canvas == null) {
            canvas.drawBitmap(pointerBitmap, x, this.getHeight() / 2 - (pointerBitmap.getHeight() / 2), new Paint());
        } else {
            canvas.drawBitmap(pointerBitmap, x, y, new Paint());
        }



        //canvas.drawBitmap(trackBitmap, 0, 0, new Paint());
        this.canvas = canvas;
        //canvas.drawColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                y = event.getY() - pointerBitmap.getHeight() / 2;
                speed = - (int) ((event.getY() - getHeight() / 2) / getHeight()/2 * 100);
                Log.d("Debug", Integer.toString(speed));
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                y = event.getY() - pointerBitmap.getHeight() / 2;
                speed = - (int) ((event.getY() - getHeight() / 2) / (getHeight()/2) * 100);
                if (speed > 100) speed = 100;
                Log.d("Debug", Integer.toString(speed));
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                y = this.getHeight() / 2 - pointerBitmap.getHeight() / 2; // TODO: getHeight Должен браться из canvas!
                speed = 0;
                invalidate();
                return true;
            }
        }

        return super.onTouchEvent(event);
    }
}
