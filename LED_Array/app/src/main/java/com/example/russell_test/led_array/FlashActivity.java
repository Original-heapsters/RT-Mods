package com.example.russell_test.led_array;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FlashActivity extends AppCompatActivity {

    private Button toggleFlash;
    private TextView I2C_Preview;
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
        I2C_Preview = (TextView) findViewById(R.id.I2CPreview);
        LED_Sim = (LinearLayout) findViewById(R.id.LED_Activity);
    }

    private void createListeners()
    {
        toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BackGround().execute();
            }
        });

    }
    private class BackGround extends  AsyncTask <Void, String, Void> {

        @Override
        protected Void doInBackground(Void...params) {
            while(true){
                publishProgress("abc");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                publishProgress( "def" );
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate( String ...progress ){
            int color = Color.TRANSPARENT;
            Drawable background = LED_Sim.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();



            //ColorDrawable bgColor = ((ColorDrawable)LED_Sim.getBackground());
            if(color == getResources().getColor(R.color.colorPrimary))
            {
                I2C_Preview.setText("I2C Switch ON");
                LED_Sim.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            else
            {
                I2C_Preview.setText("I2C Switch OFF");
                LED_Sim.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    }

}
