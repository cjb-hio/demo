package com.xyw.smartlock.db;

/**
 *开锁
 */
public class UnLock {
    private String Lid;
    private String GPS_X;
    private String GPS_Y;
    private String OP_NO;
    private String OP_TYPE;
    private String OP_RET;
    private String OP_DATETIME;
    private String USER_CONTEXT;

    public String getLid() {
        return Lid;
    }

    public void setLid(String lid) {
        Lid = lid;
    }

    public String getGPS_X() {
        return GPS_X;
    }

    public void setGPS_X(String GPS_X) {
        this.GPS_X = GPS_X;
    }

    public String getGPS_Y() {
        return GPS_Y;
    }

    public void setGPS_Y(String GPS_Y) {
        this.GPS_Y = GPS_Y;
    }

    public String getOP_NO() {
        return OP_NO;
    }

    public void setOP_NO(String OP_NO) {
        this.OP_NO = OP_NO;
    }

    public String getOP_TYPE() {
        return OP_TYPE;
    }

    public void setOP_TYPE(String OP_TYPE) {
        this.OP_TYPE = OP_TYPE;
    }

    public String getOP_RET() {
        return OP_RET;
    }

    public void setOP_RET(String OP_RET) {
        this.OP_RET = OP_RET;
    }

    public String getOP_DATETIME() {
        return OP_DATETIME;
    }

    public void setOP_DATETIME(String OP_DATETIME) {
        this.OP_DATETIME = OP_DATETIME;
    }

    public String getUSER_CONTEXT() {
        return USER_CONTEXT;
    }

    public void setUSER_CONTEXT(String USER_CONTEXT) {
        this.USER_CONTEXT = USER_CONTEXT;
    }

    @Override
    public String toString() {
        return "UnLock{" +
                "Lid='" + Lid + '\'' +
                ", GPS_X='" + GPS_X + '\'' +
                ", GPS_Y='" + GPS_Y + '\'' +
                ", OP_NO='" + OP_NO + '\'' +
                ", OP_TYPE='" + OP_TYPE + '\'' +
                ", OP_RET='" + OP_RET + '\'' +
                ", OP_DATETIME='" + OP_DATETIME + '\'' +
                ", USER_CONTEXT='" + USER_CONTEXT + '\'' +
                '}';
    }
}
