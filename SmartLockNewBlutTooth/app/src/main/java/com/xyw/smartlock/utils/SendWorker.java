package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/9/2.
 */
public class SendWorker {
    private String applyName;
    private String dateTime;
    private String phNumber;
    private String areaName;
    private String auditResule;

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAuditResule() {
        return auditResule;
    }

    public void setAuditResule(String auditResule) {
        this.auditResule = auditResule;
    }

    @Override
    public String toString() {
        return "SendWorker{" +
                "applyName='" + applyName + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", phNumber='" + phNumber + '\'' +
                ", areaName='" + areaName + '\'' +
                ", auditResule='" + auditResule + '\'' +
                '}';
    }
}
