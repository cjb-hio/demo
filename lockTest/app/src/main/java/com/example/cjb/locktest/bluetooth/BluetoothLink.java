package com.example.cjb.locktest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.cjb.locktest.factory.base.ILink;
import com.example.cjb.locktest.utils.TextInfo;
import com.example.cjb.locktest.utils.Utils;

import java.util.List;
import java.util.UUID;

public class BluetoothLink implements ILink {
    private boolean isScan=false;
    private BluetoothLinkCallback callback;
    private static String TAG="BluetoothLink";
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    BluetoothDevice device;
    private Context context;
    private BluetoothGatt bluetoothGatt;

    static private String mode2Uuid="6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    static private String readUuid="6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    static private String writeUuid="6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    BluetoothGattService writeBgs;
    BluetoothGattCharacteristic characteristic;

    public BluetoothLink(BluetoothLinkCallback callback){
        this.callback=callback;
    }
    @Override
    public void init(Context context) {
        this.context=context;
        bluetoothManager= (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter==null){
            Log.i(TAG,"bluetoothAdapter is null");
            return;
        }
        //开启蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        //开始扫描
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(scanCallback);
        isScan=true;
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("test","scan stop");
                bluetoothLeScanner.stopScan(scanCallback);
                isScan=false;
                TextInfo.getInstance().appenText("扫描结束");
            }
        },60000);

    }


    @Override
    public void connect(String address) {
        if(bluetoothAdapter==null){
            return ;
        }
        if(isScan==true){
            bluetoothLeScanner.stopScan(scanCallback);
            isScan=false;
        }

        device = bluetoothAdapter.getRemoteDevice(address);
        if(device==null){
            Log.i(TAG,"device is null");
            return;
        }

        bluetoothGatt = device.connectGatt( context,false,bluetoothGattCallback);
        if(bluetoothGatt==null){
            Log.i(TAG,"bluetoothGatt is null");
            return;
        }
        TextInfo.getInstance().appenText("连接中...");


    }

    @Override
    public void disconnect() {

        if (bluetoothGatt != null && bluetoothAdapter != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            TextInfo.getInstance().appenText("断开连接");
        }

    }

    public void close() {

        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt=null;
            TextInfo.getInstance().appenText("关闭连接");
        }

    }


    @Override
    public void write(byte[] frame) {

        byte[] b;
        Log.i("test","write:"+Utils.bytes2String(frame));
        if(bluetoothGatt==null||frame==null||frame.length==0){
            return;
        }

        String s=Utils.bytes2String(frame);
        TextInfo.getInstance().appenText("发送:"+s);
        writeBgs = bluetoothGatt.getService(UUID.fromString(mode2Uuid));
        if(writeBgs==null){
            disconnect();
            return;
        }

        characteristic = writeBgs.getCharacteristic(UUID.fromString(writeUuid));
        if(characteristic==null){
            disconnect();
            return;
        }

//        characteristic.setValue(frame);
//        bluetoothGatt.writeCharacteristic(characteristic);
        int pos=0;

        while(true){
            if((pos+20)<=frame.length){
                b=new byte[20];
                System.arraycopy(frame,pos,b,0,20);
                pos+=20;
                characteristic.setValue(b);
                bluetoothGatt.writeCharacteristic(characteristic);
            }else{
                b=new byte[frame.length-pos];
                System.arraycopy(frame,pos,b,0,frame.length-pos);
                characteristic.setValue(b);
                bluetoothGatt.writeCharacteristic(characteristic);
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    private ScanCallback scanCallback=new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            callback.onScanResult(callbackType,result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private BluetoothGattCallback bluetoothGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if(newState==BluetoothProfile.STATE_CONNECTED){
                Log.i("test","onConnect");

                TextInfo.getInstance().appenText("建立连接");
                gatt.discoverServices();

            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                //textView.append("断开连接\n");
                close();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if(status==0){
                List<BluetoothGattService> services = gatt.getServices();
                for(BluetoothGattService service:services){
                    Log.i("test","service uuid:"+service.getUuid().toString());
                }
                setNotify();
            }

        }

        private void setNotify() {

            if(bluetoothGatt==null){
                return;
            }
            writeBgs = bluetoothGatt.getService(UUID.fromString(mode2Uuid));
            if(writeBgs==null){
                disconnect();
                return;
            }

            characteristic = writeBgs.getCharacteristic(UUID.fromString(readUuid));
            if(characteristic==null){
                disconnect();
                return;
            }

            boolean b=bluetoothGatt.setCharacteristicNotification(characteristic,true);

            if(b==true){
                List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
                if(descriptorList != null && descriptorList.size() > 0) {
                    for(BluetoothGattDescriptor descriptor : descriptorList) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGatt.writeDescriptor(descriptor);
                    }
                }
            }

            //BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            //descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            //bluetoothGatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("test","read");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            Log.e("test","changed:"+Utils.bytes2String(value));
            callback.onReadData(value);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //开启监听成功，可以像设备写入命令了
                TextInfo.getInstance().appenText("打开读数据通知");
                callback.onConnected();
            }
        }
    };

}
