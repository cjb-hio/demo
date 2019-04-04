package com.xyw.smartlock.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by HP on 2017/5/10.
 */

public class BluetoothUtils {
    private static BluetoothUtils mBluetoothUtils;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;

    public static BluetoothUtils getInstance() {
        if (null == mBluetoothUtils) {
            synchronized (ActivityUtils.class) {
                if (null == mBluetoothUtils) {
                    mBluetoothUtils = new BluetoothUtils();
                }
            }
        }
        return mBluetoothUtils;
    }

    public void init(Context context) {
        this.mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isHasBluetooth() {
        return mBluetoothAdapter != null;
    }

    public boolean isOpenBluetooth() {
        return mBluetoothAdapter.isEnabled();
    }
}
