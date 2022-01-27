package com.example.smarthouse.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.example.smarthouse.R;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.LightSource;
import com.example.smarthouse.backend.deviceTree.types.Room;
import com.example.smarthouse.backend.deviceTree.types.TemperatureSensor;
import com.example.smarthouse.backend.deviceTree.types.WashingMachine;

public class DrawApartment extends View {

    Apartment apartment;

    private float scaleDefault = 80.0f;

    public DrawApartment(Context context) {
        super(context);
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (apartment == null) {
            return;
        }

        for (Room room : apartment.getRooms()) {
            drawRoom(canvas, room);

            for (Appliance appliance : room.getAppliances()) {
                drawAppliance(canvas, room, appliance);
            }
        }
    }

    private float getAutoScale() {
        // TODO Detect Scale automatically to fit the graphics best
        return scaleDefault;
    }

    private Point transformPoint(Canvas canvas, float x, float y) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int canvasHalfWidth = canvasWidth / 2;
        int canvasHalfHeight = canvasHeight / 2;

        return new Point((int) (x + canvasHalfWidth), (int) (canvasHalfHeight - y));
    }

    private void drawRoom(Canvas canvas, Room room) {
        float scale = getAutoScale();

        int x = (int) (room.getRelativeX() * scale);
        int y = (int) (room.getRelativeY() * scale);
        int halfWidth = (int) (room.getWidth() * scale / 2);
        int halfHeight = (int) (room.getHeight() * scale / 2);

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

    private Bitmap resizedBitmap(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bitmap, 40, 40, false);
        return resizedBitmap;
    }

    private void drawAppliance(Canvas canvas, Room room, Appliance appliance) {
        float scale = getAutoScale();

        Resources res = getResources();

        int x = (int) (appliance.getRelativeX() * scale);
        int y = (int) (appliance.getRelativeY() * scale);
        int halfWidth = (int) (appliance.getWidth() * scale / 2);
        int halfHeight = (int) (appliance.getHeight() * scale / 2);

        Point leftTop = transformPoint(canvas, x - halfWidth, y - halfHeight);

        if (appliance instanceof LightSource) {
            Bitmap bitmapLight = BitmapFactory.decodeResource(res, R.drawable.light);
            canvas.drawBitmap(resizedBitmap(bitmapLight), leftTop.x, leftTop.y, new Paint());

            LightSource lightSource = (LightSource) appliance;
            if (lightSource.isOn()) {
                Bitmap bitmapLightOn = BitmapFactory.decodeResource(res, R.drawable.lighton);
                canvas.drawBitmap(resizedBitmap(bitmapLightOn), leftTop.x, leftTop.y, new Paint());
            } else {
                Bitmap bitmapLightOff = BitmapFactory.decodeResource(res, R.drawable.lightoff);
                canvas.drawBitmap(resizedBitmap(bitmapLightOff), leftTop.x, leftTop.y, new Paint());
            }
        } else if (appliance instanceof WashingMachine) {
            Bitmap bitmapWashingMachine = BitmapFactory.decodeResource(res, R.drawable.washingmachine);
            canvas.drawBitmap(resizedBitmap(bitmapWashingMachine), leftTop.x, leftTop.y, new Paint());
        } else if (appliance instanceof TemperatureSensor) {
            Bitmap bitmapTemperatureSensor = BitmapFactory.decodeResource(res, R.drawable.temp1);
            canvas.drawBitmap(resizedBitmap(bitmapTemperatureSensor), leftTop.x, leftTop.y, new Paint());
            TemperatureSensor temperatureSensor = (TemperatureSensor) appliance;
            float temperature = temperatureSensor.getValue();
            // Draw temperature
        }
    }
}






