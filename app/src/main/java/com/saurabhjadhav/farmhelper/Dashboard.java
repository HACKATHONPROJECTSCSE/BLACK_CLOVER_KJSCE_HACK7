package com.saurabhjadhav.farmhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.saurabhjadhav.farmhelper.DiseasesPrediction.DiseasesPredictor;

public class Dashboard extends AppCompatActivity {

    LinearLayout PlantDiseasesPredictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        PlantDiseasesPredictor = findViewById(R.id.PlantDieasesPrediction);

        PlantDiseasesPredictor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, DiseasesPredictor.class));
            }
        });

    }
}