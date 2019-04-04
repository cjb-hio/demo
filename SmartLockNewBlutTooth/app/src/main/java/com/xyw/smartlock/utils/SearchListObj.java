package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/7/4.
 */
public class SearchListObj {
    private String Used;
    private String DateTime;
    private String LockID;
    private String Result;
    private String SreaName;

    public String getUsed() {
        return Used;
    }

    public void setUsed(String used) {
        Used = used;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getLockID() {
        return LockID;
    }

    public void setLockID(String lockID) {
        LockID = lockID;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getSreaName() {
        return SreaName;
    }

    public void setSreaName(String sreaName) {
        SreaName = sreaName;
    }

    @Override
    public String toString() {
        return "SearchListObj{" +
                "Used='" + Used + '\'' +
                ", DateTime='" + DateTime + '\'' +
                ", LockID='" + LockID + '\'' +
                ", Result='" + Result + '\'' +
                ", SreaName='" + SreaName + '\'' +
                '}';
    }
}
