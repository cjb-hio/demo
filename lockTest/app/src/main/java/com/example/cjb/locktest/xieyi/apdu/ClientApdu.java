package com.example.cjb.locktest.xieyi.apdu;

import com.example.cjb.locktest.xieyi.apdu.apduoption.ApduOption;
import com.example.cjb.locktest.xieyi.type.TimeTag;

public class ClientApdu implements Apdu {

    int index;
    ApduOption apduOption;
    int option;
    TimeTag timeTag;

    @Override
    public String build() {
        return null;
    }
}
