package com.example.cjb.locktest.utils;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TimerBuffer {

    private byte[] buff=new byte[1024];
    private int pos;

    public void setRun(boolean run) {
        synchronized (this){
            isRun = run;
        }
    }

    public TimerBuffer(Handler handler){
        this.handler=handler;
    }
    public void setData(byte[] x,int delay){
        synchronized (this){
            this.delay=delay;
            System.arraycopy(x,0,buff,pos,x.length);
            pos+=x.length;
            if(isRun==false){
                myThread=new MyThread();
                myThread.start();
                isRun=true;
            }else{
                long t=System.currentTimeMillis();
                lastTime=t;
            }

        }
    }


    private boolean isRun=false;

    public void setLastTime(long lastTime) {
        synchronized (this){
            this.lastTime = lastTime;
        }
    }

    public long getLastTime() {
        synchronized (this){
            return lastTime;
        }
    }


    private long lastTime;
    private int delay;
    private Handler handler;


    MyThread myThread;

    private class MyThread extends Thread{
        @Override
        public void run() {
            byte[] data;
            long t=System.currentTimeMillis();
            setLastTime(t);
            while(true){
                long curTime=System.currentTimeMillis();

                if((curTime-getLastTime())>delay){
                    Message message=new Message();
                    synchronized (TimerBuffer.class){
                        data=new byte[pos];
                        System.arraycopy(buff,0,data,0,pos);
                        pos=0;
                    }
                    message.what=100;
                    message.obj=data;
                    handler.sendMessage(message);
                    break;

                }
            }
            setRun(false);
            Log.i("test","timer thread over");
        }
    }
}
