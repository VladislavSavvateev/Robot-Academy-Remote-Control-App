package ru.apptrust.robotacademyremotecontrolapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

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
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                y = event.getY() - pointerBitmap.getHeight() / 2;
                speed = - (int) ((event.getY() - getHeight() / 2) / (getHeight()/2) * 100);
                if (speed > 100) speed = 100;
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                y = this.getHeight() / 2 - pointerBitmap.getHeight() / 2; // TODO: getHeight Должен браться из canvas!
                speed = 0;
                invalidate();
                break;
            }
        }

        ((MainActivity) this.getContext() ).speed.setText(String.valueOf(speed));
        Byte text = (byte) speed;
        ((MainActivity) this.getContext() ).speedBytes.setText(Byte.toString((byte) speed));
          
        try {
            if (MainActivity.socket != null) sendSpeedEngine((byte) speed);
        } catch (Throwable ignored) {}

        return true;
    }

    void sendSpeedEngine(byte speed) throws Throwable {
        byte by = 1;
        byte by2 = speed;
        byte[] arr = new byte[]{13, 0, 0, 0, -128, 0, 0, -92, 0, by, -127, by2, -90, 0, by};

        /*
        byte[] arr = new byte[14];
        arr[0] = 12;
        arr[1] = 0;

        arr[2] = 0;
        arr[3] = 0;

        arr[4] = -128;

        arr[5] = 0;
        arr[6] = 0;

        arr[7] = -91;
        arr[8] = 0;
        arr[9] = 1;
        arr[10] = 50; // speed

        arr[11] = -90;
        arr[12] = 0; // порт
        arr[13] = 1;
         */
        arr[10] = speed;

        arr[11] = -90;
        arr[12] = 0;
        arr[13] = 1;

        // Log.d("DEBUG", printByteArray(arr)); // debug
        MainActivity.os.write(arr);
        MainActivity.os.flush();
    }

    static String printByteArray(byte[] arr) {
        String result = "";
        for (int i = 0; i < arr.length; i++) {
            String half = Integer.toHexString(arr[i]);
            if (half.length() % 2 == 1) half = "0" + half;
            half = half.substring(half.length() - 2).toUpperCase();

            result += half;
        }
        return result;
    }
}