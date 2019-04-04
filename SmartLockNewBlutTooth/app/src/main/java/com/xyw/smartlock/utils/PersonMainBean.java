package com.xyw.smartlock.utils;

public class PersonMainBean {
    private String personMainStr;
    private String personMainID;

    public String getPersonMainStr() {
        return personMainStr;
    }

    public void setPersonMainStr(String personMainStr) {
        this.personMainStr = personMainStr;
    }

    public String getPersonMainID() {
        return personMainID;
    }

    public void setPersonMainID(String personMainID) {
        this.personMainID = personMainID;
    }

    @Override
    public String toString() {
        return "PersonMainBean{" +
                "personMainStr='" + personMainStr + '\'' +
                ", personMainID='" + personMainID + '\'' +
                '}';
    }
}
