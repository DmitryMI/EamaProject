package com.example.smarthouse.backend.deviceTree;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.Room;

public abstract class DeviceTreeClient {

    public interface ObjectReceivedCallback<T> {
        void onObjectReceived(T obj);

        void onFail(String errorMessage);
    }

    public abstract void setVariable(int roomId, int applianceId, String variable, Object value);
    public abstract void getVariable(ObjectReceivedCallback<Object> callback, int roomId, int applianceId, String variable);
    public abstract void setApartment(Apartment apartment);
    public abstract void getAppliance(ObjectReceivedCallback<Appliance> callback, int roomId, int applianceId);
    public abstract void getRoom(ObjectReceivedCallback<Room> callback, int roomId);
    public abstract void getApartment(ObjectReceivedCallback<Apartment> callback);

}
