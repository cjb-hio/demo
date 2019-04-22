package com.example.cjb.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Psn implements Parcelable {
    private String age;
    private String name;
    protected Psn(Parcel in) {
        this.age=in.readString();
        this.name=in.readString();
    }

    public static final Creator<Psn> CREATOR = new Creator<Psn>() {
        @Override
        public Psn createFromParcel(Parcel in) {
            return new Psn(in);
        }

        @Override
        public Psn[] newArray(int size) {
            return new Psn[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(age);
        dest.writeString(name);
    }
}
