package com.example.cjb.locktest;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cjb.locktest.bluetooth.BluetoothLink;
import com.example.cjb.locktest.bluetooth.BluetoothLinkCallback;
import com.example.cjb.locktest.factory.LinkaManager;
import com.example.cjb.locktest.protocol.base.ClassFrame;
import com.example.cjb.locktest.protocol.base.Protocol11778;
import com.example.cjb.locktest.protocol.base.Sm1Manager;
import com.example.cjb.locktest.tcp.TcpSocket;
import com.example.cjb.locktest.utils.GpsUtils;
import com.example.cjb.locktest.utils.TextInfo;
import com.example.cjb.locktest.utils.TimerBuffer;
import com.example.cjb.locktest.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button readAdrButton;
    Button writeAdrButton;
    Button readCodeButton;
    Button writeCodeButton;
    Button lockButton;
    Button unlockButton;
    Button stateButton;
    Button testButton;
    Button xie;
    Button connectButton;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    TextView infoView;

    Button clearButton;
    MyCallback myCallback=new MyCallback();

    private AlertDialog dialog;
    ArrayList<BluetoothDevice> devices=new ArrayList<>();
    DialogListAdapter adapterhcsz;
    BluetoothLink bluetoothLink;
    Protocol11778 protocol;
    TimerBuffer timerBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initView();

        bluetoothLink=new BluetoothLink(new MyCallback());
        protocol=new Protocol11778(bluetoothLink);

        int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{(Manifest.permission.ACCESS_COARSE_LOCATION)}, 100);
        }

        checkGps();
        timerBuffer=new TimerBuffer(handler);
        adapterhcsz = new DialogListAdapter(this, android.R.layout.simple_list_item_1, devices,itemListener);  //string数组转换成arrayAdapter
        //************************************test****************************************


        //protocol.processData(Utils.hexStr2bytes("68 1c 00 43 05 07 09 19 05 16 20 00 0c 22 85 01 01 43 10 0b 00 01 16 08 00 00 00 33 2a 16"));

        //int h=Utils.hcsChech(Utils.hexStr2bytes(""));
        Log.i("test","---->");

    }

    private void checkGps() {
        if(!GpsUtils.checkGpsIsOpen(this)){
            GpsUtils.openGPSSEtting(this);
        }
    }

    private View.OnClickListener itemListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //onClick
            dialog.dismiss();
            DialogListAdapter.ViewHolder vh= (DialogListAdapter.ViewHolder) v.getTag();
            String address=vh.title.getText().toString().trim();
            bluetoothLink.connect(address);
        }
    };
    private void initView() {


        readAdrButton=findViewById(R.id.readAdr);
        readAdrButton.setOnClickListener(onClickListener);
        writeAdrButton=findViewById(R.id.writeAdr);
        writeAdrButton.setOnClickListener(onClickListener);
        readCodeButton=findViewById(R.id.readcode);
        readCodeButton.setOnClickListener(onClickListener);
        writeCodeButton=findViewById(R.id.writecode);
        writeCodeButton.setOnClickListener(onClickListener);
        lockButton=findViewById(R.id.close);
        lockButton.setOnClickListener(onClickListener);
        unlockButton=findViewById(R.id.open);
        unlockButton.setOnClickListener(onClickListener);
        stateButton=findViewById(R.id.state);
        stateButton.setOnClickListener(onClickListener);
        connectButton=findViewById(R.id.connect);
        connectButton.setOnClickListener(onClickListener);

        clearButton=findViewById(R.id.clear);
        clearButton.setOnClickListener(onClickListener);
        xie=findViewById(R.id.writeaddr);
        xie.setOnClickListener(onClickListener);
        testButton=findViewById(R.id.smtest);
        testButton.setOnClickListener(onClickListener);

        editText1=findViewById(R.id.editAdr);
        editText2=findViewById(R.id.editcode);
        editText3=findViewById(R.id.edit3);
        infoView=findViewById(R.id.info);
        infoView.setMovementMethod(ScrollingMovementMethod.getInstance());
        infoView.setTextIsSelectable(true);
        TextInfo.getInstance().init(infoView);
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String frame="";
            String temp="";
            if(protocol==null){
                return;
            }
            protocol.setServerAddress("201605190907");
            switch (view.getId()){
                case R.id.readAdr:
                    //读电能表的通信地址
                    TextInfo.getInstance().appenText("读取通信地址:");
                    protocol.read("4001",(byte)2,(byte)0);
                    break;
                case R.id.connect:

                    Sm1Manager.getInstance().init(protocol);
                    //infoView.append("建立应用连接:"+"\n");
                    //protocol.connect();
                    break;
                case R.id.readcode:
                    //读编码
                    TextInfo.getInstance().appenText("读取编码:");
                    protocol.read("4003",(byte)2,(byte)0);
                    break;
                case R.id.writeAdr:

//                    temp=editText1.getText().toString();
//                    if(temp.length()==0){
//                        return;
//                    }
//                    temp=Utils.bu(temp,12);
//                    temp=Utils.getObjectString(temp);
//
//                    infoView.append("设置通信地址:"+"\n");
//                    protocol.set("4001",(byte)2,(byte)0,temp);


                    String data=editText1.getText().toString();
                    data=Utils.trimA(data).toLowerCase();
                    if(data.length()==0){
                        data="803600020000";
                    }
                    int len=data.length()/2;
                    temp=Utils.inter2HexString(len,2);
                    temp=Utils.turnPer2(temp);
                    data=temp+data;
                    data=protocol.makeFrame("201605190907","43",10,data);

                    if(data.length()>0){
                        TextInfo.getInstance().appenText("透传:");
                        bluetoothLink.write(Utils.hexStr2bytes(data));
                    }

                    break;
                case R.id.writecode:
                    temp=editText2.getText().toString();
                    if(temp.length()==0){
                        return;
                    }
                    temp=Utils.bu(temp,16);
                    temp=Utils.getObjectString(temp);

                    TextInfo.getInstance().appenText("设置客户编码:");
                    protocol.set("4003",(byte)2,(byte)0,temp);
                    break;
                case R.id.writeaddr:
                    temp=editText3.getText().toString();
                    if(temp.length()==0){
                        return;
                    }
                    temp=Utils.bu(temp,16);
                    temp=Utils.getObjectString(temp);

                    TextInfo.getInstance().appenText("设置地址:");
                    protocol.set("4001",(byte)2,(byte)0,temp);
                    break;
                case R.id.open:
                    TextInfo.getInstance().appenText("开锁:");
                    //temp=Utils.getObjectString("12345678");
                    protocol.action("4310",(byte)0x7f,(byte)0,temp);
                    break;
                case R.id.close:
                    protocol.index=0;
                    TextInfo.getInstance().appenText("关锁:");
                    protocol.action("4310",(byte)0x80,(byte)0,"00");
                    break;
                case R.id.state:
                    //读状态
                    TextInfo.getInstance().appenText("读取锁状态:");
                    protocol.read("4310",(byte)0x0b,(byte)0);
                    break;
                case R.id.smtest:
                    //第一位：类别 0：秘钥协商
                    Sm1Manager.getInstance().setEsamId("1234567812345678");
                    Sm1Manager.getInstance().setNum("00000001");
                    //Sm1Manager.getInstance().setSessionVerify(Utils.makeTestStr(48),"00000001");
                    //Sm1Manager.getInstance().setKeyUpdate("0000000112345678");
                    //Sm1Manager.getInstance().setDataEpt("01",Utils.makeTestStr(156));
                    //Sm1Manager.getInstance().clearData("01","00000001",Utils.makeTestStr(156));

                    Log.i("test","Sm1Manager:"+Sm1Manager.getInstance().toString());
                    break;
                case R.id.clear:
                    TextInfo.getInstance().clear();
                    break;
            }
        }
    };
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.i("test","on Click");
                if(bluetoothLink!=null){
                    bluetoothLink.disconnect();
                }
                bluetoothLink.init(getApplicationContext());
                initDialog();
                TextInfo.getInstance().appenText("扫描中...");
                devices.clear();
                adapterhcsz.notifyDataSetChanged();
                return false;
            }
        });
    }

    /**
     * 初始化弹出框
     */
    private void initDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("搜索蓝牙");

        builder.setAdapter(adapterhcsz,null);
        dialog=builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void test(View view) {
        byte[] x=Utils.hexStr2bytes("55000156AA");
        bluetoothLink.write(x);
    }




    class MyCallback extends BluetoothLinkCallback{

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            Log.i("test","device discover:"+result.getDevice().getAddress());

            BluetoothDevice device = result.getDevice();
            if (!devices.contains(device)) {
                if(device.getName()==null){
                    return;
                }
                if(device.getName().startsWith("S")){
                    devices.add(device);
                    adapterhcsz.notifyDataSetChanged();
                }

            }
        }

        @Override
        public void onConnected() {
        }

        @Override
        public void onReadData(byte[] data) {
            //得到返回数据
            timerBuffer.setData(data,100);
            //protocol.processData(data);
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bluetoothLink!=null){
            bluetoothLink.disconnect();
        }
    }

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                byte[] ret= (byte[]) msg.obj;
                Log.i("test","str:"+Utils.bytes2String(ret)+" len:"+ret.length);
                if(protocol!=null){
                    protocol.processData(ret);
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 11) {
        }
    }
}
