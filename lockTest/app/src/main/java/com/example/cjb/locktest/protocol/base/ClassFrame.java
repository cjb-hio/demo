package com.example.cjb.locktest.protocol.base;

import com.example.cjb.locktest.utils.Utils;

public class ClassFrame {

    public static class LinkRequest extends IFrame{
        @order(1)
        byte piid;
        @order(2)
        byte type;

        @order(3)
        @annocation("long")
        int heartbeat;

        @order(4)
        @annocation("date_time")
        String time;

        public LinkRequest(byte piid, byte type, int heartbeat, String time) {
            this.piid = piid;
            this.type = type;
            this.heartbeat = heartbeat;
            this.time = time;
        }

        public LinkRequest() {
        }
    }

    public static class LinkResponse extends IFrame{
        @order(1)
        byte piid;
        @order(2)
        byte result;   //0x80;成功 0x81地址重复 0x82:非法 0x83 容量不足

        public LinkResponse(byte piid, byte result, String requestTime, String getTime, String repTime) {
            this.piid = piid;
            this.result = result;
            this.requestTime = requestTime;
            this.getTime = getTime;
            this.repTime = repTime;
        }

        @order(3)
        @annocation("date_time")
        String requestTime;

        @order(4)
        @annocation("date_time")
        String getTime;

        @order(5)
        @annocation("date_time")
        String repTime;

    }

    public static class ConnectRequest extends IFrame{
        @order(1)
        byte piid;
        @order(2)
        @annocation("long")
        int version;

        @order(3)
        @annocation("size8")
        String protl;

        @order(4)
        @annocation("size16")
        String fun;

        @order(5)
        @annocation("long")
        int sendSize;

        @order(6)
        @annocation("long")
        int getSize;

        @order(7)
        byte windowSize;

        @order(8)
        @annocation("long")
        int hangdleSize;

        @order(9)
        int delay;

        @order(10)
        byte authenticate;

        public ConnectRequest(byte piid, int version, String protl, String fun, int sendSize, int getSize, byte windowSize, int hangdleSize, int delay, byte authenticate) {
            this.piid = piid;
            this.version = version;
            this.protl = protl;
            this.fun = fun;
            this.sendSize = sendSize;
            this.getSize = getSize;
            this.windowSize = windowSize;
            this.hangdleSize = hangdleSize;
            this.delay = delay;
            this.authenticate = authenticate;
        }
    }

        public static class ConnectResponse extends IFrame{
            @order(1)
            byte piid;
            @order(2)
            @annocation("size32")
            String info;

            @order(3)
            @annocation("long")
            int version;

            @order(4)
            @annocation("size8")
            String protl;

            @order(5)
            @annocation("size16")
            String fun;

            @order(6)
            @annocation("long")
            int sendSize;

            @order(7)
            @annocation("long")
            int getSize;

            @order(8)
            byte windowSize;

            @order(9)
            @annocation("long")
            int hangdleSize;

            @order(10)
            int delay;

            @order(11)
            byte autresult;

            @order(12)
            byte addinfo;

            @Override
            public void parse(byte[] data, int pos) {
                super.parse(data, pos);
                String info=this.info;
                String m="";
                byte[] x=Utils.hexStr2bytes(info);
                m=new String(x);
                for(int i=0;i<x.length;i++){
                    char ch= (char) (x[i]&0xff);
                    m=m+ch;
                }
                m=m+"\0";
                this.info=m;
            }
        }


    public static class GetRequest extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte attr;

        @order(5)
        byte index;
        public GetRequest(byte type, byte ppid, String OI, byte attr, byte index) {
            this.type = type;
            this.ppid = ppid;
            this.OI = OI;
            this.attr = attr;
            this.index = index;
        }
    }



    public static class GetResponse extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte attr;

        @order(5)
        byte index;

        @order(6)
        @annocation("data")
        String data;

        @Override
        public byte getAttr() {
            return attr;
        }

        @Override
        public String getOI() {
            return OI;
        }
    }

    public static class SetRequest extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte attr;

        @order(5)
        byte index;

        @order(6)
        @annocation("data")
        String data;

        public SetRequest(byte type, byte ppid, String OI, byte attr, byte index, String data) {
            this.type = type;
            this.ppid = ppid;
            this.OI = OI;
            this.attr = attr;
            this.index = index;
            this.data = data;
        }
    }

    public static class SetResponse extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte attr;

        @order(5)
        byte index;

        @order(6)
        byte dar= (byte) 0xff;
    }

    public static class ActionRequest extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte bz;

        @order(5)
        byte mode;

        @order(6)
        @annocation("data")
        String data;

        public ActionRequest(byte type, byte ppid, String OI, byte bz, byte mode, String data) {
            this.type = type;
            this.ppid = ppid;
            this.OI = OI;
            this.bz = bz;
            this.mode = mode;
            this.data = data;
        }

    }

    public static class ActionResponse extends IFrame{
        @order(1)
        byte type;
        @order(2)
        byte ppid;

        @order(3)
        @annocation("size2")
        String OI;

        @order(4)
        byte bz;

        @order(5)
        byte mode;

        @order(6)
        byte dar=(byte)0xff;

        @order(7)
        @annocation("op_result")
        String data;

        @Override
        public byte getAttr() {
            return bz;
        }

        @Override
        public String getOI() {
            return OI;
        }

    }



}
