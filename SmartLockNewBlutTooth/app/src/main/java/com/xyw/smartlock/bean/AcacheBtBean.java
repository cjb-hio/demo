package com.xyw.smartlock.bean;

import java.io.Serializable;

/**
 * Created by acer on 2016/6/17.
 */
public class AcacheBtBean implements Serializable {
    private String Address;


    @Override
    public String toString() {
        return "AcacheBtBean{" +
                "Address='" + Address + '\'' +
                '}';
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
