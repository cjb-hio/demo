package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/10/27.
 */
public class SendOrderList {
    private int id;
    private String dataUserName;
    private String dataUserNumber;
    private String TASK_NO;
    private String SIGN;
    private String SIGNIMG;

    public String getDataUserName() {
        return dataUserName;
    }

    public void setDataUserName(String dataUserName) {
        this.dataUserName = dataUserName;
    }

    public String getDataUserNumber() {
        return dataUserNumber;
    }

    public void setDataUserNumber(String dataUserNumber) {
        this.dataUserNumber = dataUserNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTASK_NO() {
        return TASK_NO;
    }

    public void setTASK_NO(String TASK_NO) {
        this.TASK_NO = TASK_NO;
    }

    public String getSIGN() {
        return SIGN;
    }

    public void setSIGN(String SIGN) {
        this.SIGN = SIGN;
    }

    public String getSIGNIMG() {
        return SIGNIMG;
    }

    public void setSIGNIMG(String SIGNIMG) {
        this.SIGNIMG = SIGNIMG;
    }

    @Override
    public String toString() {
        return "SendOrderList{" +
                "id=" + id +
                ", dataUserName='" + dataUserName + '\'' +
                ", dataUserNumber='" + dataUserNumber + '\'' +
                ", TASK_NO='" + TASK_NO + '\'' +
                ", SIGN='" + SIGN + '\'' +
                ", SIGNIMG='" + SIGNIMG + '\'' +
                '}';
    }
}
