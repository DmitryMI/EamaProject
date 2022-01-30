package com.example.smarthouse.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private Bitmap bitmapLightOn;
    private Bitmap bitmapLightOff;
    private Bitmap bitmapWashingMachine;
    private Bitmap bitmapTemperatureSensor;

    private static final float scaleDefault = 80.0f;
    private DeviceTreeService deviceTreeService;
    private LocationService locationService;
    private int activeRoomIndex = -1;
    private float autoScale;

    private boolean viewportInitialized = false;
    private float viewportCenterX;
    private float viewportCenterY;
    private float viewportScale = 1.0f;

    private float touchStartX;
    private float touchStartY;
    private float touchSize;
    private float moveCenterX;
    private float moveCenterY;

    public DrawApartment(Context context)
    {
        super(context);
    }
    public DrawApartment(Context context, android.util.AttributeSet attributeSet)
    {
        super(context, attributeSet);

        setupResources();
    }

    private void setupResources()
    {
        Resources res = getResources();
        bitmapLightOn = BitmapFactory.decodeResource(res, R.drawable.lighton);
        bitmapLightOff = BitmapFactory.decodeResource(res, R.drawable.lightoff);
        bitmapWashingMachine = BitmapFactory.decodeResource(res, R.drawable.washingmachine);
        bitmapTemperatureSensor = BitmapFactory.decodeResource(res, R.drawable.temp1);

    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!viewportInitialized)
        {
            viewportCenterX = getWidth() / 2.0f;
            viewportCenterY = getHeight() / 2.0f;
            viewportInitialized = true;
        }

        if (apartment == null) {
            return;
        }

        setAutoScale();

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
                if(motionEvent.getPointerCount() == 1) {
                    touchStartX = motionEvent.getX();
                    touchStartY = motionEvent.getY();
                }
                return true;
            case (MotionEvent.ACTION_MOVE) :
                if(!viewportInitialized)
                {
                    return true;
                }
                if(motionEvent.getPointerCount() == 2) {
                    float x0 = motionEvent.getX(0);
                    float y0 = motionEvent.getY(0);
                    float x1 = motionEvent.getX(1);
                    float y1 = motionEvent.getY(1);
                    float dx = x1 - x0;
                    float dy = y1 - y0;
                    float distance = (float)Math.sqrt(dx * dx + dy * dy);
                    float centerX = (x1 + x0) / 2;
                    float centerY = (y1 + y0) / 2;
                    float shiftX = centerX - moveCenterX;
                    float shiftY = centerY - moveCenterY;

                    if(touchSize == 0)
                    {
                        touchSize = distance;
                    }
                    else
                    {
                        float scaleChange = distance / touchSize;
                        viewportScale *= scaleChange;

                        viewportCenterX -= getWidth() / 2.0f;
                        viewportCenterX *= scaleChange;
                        viewportCenterX += getWidth() / 2.0f;

                        viewportCenterY -= getHeight() / 2.0f;
                        viewportCenterY *= scaleChange;
                        viewportCenterY += getHeight() / 2.0f;
                    }

                    if(moveCenterX != 0 && moveCenterY != 0)
                    {
                        viewportCenterX += shiftX;
                        viewportCenterY += shiftY;
                    }

                    moveCenterX = centerX;
                    moveCenterY = centerY;
                    touchSize = distance;

                    invalidate();
                }
                return true;
            case (MotionEvent.ACTION_UP) :
                float touchStopX = motionEvent.getX();
                float touchStopY = motionEvent.getY();
                if(Math.abs(touchStopX - touchStartX) < clickEventMaxDelta && Math.abs(touchStopY - touchStartY) < clickEventMaxDelta)
                {
                    onClick(touchStopX, touchStopY);
                    performClick();
                }

                touchSize = 0;
                moveCenterX = 0;
                moveCenterY = 0;

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
        boolean somethingClicked = false;
        for(Room room : apartment.getRooms())
        {
            for(Appliance appliance : room.getAppliances())
            {
                Rect boundingBox = getApplianceBoundingBox(room, appliance);
                if(boundingBox.contains((int)x, (int)y))
                {
                    onClick(x, y, boundingBox, room, appliance);
                    somethingClicked = true;
                    break;
                }
            }
            if(somethingClicked)
            {
                break;
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
            invalidate();
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

    public void setActiveRoomIndex(int roomIndex)
    {
        activeRoomIndex = roomIndex;
        invalidate();
    }

    private void setAutoScale()
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

    private float getCompositeScale() {
        return autoScale * viewportScale;
    }

    private int alightTextX(float originalPositionX, float textSize, int textLength)
    {
       return (int)(originalPositionX - textLength / 2 * textSize * 0.5);
    }

    private Point transformPoint(float x, float y) {

        //float scale = getAutoScale();
        int transformedX = (int) ((x * viewportScale * autoScale + viewportCenterX));
        int transformedY = (int) ((viewportCenterY - y * viewportScale * autoScale));

        return new Point(transformedX, transformedY);
    }

    private void drawRoom(Canvas canvas, Room room) {
        float scale = getCompositeScale();

        float x = (room.getRelativeX());
        float y = (room.getRelativeY());
        float halfWidth = (room.getWidth() / 2);
        float halfHeight = (room.getHeight() / 2);

        Point leftTop = transformPoint(x - halfWidth, y + halfHeight);
        Point rightBottom = transformPoint(x + halfWidth, y - halfHeight);

        Paint roomBordersPaint = new Paint();
        roomBordersPaint.setStyle(Paint.Style.STROKE);
        roomBordersPaint.setStrokeWidth(0.05f * scale);

        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, roomBordersPaint);

        if(room.getId() == activeRoomIndex)
        {
            Paint activeRoomPaint = new Paint();
            activeRoomPaint.setColor(Color.GREEN);
            activeRoomPaint.setAlpha(50);
            canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, activeRoomPaint);
        }

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
        float scale = getCompositeScale();
        float ratio = (float)bitmap.getWidth() / bitmap.getHeight();
        float widthScaled = size * scale;
        float heightScaled = widthScaled / ratio;
        return Bitmap.createScaledBitmap(
                bitmap, (int)widthScaled, (int)heightScaled, false);
    }

    private Rect getApplianceBoundingBox(Room room, Appliance appliance)
    {
        float relativeX = appliance.getRelativeX() + room.getRelativeX();
        float relativeY = appliance.getRelativeY() + room.getRelativeY();

        float halfWidth;
        float halfHeight;

        if (appliance instanceof LightSource) {
            halfWidth = (lightSourceSize / 2);
            halfHeight = (lightSourceSize / 2);
        } else if (appliance instanceof Machine) {
            halfWidth = (machineSize / 2);
            halfHeight = (machineSize / 2);
        } else if (appliance instanceof Sensor){
            halfWidth = (sensorSize / 2);
            halfHeight = (sensorSize / 2);
        }
        else
        {
            halfWidth = (machineSize / 2);
            halfHeight = (machineSize / 2);
        }

        Point leftTop = transformPoint((relativeX) - halfWidth, (relativeY) + halfHeight);
        Point rightTop = transformPoint((relativeX) + halfWidth, (relativeY) - halfHeight);
        return new Rect(leftTop.x, leftTop.y, rightTop.x, rightTop.y);
    }

    private void drawAppliance(Canvas canvas, Room room, Appliance appliance) {
        float scale = getCompositeScale();

        Rect boundingBox = getApplianceBoundingBox(room, appliance);

        if (appliance instanceof LightSource) {
            LightSource lightSource = (LightSource) appliance;
            if (lightSource.isOn()) {
                canvas.drawBitmap(resizedBitmap(bitmapLightOn, lightSourceSize), boundingBox.left, boundingBox.top, new Paint());
            } else {
                canvas.drawBitmap(resizedBitmap(bitmapLightOff, lightSourceSize), boundingBox.left, boundingBox.top, new Paint());
            }
        } else if (appliance instanceof WashingMachine) {
            canvas.drawBitmap(resizedBitmap(bitmapWashingMachine, machineSize), boundingBox.left, boundingBox.top, new Paint());

        } else if (appliance instanceof TemperatureSensor) {
            Bitmap resizedSensorBitmap = resizedBitmap(bitmapTemperatureSensor, sensorSize);
            canvas.drawBitmap(resizedSensorBitmap, boundingBox.left, boundingBox.top, new Paint());
            TemperatureSensor temperatureSensor = (TemperatureSensor) appliance;
            float temperature = temperatureSensor.getValue();

            float labelX = appliance.getRelativeX() + room.getRelativeX();
            float labelY = appliance.getRelativeY() + room.getRelativeY();

            Point temperatureLabelPosition = transformPoint(labelX, labelY);
            int yShifted = temperatureLabelPosition.y + boundingBox.height() + 10;
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






