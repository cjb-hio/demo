package com.xyw.smartlock.utils;

/**
 * Created by 19428 on 2016/10/28.
 */
public class SelectBean {
    private String Name;
    private String Op_No;
    private String Role_Id;
    private String Area_Id;
    private String Dt;
    private String V_BEGINTIME;
    private String V_ENDTIME;
    private String ZONE_NAME;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getOp_No() {
        return Op_No;
    }

    public void setOp_No(String op_No) {
        Op_No = op_No;
    }

    public String getRole_Id() {
        return Role_Id;
    }

    public void setRole_Id(String role_Id) {
        Role_Id = role_Id;
    }

    public String getArea_Id() {
        return Area_Id;
    }

    public void setArea_Id(String area_Id) {
        Area_Id = area_Id;
    }

    public String getDt() {
        return Dt;
    }

    public void setDt(String dt) {
        Dt = dt;
    }

    public String getV_BEGINTIME() {
        return V_BEGINTIME;
    }

    public void setV_BEGINTIME(String v_BEGINTIME) {
        V_BEGINTIME = v_BEGINTIME;
    }

    public String getV_ENDTIME() {
        return V_ENDTIME;
    }

    public void setV_ENDTIME(String v_ENDTIME) {
        V_ENDTIME = v_ENDTIME;
    }

    public String getZONE_NAME() {
        return ZONE_NAME;
    }

    public void setZONE_NAME(String ZONE_NAME) {
        this.ZONE_NAME = ZONE_NAME;
    }

    @Override
    public String toString() {
        return "JurisdictBean{" +
                "Name='" + Name + '\'' +
                ", Op_No='" + Op_No + '\'' +
                ", Role_Id='" + Role_Id + '\'' +
                ", Area_Id='" + Area_Id + '\'' +
                ", Dt='" + Dt + '\'' +
                ", V_BEGINTIME='" + V_BEGINTIME + '\'' +
                ", V_ENDTIME='" + V_ENDTIME + '\'' +
                ", ZONE_NAME='" + ZONE_NAME + '\'' +
                '}';
    }
}
