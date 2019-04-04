package com.xyw.smartlock.db;

/**
 * 锁档案
 */
public class LockFile {
    private String LID;
    private String L_NAME;
    private String L_ADDR;
    private String L_GPS_X;
    private String L_GPS_Y;
    private String L_CREATE_DT;
    private String L_CREATE_OP;
    private String L_BOX_NO;
    private String L_BOX_TYPE;
    private String ZONE_NO;
    private String KEY_VER;
    private String PASSNUM;
    private String ZONE_NAME;


    public String getLID() {
        return LID;
    }

    public void setLID(String LID) {
        this.LID = LID;
    }

    public String getL_NAME() {
        return L_NAME;
    }

    public void setL_NAME(String l_NAME) {
        L_NAME = l_NAME;
    }

    public String getL_ADDR() {
        return L_ADDR;
    }

    public void setL_ADDR(String l_ADDR) {
        L_ADDR = l_ADDR;
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

    public String getL_CREATE_OP() {
        return L_CREATE_OP;
    }

    public void setL_CREATE_OP(String l_CREATE_OP) {
        L_CREATE_OP = l_CREATE_OP;
    }

    public String getL_BOX_NO() {
        return L_BOX_NO;
    }

    public void setL_BOX_NO(String l_BOX_NO) {
        L_BOX_NO = l_BOX_NO;
    }

    public String getL_BOX_TYPE() {
        return L_BOX_TYPE;
    }

    public void setL_BOX_TYPE(String l_BOX_TYPE) {
        L_BOX_TYPE = l_BOX_TYPE;
    }

    public String getKEY_VER() {
        return KEY_VER;
    }

    public void setKEY_VER(String KEY_VER) {
        this.KEY_VER = KEY_VER;
    }

    public String getZONE_NO() {
        return ZONE_NO;
    }

    public void setZONE_NO(String ZONE_NO) {
        this.ZONE_NO = ZONE_NO;
    }

    public String getPASSNUM() {
        return PASSNUM;
    }

    public void setPASSNUM(String PASSNUM) {
        this.PASSNUM = PASSNUM;
    }

    public String getZONE_NAME() {
        return ZONE_NAME;
    }

    public void setZONE_NAME(String ZONE_NAME) {
        this.ZONE_NAME = ZONE_NAME;
    }


    @Override
    public String toString() {
        return "LockFile{" +
                "LID='" + LID + '\'' +
                ", L_NAME='" + L_NAME + '\'' +
                ", L_ADDR='" + L_ADDR + '\'' +
                ", L_GPS_X='" + L_GPS_X + '\'' +
                ", L_GPS_Y='" + L_GPS_Y + '\'' +
                ", L_CREATE_DT='" + L_CREATE_DT + '\'' +
                ", L_CREATE_OP='" + L_CREATE_OP + '\'' +
                ", L_BOX_NO='" + L_BOX_NO + '\'' +
                ", L_BOX_TYPE='" + L_BOX_TYPE + '\'' +
                ", KEY_VER='" + KEY_VER + '\'' +
                ", ZONE_NO='" + ZONE_NO + '\'' +
                ", PASSNUM='" + PASSNUM + '\'' +
                ", ZONE_NAME='" + ZONE_NAME + '\'' +
                '}';
    }


}
