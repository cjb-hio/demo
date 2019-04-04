package com.xyw.smartlock.utils;

import java.io.Serializable;

public class AcacheUserBean implements Serializable {

    /**
     *
     */
    private static final long se = 1L;

    private String Account;
    private String OP_DT;
    private String OP_NO;
    private String result;
    private String BeginTime;
    private String EndTime;
    private String OP_NAME;
    private String AREA_NAME;
    private String KEYVALUE;
    private String Area_id;
    private String ROLE_ID;
    private String USER_CONTEXT;
    private String MAXVER;
    private String Address = "0";


    @Override
    public String toString() {
        return "AcacheUserBean{" +
                "Account='" + Account + '\'' +
                ", OP_DT='" + OP_DT + '\'' +
                ", OP_NO='" + OP_NO + '\'' +
                ", result='" + result + '\'' +
                ", BeginTime='" + BeginTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", OP_NAME='" + OP_NAME + '\'' +
                ", AREA_NAME='" + AREA_NAME + '\'' +
                ", KEYVALUE='" + KEYVALUE + '\'' +
                ", Area_id='" + Area_id + '\'' +
                ", ROLE_ID='" + ROLE_ID + '\'' +
                ", USER_CONTEXT='" + USER_CONTEXT + '\'' +
                ", MAXVER='" + MAXVER + '\'' +
                ", Address='" + Address + '\'' +
                '}';

    }

    public static long getSe() {
        return se;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


    public void setMAXVER(String MAXVER) {
        this.MAXVER = MAXVER;
    }

    public String getMAXVER() {
        return MAXVER;
    }

    public String getOP_DT() {
        return OP_DT;
    }

    public void setOP_DT(String OP_DT) {
        this.OP_DT = OP_DT;
    }

    public String getOP_NO() {
        return OP_NO;
    }

    public void setOP_NO(String OP_NO) {
        this.OP_NO = OP_NO;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getOP_NAME() {
        return OP_NAME;
    }

    public void setOP_NAME(String OP_NAME) {
        this.OP_NAME = OP_NAME;
    }

    public String getAREA_NAME() {
        return AREA_NAME;
    }

    public void setAREA_NAME(String AREA_NAME) {
        this.AREA_NAME = AREA_NAME;
    }

    public String getKEYVALUE() {
        return KEYVALUE;
    }

    public void setKEYVALUE(String KEYVALUE) {
        this.KEYVALUE = KEYVALUE;
    }

    public String getArea_id() {
        return Area_id;
    }

    public void setArea_id(String area_id) {
        Area_id = area_id;
    }

    public String getROLE_ID() {
        return ROLE_ID;
    }

    public void setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String getUSER_CONTEXT() {
        return USER_CONTEXT;
    }

    public void setUSER_CONTEXT(String USER_CONTEXT) {
        this.USER_CONTEXT = USER_CONTEXT;
    }
}
