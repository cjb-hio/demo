package com.xyw.smartlock.utils;

/**
 * Created by acer on 2016/4/25.
 */
public class SendWork {
    private String myName;
    private String area;
    private String startTime;
    private String endTime;
    private String content;
    private String path1;
    private String path2;
    private String task_no;//任务号
    private String r_op_no;//请求的操作员
    private String R_ZONE_NO;//区域号
    private String RET_V;  //审核状态
    private String RET_OP_NO; //审核人
    private String ROLE_ID;
    private String HAVE_INFO;

    public String getROLE_ID() {
        return ROLE_ID;
    }

    public void setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getRET_V() {
        return RET_V;
    }

    public void setRET_V(String RET_V) {
        this.RET_V = RET_V;
    }

    public String getRET_OP_NO() {
        return RET_OP_NO;
    }

    public void setRET_OP_NO(String RET_OP_NO) {
        this.RET_OP_NO = RET_OP_NO;
    }

    public String getTask_no() {
        return task_no;
    }

    public void setTask_no(String task_no) {
        this.task_no = task_no;
    }

    public String getR_op_no() {
        return r_op_no;
    }

    public void setR_op_no(String r_op_no) {
        this.r_op_no = r_op_no;
    }

    public String getR_ZONE_NO() {
        return R_ZONE_NO;
    }

    public void setR_ZONE_NO(String r_ZONE_NO) {
        R_ZONE_NO = r_ZONE_NO;
    }

    public String getPath1() {
        return path1;
    }

    public void setPath1(String path1) {
        this.path1 = path1;
    }

    public String getPath2() {
        return path2;
    }

    public void setPath2(String path2) {
        this.path2 = path2;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHAVE_INFO() {
        return HAVE_INFO;
    }

    public void setHAVE_INFO(String HAVE_INFO) {
        this.HAVE_INFO = HAVE_INFO;
    }
}
