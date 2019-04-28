package com.example.cjb.locktest.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TextInfo {
    private TextView textView;
    private static  TextInfo ourInstance;

    public static TextInfo getInstance() {
        synchronized (TextInfo.class){
            if(ourInstance==null){
                ourInstance=new TextInfo();
            }
            return ourInstance;
        }

    }

    public void init(TextView textView){
        this.textView=textView;
    }
    private TextInfo() {
    }

    public void appenText(final String x){

            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.append(x+"\n");
                }
            });

    }

    public void clear(){
        synchronized (TextInfo.class){
            textView.setText("");
        }
    }
}
