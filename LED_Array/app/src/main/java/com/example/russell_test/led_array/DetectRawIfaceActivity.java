package com.example.russell_test.led_array;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.motorola.mod.IModManager;
import com.motorola.mod.ModDevice;
import com.motorola.mod.ModInterfaceDelegation;
import com.motorola.mod.ModManager;
import com.motorola.mod.ModProtocol;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

public class DetectRawIfaceActivity extends AppCompatActivity {

    Button turnOnLEDs;
    Button turnOffLEDs;
    Button applyColor;
    TextView doesSupportMods;
    TextView I2cTxt;
    EditText LEDColor;
    RAW_LED_Mgr mgr;
    ModManager mManager;
    List<ModDevice> mods;
    private Personality personality;

    /** Handler for events from mod device */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Personality.MSG_MOD_DEVICE:
                    /** Mod attach/detach */
                    ModDevice device = personality.getModDevice();
                    onModDevice(device);
                    break;
                case Personality.MSG_RAW_IO_READY:
                    /** Mod RAW I/O ready to use */
                    onRawInterfaceReady();
                    break;
                case Personality.MSG_RAW_IO_EXCEPTION:
                    /** Mod RAW I/O exception */
                    onIOException();
                    break;
                case Personality.MSG_RAW_REQUEST_PERMISSION:
                    /** Request grant RAW_PROTOCOL permission */
                    onRequestRawPermission();
                default:
                    Log.i(Constants.TAG, "MainActivity - Un-handle events: " + msg.what);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_raw_iface);

        initializeComponents();
        setupListeners();

        updateDisplay(supportsMods());
        initPersonality();
        //establishRAWComms();
    }

    private void initializeComponents() {
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

    private void setupListeners() {
        mgr = new RAW_LED_Mgr();
        turnOnLEDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.turnAllOn();
                boolean temp = personality.getRaw().executeRaw(mgr.getCmd());
                Toast.makeText(getApplicationContext(),"ExecuteCode: " + Arrays.toString(mgr.getCmd()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Data sent: " + temp,Toast.LENGTH_SHORT).show();

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });

        turnOffLEDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.turnAllOff();
                boolean temp = personality.getRaw().executeRaw(mgr.getCmd());
                Toast.makeText(getApplicationContext(),"ExecuteCode: " + Arrays.toString(mgr.getCmd()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Data sent: " + temp,Toast.LENGTH_SHORT).show();

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });

        applyColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mgr.changeAllLEDs((Integer.parseInt(LEDColor.getText().toString())));
                boolean temp = personality.getRaw().executeRaw(mgr.getCmd());
                Toast.makeText(getApplicationContext(),"ExecuteCode: " + Arrays.toString(mgr.getCmd()), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Data sent: " + temp,Toast.LENGTH_SHORT).show();

                I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + Arrays.toString(mgr.getCmd()));
            }
        });
    }

    private void updateDisplay(boolean updateData) {
        String supportText = doesSupportMods.getText().toString();
        doesSupportMods.setText(supportText + " " + updateData);

        mgr.turnAllOff();

        I2cTxt.setText("Command in human: " + mgr.getCmdString() + "\n\nCommand in computer: " + mgr.getCmd().toString());

    }

    private Boolean supportsMods() {
        Boolean doesSupport = false;
        if (ModManager.isModServicesAvailable(DetectRawIfaceActivity.this) == ModManager.SUCCESS) {
            doesSupport = true;
        }

        return doesSupport;
    }

    //private boolean RefRaw()
    //{
    //     List<ModInterfaceDelegation> devices =
    //            modManager.getModInterfaceDelegationsByProtocol(modDevice,
    //                    ModProtocol.Protocol.RAW);
    //    if (devices != null && !devices.isEmpty()) {
    //        // TODO: go through the whole devices list for multi connected devices.
    //        // Here simply operate the first device for this example.
    //        ModInterfaceDelegation device = devices.get(0);

    //        /**
    //         * Be care to strict follow Android policy, you need visibly asking for
    //         * grant permission.
    //         */
    //        if (getApplicationContext().checkSelfPermission(ModManager.PERMISSION_USE_RAW_PROTOCOL)
    //                != PackageManager.PERMISSION_GRANTED) {
    //            onRequestRawPermission();
    //        } else {
    //            /** The RAW_PROTOCOL permission already granted, open RAW I/O */
    //            getRawPfd(device);
    //            return true;
    //        }
    //    }
   // }

    private void establishRAWComms() {
        Intent intent = new Intent(ModManager.ACTION_BIND_MANAGER);
        intent.setComponent(ModManager.MOD_SERVICE_NAME);

        getApplicationContext().bindService(intent, new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                IModManager mMgrSrvc = IModManager.Stub.asInterface(binder);
                mManager = new ModManager(getApplicationContext(), mMgrSrvc);

                try {
                    mods = mManager.getModList(false);
                    if (mods != null && !mods.isEmpty()) {
                        List<ModInterfaceDelegation> rds =
                                mManager.getModInterfaceDelegationsByProtocol(mods.get(0),
                                        ModProtocol.Protocol.RAW);

                        if (checkSelfPermission(ModManager.PERMISSION_USE_RAW_PROTOCOL)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{ModManager.PERMISSION_USE_RAW_PROTOCOL},
                                    100);
                        } else {
                            Toast.makeText(getApplicationContext(), "You already have permissions", Toast.LENGTH_SHORT);
                        }

                        if (rds != null && !rds.isEmpty()) {
                            ModInterfaceDelegation raw = rds.get(0);


                        }

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }

        }, getApplicationContext().BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releasePersonality();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Initial MDK Personality interface */
        initPersonality();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /** Initial MDK Personality interface */
    private void initPersonality() {
        if (null == personality) {
            personality = new RAW_Comm(this, Constants.VID_MDK, Constants.PID_TEMPERATURE);
            personality.registerListener(handler);
        }
    }

    /** Clean up MDK Personality interface */
    private void releasePersonality() {
        SharedPreferences preference = getSharedPreferences("recordingRaw", MODE_PRIVATE);

        /** Clean up MDK Personality interface */
        if (null != personality) {
            personality.getRaw().executeRaw(Constants.RAW_CMD_STOP);
            personality.onDestroy();
            personality = null;
        }
    }

    /**
     * Mod device attach/detach
     */
    public void onModDevice(ModDevice device) {
        /** Moto Mods Status */
        /**
         * Get mod device's Product String, which should correspond to
         * the product name or the vendor internal's name.
         */

            if (null != device) {

                Toast.makeText(getApplicationContext(),"Vendor:" + device.getVendorId()+ "\nProduct:" + device.getProductId(),Toast.LENGTH_LONG).show();

                if ((device.getVendorId() == Constants.VID_MDK
                        && device.getProductId() == Constants.PID_TEMPERATURE)
                        || device.getVendorId() == Constants.VID_DEVELOPER) {
                    onRequestRawPermission();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean temp = personality.getRaw().executeRaw(Constants.RAW_CMD_INFO);
                            Toast.makeText(getApplicationContext(),"ExecuteCode: " + Constants.RAW_CMD_INFO, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Data sent: " + temp,Toast.LENGTH_SHORT).show();
                        }
                    }, 5);

                }
            }
        }




    /** Check current mod whether in developer mode */
    private boolean isMDKMod(ModDevice device) {
        if (device == null) {
            /** Mod is not available */
            return false;
        } else if (device.getVendorId() == Constants.VID_DEVELOPER
                && device.getProductId() == Constants.PID_DEVELOPER) {
            // MDK in developer mode
            return true;
        } else {
            // Check MDK
            return device.getVendorId() == Constants.VID_MDK;
        }
    }

    /** RAW I/O of attached mod device is ready to use */
    public void onRawInterfaceReady() {
        /**
         *  Personality has the RAW interface, query the information data via RAW command, the data
         *  will send back from MDK with flag TEMP_RAW_COMMAND_INFO and TEMP_RAW_COMMAND_CHALLENGE.
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                personality.getRaw().executeRaw(Constants.RAW_CMD_INFO);
            }
        }, 500);
    }

    /** Handle the IO issue when write / read */
    public void onIOException() {
    }

    /*
     * Beginning in Android 6.0 (API level 23), users grant permissions to apps while
     * the app is running, not when they install the app. App need check on and request
     * permission every time perform an operation.
    */
    public void onRequestRawPermission() {
        requestPermissions(new String[]{ModManager.PERMISSION_USE_RAW_PROTOCOL},
                100);
    }

    /** Handle permission request result */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (null != personality) {
                    /** Permission grant, try to check RAW I/O of mod device */
                    personality.getRaw().checkRawInterface();
                }
            } else {
                // TODO: user declined for RAW accessing permission.
                // You may need pop up a description dialog or other prompts to explain
                // the app cannot work without the permission granted.
            }
        }
    }

    /** Parse the data from mod device */
    private void parseResponse(int cmd, int size, byte[] payload) {
        if (cmd == Constants.TEMP_RAW_COMMAND_INFO) {
            /** Got information data from personality board */

            /**
             * Checking the size of payload before parse it to ensure sufficient bytes.
             * Payload array shall at least include the command head data, and exactly
             * same as expected size.
             */
            if (payload == null
                    || payload.length != size
                    || payload.length < Constants.CMD_INFO_HEAD_SIZE) {
                return;
            }

            int version = payload[Constants.CMD_INFO_VERSION_OFFSET];
            int reserved = payload[Constants.CMD_INFO_RESERVED_OFFSET];
            int latencyLow = payload[Constants.CMD_INFO_LATENCYLOW_OFFSET] & 0xFF;
            int latencyHigh = payload[Constants.CMD_INFO_LATENCYHIGH_OFFSET] & 0xFF;
            int max_latency = latencyHigh << 8 | latencyLow;

            StringBuilder name = new StringBuilder();
            for (int i = Constants.CMD_INFO_NAME_OFFSET; i < size - Constants.CMD_INFO_HEAD_SIZE; i++) {
                if (payload[i] != 0) {
                    name.append((char) payload[i]);
                } else {
                    break;
                }
            }
            Log.i(Constants.TAG, "command: " + cmd
                    + " size: " + size
                    + " version: " + version
                    + " reserved: " + reserved
                    + " name: " + name.toString()
                    + " latency: " + max_latency);
        } else if (cmd == Constants.TEMP_RAW_COMMAND_CHALLENGE) {
            /** Got CHALLENGE command from personality board */

            /** Checking the size of payload before parse it to ensure sufficient bytes. */
            if (payload == null
                    || payload.length != size
                    || payload.length != Constants.CMD_CHALLENGE_SIZE) {
                return;
            }

            byte[] resp = Constants.getAESECBDecryptor(Constants.AES_ECB_KEY, payload);
            if (resp != null) {
                /** Got decoded CHALLENGE payload */
                ByteBuffer buffer = ByteBuffer.wrap(resp);
                buffer.order(ByteOrder.LITTLE_ENDIAN); // lsb -> msb
                long littleLong = buffer.getLong();
                littleLong += Constants.CHALLENGE_ADDATION;

                ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).order(ByteOrder.LITTLE_ENDIAN);
                buf.putLong(littleLong);
                byte[] respData = buf.array();

                /** Send challenge response back to mod device */
                byte[] aes = Constants.getAESECBEncryptor(Constants.AES_ECB_KEY, respData);
                if (aes != null) {
                    byte[] challenge = new byte[aes.length + 2];
                    challenge[0] = Constants.TEMP_RAW_COMMAND_CHLGE_RESP;
                    challenge[1] = (byte) aes.length;
                    System.arraycopy(aes, 0, challenge, 2, aes.length);
                    personality.getRaw().executeRaw(challenge);
                } else {
                    Log.e(Constants.TAG, "AES encrypt failed.");
                }
            } else {
                Log.e(Constants.TAG, "AES decrypt failed.");
            }
        }
    }
}
