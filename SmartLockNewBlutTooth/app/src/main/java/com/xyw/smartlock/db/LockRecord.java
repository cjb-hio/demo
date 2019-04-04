package com.xyw.smartlock.db;

/**
 * 开锁记录
 */
public class LockRecord {
    private String OP_NAME;
    private String L_RET;
    private String L_OPTYPE;
    private String L_GPS_X;
    private String L_GPS_Y;
    private String L_CREATE_DT;
    private String LID;
    private String L_CREATE_OP;

    public String getOP_NAME() {
        return OP_NAME;
    }

    public void setOP_NAME(String OP_NAME) {
        this.OP_NAME = OP_NAME;
    }

    public String getL_RET() {
        return L_RET;
    }

    public void setL_RET(String l_RET) {
        L_RET = l_RET;
    }

    public String getL_OPTYPE() {
        return L_OPTYPE;
    }

    public void setL_OPTYPE(String l_OPTYPE) {
        L_OPTYPE = l_OPTYPE;
    }

    public String getL_GPS_X() {
        return L_GPS_X;
    }

    public void setL_GPS_X(String l_GPS_X) {
        L_GPS_X = l_GPS_X;
    }

    public String getL_GPS_Y() {
        return L_GPS_Y;
    }

    public void setL_GPS_Y(String l_GPS_Y) {
        L_GPS_Y = l_GPS_Y;
    }

    public String getL_CREATE_DT() {
        return L_CREATE_DT;
    }

    public void setL_CREATE_DT(String l_CREATE_DT) {
        L_CREATE_DT = l_CREATE_DT;
    }

    public String getLID() {
        return LID;
    }

    public void setLID(String LID) {
        this.LID = LID;
    }

    public String getL_CREATE_OP() {
        return L_CREATE_OP;
    }

    public void setL_CREATE_OP(String l_CREATE_OP) {
        L_CREATE_OP = l_CREATE_OP;
    }

    @Override
    public String toString() {
        return "LockRecord{" +
                "OP_NAME='" + OP_NAME + '\'' +
                ", L_RET='" + L_RET + '\'' +
                ", L_OPTYPE='" + L_OPTYPE + '\'' +
                ", L_GPS_X='" + L_GPS_X + '\'' +
                ", L_GPS_Y='" + L_GPS_Y + '\'' +
                ", L_CREATE_DT='" + L_CREATE_DT + '\'' +
                ", LID='" + LID + '\'' +
                ", L_CREATE_OP='" + L_CREATE_OP + '\'' +
                '}';
    }
}
