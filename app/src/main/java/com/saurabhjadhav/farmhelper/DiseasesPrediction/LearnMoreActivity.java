package com.saurabhjadhav.farmhelper.DiseasesPrediction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.saurabhjadhav.farmhelper.MainActivity;
import com.saurabhjadhav.farmhelper.R;

public class LearnMoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more);

        Intent intent = getIntent();
        String disease = intent.getStringExtra("disease");

        TextView disease_name;
        TextView disease_description;

        disease_name=findViewById(R.id.disease_name);
        disease_description=findViewById(R.id.disease_description);


        disease_name.setText(disease);


        String url = "https://web-production-226d.up.railway.app/wiki-summary?query=";
        url = url + disease_name;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(LearnMoreActivity.this, response, Toast.LENGTH_SHORT).show();
                        disease_description.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LearnMoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }
}