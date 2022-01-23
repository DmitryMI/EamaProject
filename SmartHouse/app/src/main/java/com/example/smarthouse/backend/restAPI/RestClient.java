package com.example.smarthouse.backend.restAPI;

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

public class RestClient {

    private static RequestQueue requestQueueInstance;

    public static RequestQueue getRequestQueue(Context context) {
        if (requestQueueInstance == null) {
            requestQueueInstance = Volley.newRequestQueue(context);
        }

        return requestQueueInstance;
    }


    private final Context context;
    private final String serverBaseUrl;
    private final String apartmentUrl;
    private final String roomUrl;
    private final String applianceUrl;
    private final String variableUrl;

    public RestClient(String url, Context context) {
        this.serverBaseUrl = url;
        this.context = context;

        apartmentUrl = serverBaseUrl + "/api/Apartment";
        roomUrl = serverBaseUrl + "/api/Room/%i/";
        applianceUrl = serverBaseUrl + "/api/Appliance/%i/%i";
        variableUrl = serverBaseUrl + "/api/Appliance/%i/%i/%s";
    }

    public interface ObjectReceivedCallback<T> {
        void OnObjectReceived(T obj);

        void OnFail(String errorMessage);
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
                callback.OnFail(e.getMessage());
            }

            if (obj instanceof JsonReadable) {
                JsonReadable jsonReadable = (JsonReadable) obj;
                jsonReadable.FromJson(response);
                callback.OnObjectReceived(obj);
            } else {
                callback.OnFail(String.format("Type %s does not support JSON deserialization", listenForClass.getCanonicalName()));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callback.OnFail(error.getMessage());
        }
    }

    private static class StringResponseListener implements Response.Listener<String>, Response.ErrorListener {
        private final ObjectReceivedCallback<String> callback;

        public StringResponseListener(ObjectReceivedCallback<String> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(String response) {
            callback.OnObjectReceived(response);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            callback.OnFail(error.getMessage());
        }
    }

    private void SetVariable(int roomId, int applianceId, String variable, String value) {
        // TODO Invoke REST Put Method
        String url = String.format(apartmentUrl, roomId, applianceId, variable);

    }


    private void GetVariable(ObjectReceivedCallback<String> callback, int roomId, int applianceId, String variable) {
        String url = String.format(apartmentUrl, roomId, applianceId, variable);
        StringResponseListener responseListener = new StringResponseListener(callback);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, responseListener, responseListener);
        getRequestQueue(context).add(stringRequest);
    }

    private void GetAppliance(ObjectReceivedCallback<Appliance> callback, int roomId, int applianceId) {
        String url = String.format(apartmentUrl, roomId, applianceId);
        JsonResponseListener<Appliance> responseListener = new JsonResponseListener<>(Appliance.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }

    private void GetRoom(ObjectReceivedCallback<Room> callback, int roomId) {
        String url = String.format(roomUrl, roomId);
        JsonResponseListener<Room> responseListener = new JsonResponseListener<>(Room.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }

    private void GetApartment(ObjectReceivedCallback<Apartment> callback) {
        JsonResponseListener<Apartment> responseListener = new JsonResponseListener<>(Apartment.class, callback);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apartmentUrl, null, responseListener, responseListener);
        getRequestQueue(context).add(jsonObjectRequest);
    }
}
