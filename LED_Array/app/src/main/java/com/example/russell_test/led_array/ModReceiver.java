package com.example.russell_test.led_array;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;

import com.motorola.mod.ModManager;

import static android.support.v4.widget.ExploreByTouchHelper.INVALID_ID;

/**
 * Created by Russell-Test on 1/21/2017.
 */

public class ModReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null ){
            if(action.equals(ModManager.ACTION_MOD_ATTACH)){
                int vid = intent.getIntExtra(ModManager.EXTRA_VENDOR_ID,
                        INVALID_ID);
                int pid = intent.getIntExtra(ModManager.EXTRA_PRODUCT_ID,
                        INVALID_ID);
                if ((vid == 42) && (pid == 1)) {
                    //getRawPfd(device);
                }
            }
        }
    }
}
