package com.example.cjb.locktest.factory.base;

import android.content.Context;

public interface ILink {

    public  int state=0;
    public void init(Context context);
    public void connect(String address);

    public void disconnect();
    public void write(byte[] frame);
}
