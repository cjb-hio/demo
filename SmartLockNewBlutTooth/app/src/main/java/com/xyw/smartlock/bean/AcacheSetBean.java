package com.xyw.smartlock.bean;

import java.io.Serializable;

/**
 * Created by acer on 2016/6/22.
 */
public class AcacheSetBean implements Serializable {
    private String Ms;

    @Override
    public String toString() {
        return "AcacheSetBean{" +
                "Ms='" + Ms + '\'' +
                '}';
    }

    public String getMs() {
        return Ms;
    }

    public void setMs(String ms) {
        Ms = ms;
    }
}
