package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/10/28.
 */
public class Audit {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Audit audit = (Audit) o;

        if (id != audit.id) return false;
        if (dataUserName != null ? !dataUserName.equals(audit.dataUserName) : audit.dataUserName != null) return false;
        if (dataUserNumber != null ? !dataUserNumber.equals(audit.dataUserNumber) : audit.dataUserNumber != null) return false;
        if (TASK_NO != null ? !TASK_NO.equals(audit.TASK_NO) : audit.TASK_NO != null) return false;
        if (SIGN != null ? !SIGN.equals(audit.SIGN) : audit.SIGN != null) return false;
        return SIGNIMG != null ? SIGNIMG.equals(audit.SIGNIMG) : audit.SIGNIMG == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dataUserName != null ? dataUserName.hashCode() : 0);
        result = 31 * result + (dataUserNumber != null ? dataUserNumber.hashCode() : 0);
        result = 31 * result + (TASK_NO != null ? TASK_NO.hashCode() : 0);
        result = 31 * result + (SIGN != null ? SIGN.hashCode() : 0);
        result = 31 * result + (SIGNIMG != null ? SIGNIMG.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Audit{" +
                "id=" + id +
                ", dataUserName='" + dataUserName + '\'' +
                ", dataUserNumber='" + dataUserNumber + '\'' +
                ", TASK_NO='" + TASK_NO + '\'' +
                ", SIGN='" + SIGN + '\'' +
                ", SIGNIMG='" + SIGNIMG + '\'' +
                '}';
    }
}
