package com.example.smarthouse.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.smarthouse.backend.deviceTree.MockDeviceTreeClient;

public class DrawApartment extends View {

    Paint paint;
    Rect rect;
    MockDeviceTreeClient mockDeviceTreeClient;

    public DrawApartment(Context context) {
        super(context);
        paint = new Paint();
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);



        canvas.drawRect(50, 380, 350, 780, paint);
        canvas.drawRect(350, 380, 650, 780, paint);
        canvas.drawRect(650, 380, 1050, 580, paint);
        canvas.drawRect(650, 380, 1050, 780, paint);

    }
}
