package com.example.smarthouse.backend.restAPI;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthouse.backend.deviceTree.types.Apartment;
import com.google.common.util.concurrent.ListenableFuture;

public class SynchronisationWorker extends ListenableWorker {
    private Apartment apartment;
    private String serverIP = "192.168.178.20";
    private int serverPort = 5000;

    public SynchronisationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

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
        String url = serverIP + ":" + serverPort + "/api/Apartment";
        ResponseListener listener = new ResponseListener();
        StringRequest stringRequest = new StringRequest(url, listener, listener);

        queue.add(stringRequest);
    }

    private void updateTree() {

    }
}
