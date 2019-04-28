package com.example.cjb.locktest.tcp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.cjb.locktest.utils.TextInfo;
import com.example.cjb.locktest.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpSocket {
    private Handler handler;
    private String sendData;
    private String getData="ff";
    private TcpThread tcpThread;
    static private TcpSocket instance;

    static public TcpSocket getInstance() {
        synchronized (TcpSocket.class){
            if(instance==null){
                instance=new TcpSocket();
            }
            return instance;
        }

    }
    private TcpSocket(){

    }

    public String getInfo(String data) {
        //
        String str="";
        getData="ff";
        this.sendData=data;
        tcpThread=new TcpThread();
        tcpThread.start();
        long lstTime=System.currentTimeMillis();
        while(true){
            long curTime=System.currentTimeMillis();
            if((curTime-lstTime)>3000){
                return null;
            }else{
                synchronized (getData){
                    str=getData;
                }
                if(!str.startsWith("ff")){
                    return str;
                }
            }
        }
    }

    /**
     *
     */
    class TcpThread extends Thread{
        private Socket s;
        @Override
        public void run() {
            super.run();
            Log.i("test","tcpThread start");
            try{
                try{
                    //s = new Socket("192.168.43.30", 60000);//申请链接
                    s = new Socket("24014vp936.zicp.vip", 31514);//申请链接

                }catch (Exception e) {
                    Log.i("test","无法连接服务器");
                    TextInfo.getInstance().appenText("无法连接服务器");
                    return;

                }
                Log.i("test","open socket");
                OutputStream os = s.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.write(Utils.hexStr2bytes(sendData));
                Log.i("test","sendData:"+sendData);
                TextInfo.getInstance().appenText("sendData:"+sendData);
                InputStream inputStream = s.getInputStream();
                DataInputStream input = new DataInputStream(inputStream);
                byte[] b = new byte[10000];

                int length = input.read(b);
                byte[] t=new byte[length];
                System.arraycopy(b,0,t,0,length);
                if(length>0){
                    String str=Utils.bytes2String(t);
                    synchronized (getData){
                        getData=str;
                    }
                    TextInfo.getInstance().appenText("get from server:"+str);
                }
                s.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
