package com.example.russell_test.led_array;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button flash_activity_btn;
    private Button detect_raw_iface_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        createListeners();
    }

    private void initializeComponents()
    {
        flash_activity_btn = (Button) findViewById(R.id.flash_activity_btn);
        detect_raw_iface_btn = (Button) findViewById(R.id.detect_raw_iface_btn);


    }

    private void createListeners()
    {
        flash_activity_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FlashActivity.class));

            }
        });

        detect_raw_iface_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DetectRawIfaceActivity.class));
            }
        });
    }
}
