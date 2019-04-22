package com.example.cjb.myapplication;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Person {

    private int age;

    private String fullName;
    private Date dateOfBirth;
    public Person(int age, String fullName, Date dateOfBirth) {
        super();
        this.age = age;
        this.fullName= fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public String getFullName() {
        return fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }


    // 标准 getters & setters
}