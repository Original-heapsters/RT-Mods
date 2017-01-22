package com.example.russell_test.led_array;

import java.util.Arrays;

/**
 * Created by Russell-Test on 1/21/2017.
 */

public class RAW_LED_Mgr {
    private byte[] cmd;

    RAW_LED_Mgr(){
        cmd = new byte[64];
    }

    RAW_LED_Mgr(byte[] Command)
    {
        cmd = Command;
    }

    public byte[] getCmd()
    {
        return cmd;
    }

    public String getCmdString()
    {
        String inHuman = this.byteArrayToHex(cmd);

        return inHuman;
    }

    public void turnAllOff()
    {
        Arrays.fill( cmd, (byte) 0 );
    }

    public void turnAllOn()
    {
        Arrays.fill( cmd, (byte) 1 );
    }

    public void changeAllLEDs(int val)
    {
        Arrays.fill( cmd, (byte) val );
    }

    public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

}
