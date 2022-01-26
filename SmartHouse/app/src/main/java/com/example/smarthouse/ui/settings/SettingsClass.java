package com.example.smarthouse.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthouse.MainActivity;
import com.example.smarthouse.R;

public class SettingsClass extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        Button button =(Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsClass.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
