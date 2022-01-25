package com.example.smarthouse.backend.deviceTree;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.example.smarthouse.backend.deviceTree.types.Appliance;
import com.example.smarthouse.backend.deviceTree.types.JsonReadable;
import com.example.smarthouse.backend.deviceTree.types.Room;

import org.json.JSONObject;

public class RestClient extends DeviceTreeClient{

    private static RequestQueue requestQueueInstance;

    public static RequestQueue getRequestQueue(Context context) {
        if (requestQueueInstance == null) {
            requestQueueInstance = Volley.newRequestQueue(context);
        }

        return requestQueueInstance;
    }

    private final Context context;
    private final String apartmentUrl;
    private final String roomUrl;
    private final String applianceUrl;
    private final String variableUrl;

    public RestClient(String url, Context context) {
        this.context = context;

        apartmentUrl = url + "/api/Apartment";
        roomUrl = url + "/api/Room/%i/";
        applianceUrl = url + "/api/Appliance/%i/%i";
        variableUrl = url + "/api/Appliance/%i/%i/%s";
    }

    private static class JsonResponseListener<T> implements Response.Listener<JSONObject>, Response.ErrorListener {
        private final Class<T> listenForClass;
        private final ObjectReceivedCallback<T> callback;

        public JsonResponseListener(Class<T> listenForClass, ObjectReceivedCallback<T> callback) {
            this.listenForClass = listenForClass;
            this.callback = callback;
        }

        @Override
        public void onResponse(JSONObject response) {
            T obj = null;

            try {
                obj = listenForClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                callback.onFail(e.getMessage());
            }

            if (obj instanceof JsonReadable) {
                JsonReadable jsonReadable = (JsonReadable) obj;
                jsonReadable.FromJson(response);
                callback.onObjectReceived(obj);
            } else {
                callback.onFail(String.format("Type %s does not support JSON deserialization", listenForClass.getCanonicalName()));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callback.onFail(error.getMessage());
        }
    }

    private static class StringResponseListener implements Response.Listener<String>, Response.ErrorListener {
        private final ObjectReceivedCallback<Object> callback;

        public StringResponseListener(ObjectReceivedCallback<Object> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(String response) {
            callback.onObjectReceived(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callback.onFail(error.getMessage());
        }
    }

    @Override
    public void setVariable(int roomId, int applianceId, String variable, Object value) {
        // TODO Invoke REST Put Method
        String url = String.format(variableUrl, roomId, applianceId, variable);

    }

    @Override
    public void getVariable(ObjectReceivedCallback<Object> callback, int roomId, int applianceId, String variable) {
        String url = String.format(variableUrl, roomId, applianceId, variable);
        StringResponseListener responseListener = new StringResponseListener(callback);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, responseListener);
        getRequestQueue(context).add(stringRequest);
    }

    @Override
    public void getAppliance(ObjectReceivedCallback<Appliance> callback, int roomId, int applianceId) {
        String url = String.format(applianceUrl, roomId, applianceId);
        JsonResponseListener<Appliance> responseListener = new JsonResponseListener<>(Appliance.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }

    @Override
    public void getRoom(ObjectReceivedCallback<Room> callback, int roomId) {
        String url = String.format(roomUrl, roomId);
        JsonResponseListener<Room> responseListener = new JsonResponseListener<>(Room.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }

    @Override
    public void getApartment(ObjectReceivedCallback<Apartment> callback) {
        JsonResponseListener<Apartment> responseListener = new JsonResponseListener<>(Apartment.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apartmentUrl, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }
}
