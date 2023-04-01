package com.saurabhjadhav.farmhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.saurabhjadhav.farmhelper.ChatGptHelper.ChatGptHelper;
import com.saurabhjadhav.farmhelper.DiseasesPrediction.DiseasesPredictor;

public class Dashboard extends AppCompatActivity {

    LinearLayout PlantDiseasesPredictor, ChatBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        PlantDiseasesPredictor = findViewById(R.id.PlantDieasesPrediction);
        ChatBot = findViewById(R.id.ChatBot);

        PlantDiseasesPredictor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, DiseasesPredictor.class));
            }
        });

        ChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, ChatGptHelper.class));
            }
        });

    }
}