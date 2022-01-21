package com.example.smarthouse.backend.restAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthouse.backend.deviceTree.Apartment;

public class SynchronisationWorker extends Worker {
    private Apartment apartment;
    private String serverIP = "localhost";
    private int serverPort = 8080;

    public SynchronisationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (apartment == null) {
            getTree();
        } else {
            updateTree();
        }
        return null;
    }

    private void getTree() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        class ResponseListener implements Response.Listener<String>, Response.ErrorListener {

            @Override
            public void onResponse(String response) {
                Log.i("smartHouseWorker", response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        String url = serverIP + ":" + serverPort + "/Apartment";
        ResponseListener listener = new ResponseListener();
        StringRequest stringRequest = new StringRequest(url, listener, listener);

        queue.add(stringRequest);
    }

    private void updateTree() {

    }
}
