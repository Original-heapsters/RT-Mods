package com.example.russell_test.led_array;

import android.content.IntentFilter;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.motorola.mod.ModInterfaceDelegation;
import com.motorola.mod.ModManager;

import org.w3c.dom.Text;

import java.util.Arrays;

public class DetectRawIfaceActivity extends AppCompatActivity {

    Button turnOnLEDs;
    Button turnOffLEDs;
    Button applyColor;
    TextView doesSupportMods;
    TextView I2cTxt;
    EditText LEDColor;
    RAW_LED_Mgr mgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_raw_iface);

        initializeComponents();
        setupListeners();

        updateDisplay(supportsMods());
    }

    private void initializeComponents()
    {
        turnOnLEDs = (Button) findViewById(R.id.TurnOnLEDBtn);
        turnOffLEDs = (Button) findViewById(R.id.TurnOffLEDBtn);
        applyColor = (Button) findViewById(R.id.ApplyNewColor);
        doesSupportMods = (TextView) findViewById(R.id.SupportsModsPreview);
        I2cTxt = (TextView) findViewById(R.id.I2CCommand);
        LEDColor = (EditText) findViewById(R.id.InputLEDValue);

        ModReceiver modReceiver = new ModReceiver();
        IntentFilter filter = new IntentFilter(ModManager.ACTION_MOD_ATTACH);
        filter.addAction(ModManager.ACTION_MOD_DETACH);
        getApplicationContext().registerReceiver(modReceiver, filter, ModManager.PERMISSION_MOD_INTERNAL, null);
    }

    private void setupListeners()
    {
        mgr = new RAW_LED_Mgr();
        turnOnLEDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.turnAllOn();

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });

        turnOffLEDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.turnAllOff();

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });

        applyColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.changeAllLEDs((Integer.parseInt(LEDColor.getText().toString())));

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });
    }

    private void updateDisplay(boolean updateData)
    {
        String supportText = doesSupportMods.getText().toString();
        doesSupportMods.setText(supportText + " " + updateData);

        mgr.turnAllOff();

        I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + mgr.getCmd().toString());

    }

    private Boolean supportsMods()
    {
        Boolean doesSupport = false;
        if(ModManager.isModServicesAvailable(DetectRawIfaceActivity.this) == ModManager.SUCCESS){
            doesSupport = true;
        }

        return doesSupport;
    }

    /*
    /** Get file description via ModManager for attached Moto Mod, to create RAW I/O
    private void getRawPfd(ModInterfaceDelegation device) {
        try {
            /** Get file description of this mod device
            parcelFD = modManager.openModInterface(device,
                    ParcelFileDescriptor.MODE_READ_WRITE);
            if (parcelFD != null) {
                try {
                    /**
                     * Get read / write file descriptor, For further details,
                     * refer to http://man7.org/linux/man-pages/man2/pipe.2.html

                    syncPipes = Os.pipe();
                } catch (ErrnoException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /** Create work threads for read / write data
                createSendingThread();
                createReceivingThread();

                if (null != sendingThread && null != receiveThread) {
                    /** Notify that RAW I/O is ready to use
                    onRawInterfaceReady();
                }
            } else {
                Log.e(SyncStateContract.Constants.TAG, "getRawPfd PFD null ");
            }
        } catch (RemoteException e) {
            Log.e(SyncStateContract.Constants.TAG, "openRawDevice exception " + e);
        }
    }
    */
}
