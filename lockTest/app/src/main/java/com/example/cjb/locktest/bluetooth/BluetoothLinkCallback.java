package com.example.cjb.locktest.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import com.example.cjb.locktest.factory.base.ILinkCallback;

public abstract class BluetoothLinkCallback implements ILinkCallback {

    public abstract void onScanResult(int callbackType, ScanResult result);

}
