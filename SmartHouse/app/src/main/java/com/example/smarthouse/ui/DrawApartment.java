package com.example.smarthouse.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.Room;

public class DrawApartment extends View {

    Apartment apartment;

    private float scaleDefault = 80.0f;

    public DrawApartment(Context context) {
        super(context);
    }

    public void setApartment(Apartment apartment)
    {
        this.apartment = apartment;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(apartment == null)
        {
            return;
        }

        for (Room room : apartment.getRooms())
        {
            drawRoom(canvas, room);

            for(Appliance appliance : room.getAppliances())
            {
                drawAppliance(canvas, room, appliance);
            }
        }
    }

    private float getAutoScale()
    {
        // TODO Detect Scale automatically to fit the graphics best
        return scaleDefault;
    }

    private Point transformPoint(Canvas canvas, float x, float y)
    {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int canvasHalfWidth = canvasWidth / 2;
        int canvasHalfHeight = canvasHeight / 2;

        return new Point((int)(x + canvasHalfWidth), (int)(canvasHalfHeight - y));
    }

    private void drawRoom(Canvas canvas, Room room)
    {
        float scale = getAutoScale();

        int x = (int)(room.getRelativeX() * scale);
        int y = (int)(room.getRelativeY() * scale);
        int halfWidth = (int)(room.getWidth() * scale / 2);
        int halfHeight = (int)(room.getHeight() * scale / 2);

        Point leftTop = transformPoint(canvas, x - halfWidth, y - halfHeight);
        Point rightBottom = transformPoint(canvas, x + halfWidth, y + halfHeight);

        Paint roomBordersPaint = new Paint();
        roomBordersPaint.setStyle(Paint.Style.STROKE);
        roomBordersPaint.setStrokeWidth(8);

        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, roomBordersPaint);

        Paint roomNamePaint = new Paint();
        roomNamePaint.setTextSize(20);

        Point roomNamePosition = transformPoint(canvas, x, y);

        canvas.drawText(room.getName(), roomNamePosition.x, roomNamePosition.y, roomNamePaint);
    }

    private void drawAppliance(Canvas canvas, Room container, Appliance appliance)
    {
        // TODO Draw appliances
    }
}
