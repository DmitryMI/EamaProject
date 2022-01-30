package com.example.smarthouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DevicesViewHolder> {

    List<String> machineArrayAdapter;
    Context contextViewAdapter;

    public RecyclerViewAdapter(Context context, List<String> machineArray) {
        contextViewAdapter = context;
        machineArrayAdapter = machineArray;


    }


    @NonNull
    @Override
    public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(contextViewAdapter);
        View view = inflater.inflate(R.layout.recyclervierow, parent, false);
        return new DevicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesViewHolder holder, int position) {
        holder.textView.setText(machineArrayAdapter.get(position));

    }


    @Override
    public int getItemCount() {
        return machineArrayAdapter.size();
    }

    public class DevicesViewHolder extends RecyclerView.ViewHolder {

        TextView textView;


        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView1);

        }
    }

}
