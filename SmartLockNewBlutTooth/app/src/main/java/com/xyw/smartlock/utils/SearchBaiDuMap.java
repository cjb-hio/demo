package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/6/30.
 */
public class SearchBaiDuMap {
    private String Latitude;
    private String Longitude;
    private String Lid;
    private String LockName;
    private String type;

    public String getLockName() {
        return LockName;
    }

    public void setLockName(String lockName) {
        LockName = lockName;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLid() {
        return Lid;
    }

    public void setLid(String lid) {
        Lid = lid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SearchBaiDuMap{" +
                "Latitude='" + Latitude + '\'' +
                ", Longitude='" + Longitude + '\'' +
                ", Lid='" + Lid + '\'' +
                ", LockName='" + LockName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
