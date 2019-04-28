package com.example.cjb.locktest.factory.base;

public interface ILinkCallback {

    public void onConnected();
    public void onReadData(byte[] data);
}
