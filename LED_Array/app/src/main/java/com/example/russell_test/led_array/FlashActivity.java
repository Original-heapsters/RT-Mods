package com.example.russell_test.led_array;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FlashActivity extends AppCompatActivity {

    private Button toggleFlash;
    private LinearLayout LED_Sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        initializeComponents();

        createListeners();
    }

    private void initializeComponents()
    {
        toggleFlash = (Button) findViewById(R.id.toggle_flash_btn);
        LED_Sim = (LinearLayout) findViewById(R.id.LED_Activity);
    }

    private void createListeners()
    {
        toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LED_Sim.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            }
        });

    }
}
