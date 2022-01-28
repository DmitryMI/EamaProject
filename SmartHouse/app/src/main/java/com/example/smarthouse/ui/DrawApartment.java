package com.example.smarthouse.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.smarthouse.R;
import com.example.smarthouse.backend.deviceTree.DeviceTreeService;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.LightSource;
import com.example.smarthouse.backend.deviceTree.types.Machine;
import com.example.smarthouse.backend.deviceTree.types.Room;
import com.example.smarthouse.backend.deviceTree.types.Sensor;
import com.example.smarthouse.backend.deviceTree.types.TemperatureSensor;
import com.example.smarthouse.backend.deviceTree.types.WashingMachine;
import com.example.smarthouse.backend.location.LocationService;

import java.util.Locale;

public class DrawApartment extends View {

    Apartment apartment;

    private static final float lightSourceSize = 0.7f;
    private static final float sensorSize = 0.7f;
    private static final float machineSize = 1.3f;
    private static final float textSize = 0.3f;
    private static final int margin = 100;
    private static final int clickEventMaxDelta = 200;

    private static final float scaleDefault = 80.0f;
    private DeviceTreeService deviceTreeService;
    private LocationService locationService;
    private float autoScale;

    private float touchStartX;
    private float touchStartY;

    public DrawApartment(Context context) {
        super(context);
    }
    public DrawApartment(Context context, android.util.AttributeSet attributeSet)
    {
        super(context, attributeSet);
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

        setAutoScale(canvas);

        for (Room room : apartment.getRooms()) {
            drawRoom(canvas, room);

            for (Appliance appliance : room.getAppliances()) {
                drawAppliance(canvas, room, appliance);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        if(deviceTreeService == null)
        {
            return true;
        }

        int action = motionEvent.getAction();

        //Log.i("SmartHouse DrawApartment", motionEvent.toString());

        switch(action) {

            case (MotionEvent.ACTION_DOWN) :
                touchStartX = motionEvent.getX();
                touchStartY = motionEvent.getY();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                return true;
            case (MotionEvent.ACTION_UP) :
                float touchStopX = motionEvent.getX();
                float touchStopY = motionEvent.getY();
                if(Math.abs(touchStopX - touchStartX) < clickEventMaxDelta && Math.abs(touchStopY - touchStartY) < clickEventMaxDelta)
                {
                    onClick(touchStopX, touchStopY);
                    performClick();
                }
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                return true;
            default :
                return super.onTouchEvent(motionEvent);
        }
    }

    private void onClick(float x, float y)
    {
        for(Room room : apartment.getRooms())
        {
            for(Appliance appliance : room.getAppliances())
            {
                Rect boundingBox = getApplianceBoundingBox(room, appliance);
                if(boundingBox.contains((int)x, (int)y))
                {
                    onClick(x, y, boundingBox, room, appliance);
                }
            }
        }
    }

    private void onClick(float x, float y, Rect boundingBox, Room room, Appliance appliance)
    {
        Log.i("SmartHouse DrawApartment", appliance.getName() + " clicked");

        if(appliance instanceof LightSource)
        {
            LightSource lightSource = (LightSource) appliance;
            boolean isOn = lightSource.isOn();
            lightSource.setIsOn(!isOn);
            deviceTreeService.sendDeviceTree(apartment);
        }
        if(appliance instanceof WashingMachine)
        {
            WashingMachine washingMachine = (WashingMachine) appliance;
            // TODO Open Washing Machine activity
        }
    }

    @Override
    public boolean performClick()
    {
        return super.performClick();
    }

    private void setAutoScale(Canvas canvas)
    {
        if(apartment == null)
        {
            autoScale = scaleDefault;
            return;
        }

        float drawingRegionWidth = getWidth() - margin;
        float drawingRegionHeight = getHeight() - margin;

        float apartmentMinX = 0;
        float apartmentMinY = 0;
        float apartmentMaxX = 0;
        float apartmentMaxY = 0;
        for(Room room : apartment.getRooms())
        {
            float x = room.getRelativeX();
            float y = room.getRelativeY();
            float w = room.getWidth() / 2;
            float h = room.getHeight() / 2;

            float minX = x - w;
            if(minX < apartmentMinX)
            {
                apartmentMinX = minX;
            }

            float minY = y - h;
            if(minY < apartmentMinY)
            {
                apartmentMinY = minY;
            }

            float maxX = x + w;
            if(maxX > apartmentMaxX)
            {
                apartmentMaxX = maxX;
            }

            float maxY = y + h;
            if(maxY > apartmentMaxY)
            {
                apartmentMaxY = maxY;
            }
        }

        float apartmentWidth = apartmentMaxX - apartmentMinX;
        float apartmentHeight = apartmentMaxY - apartmentMinY;

        float xScale = drawingRegionWidth / apartmentWidth;
        float yScale = drawingRegionHeight / apartmentHeight;
        autoScale = Math.min(xScale, yScale);
    }

    private float getAutoScale() {
        return autoScale;
    }

    private int alightTextX(float originalPositionX, float textSize, int textLength)
    {
       return (int)(originalPositionX - textLength / 2 * textSize * 0.5);
    }

    private Point transformPoint(float x, float y) {
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        int canvasHalfWidth = canvasWidth / 2;
        int canvasHalfHeight = canvasHeight / 2;

        return new Point((int) (x + canvasHalfWidth), (int) (canvasHalfHeight - y));
    }

    private void drawRoom(Canvas canvas, Room room) {
        float scale = getAutoScale();

        float x = (room.getRelativeX() * scale);
        float y = (room.getRelativeY() * scale);
        float halfWidth = (room.getWidth() * scale / 2);
        float halfHeight = (room.getHeight() * scale / 2);

        Point leftTop = transformPoint(x - halfWidth, y + halfHeight);
        Point rightBottom = transformPoint(x + halfWidth, y - halfHeight);

        Paint roomBordersPaint = new Paint();
        roomBordersPaint.setStyle(Paint.Style.STROKE);
        roomBordersPaint.setStrokeWidth(8);

        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, roomBordersPaint);

        Paint roomNamePaint = new Paint();
        float fontSize = textSize * scale;
        roomNamePaint.setTextSize(fontSize);

        Point roomNamePosition = transformPoint(x, y);

        String roomName = room.getName();
        //int xCentered = (int)(roomNamePosition.x - roomName.length() / 2 * fontSize * 0.5);
        int xCentered = alightTextX(roomNamePosition.x, fontSize, roomName.length());
        int yOffsetted = (int)(roomNamePosition.y - 0.5 * scale);
        canvas.drawText(roomName, xCentered, yOffsetted, roomNamePaint);
    }

    private Bitmap resizedBitmap(Bitmap bitmap, float size) {
        float scale = getAutoScale();
        float ratio = (float)bitmap.getWidth() / bitmap.getHeight();
        float widthScaled = size * scale;
        float heightScaled = widthScaled / ratio;
        return Bitmap.createScaledBitmap(
                bitmap, (int)widthScaled, (int)heightScaled, false);
    }

    private Rect getApplianceBoundingBox(Room room, Appliance appliance)
    {
        float scale = getAutoScale();

        float relativeX = appliance.getRelativeX() + room.getRelativeX();
        float relativeY = appliance.getRelativeY() + room.getRelativeY();

        float x = (relativeX * scale);
        float y = (relativeY * scale);

        float halfWidth;
        float halfHeight;

        if (appliance instanceof LightSource) {
            halfWidth = (lightSourceSize * scale / 2);
            halfHeight = (lightSourceSize * scale / 2);
        } else if (appliance instanceof Machine) {
            halfWidth = (machineSize * scale / 2);
            halfHeight = (machineSize * scale / 2);
        } else if (appliance instanceof Sensor){
            halfWidth = (sensorSize * scale / 2);
            halfHeight = (sensorSize * scale / 2);
        }
        else
        {
            halfWidth = (machineSize * scale / 2);
            halfHeight = (machineSize * scale / 2);
        }

        Point leftTop = transformPoint(x - halfWidth, y + halfHeight);
        Point rightTop = transformPoint(x + halfWidth, y - halfHeight);
        return new Rect(leftTop.x, leftTop.y, rightTop.x, rightTop.y);
    }

    private void drawAppliance(Canvas canvas, Room room, Appliance appliance) {
        float scale = getAutoScale();

        Resources res = getResources();

        Rect boundingBox = getApplianceBoundingBox(room, appliance);

        if (appliance instanceof LightSource) {
            LightSource lightSource = (LightSource) appliance;
            if (lightSource.isOn()) {
                Bitmap bitmapLightOn = BitmapFactory.decodeResource(res, R.drawable.lighton);
                canvas.drawBitmap(resizedBitmap(bitmapLightOn, lightSourceSize), boundingBox.left, boundingBox.top, new Paint());
            } else {
                Bitmap bitmapLightOff = BitmapFactory.decodeResource(res, R.drawable.lightoff);
                canvas.drawBitmap(resizedBitmap(bitmapLightOff, lightSourceSize), boundingBox.left, boundingBox.top, new Paint());
            }
        } else if (appliance instanceof WashingMachine) {

            Bitmap bitmapWashingMachine = BitmapFactory.decodeResource(res, R.drawable.washingmachine);
            canvas.drawBitmap(resizedBitmap(bitmapWashingMachine, machineSize), boundingBox.left, boundingBox.top, new Paint());

        } else if (appliance instanceof TemperatureSensor) {
            Bitmap bitmapTemperatureSensor = BitmapFactory.decodeResource(res, R.drawable.temp1);
            Bitmap resizedSensorBitmap = resizedBitmap(bitmapTemperatureSensor, sensorSize);
            canvas.drawBitmap(resizedSensorBitmap, boundingBox.left, boundingBox.top, new Paint());
            TemperatureSensor temperatureSensor = (TemperatureSensor) appliance;
            float temperature = temperatureSensor.getValue();

            float labelX = appliance.getRelativeX() + room.getRelativeX();
            float labelY = appliance.getRelativeY() + room.getRelativeY();

            Point temperatureLabelPosition = transformPoint(labelX * scale, labelY * scale);
            int yShifted = temperatureLabelPosition.y + resizedSensorBitmap.getHeight() + 10;
            Paint temperaturePaint = new Paint();
            float fontSize = textSize * scale;
            temperaturePaint.setTextSize(fontSize);

            String temperatureText = String.format(Locale.US, "%2.1f C", temperature);
            int xCentered = alightTextX(temperatureLabelPosition.x, fontSize, temperatureText.length());
            //int xCentered = temperatureLabelPosition.x;
            canvas.drawText(temperatureText, xCentered, yShifted, temperaturePaint);
        }
    }

    public void setDeviceTreeService(DeviceTreeService deviceTreeService) {
        this.deviceTreeService = deviceTreeService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}






