package com.example.cjb.locktest.protocol.base;


import com.example.cjb.locktest.tcp.TcpSocket;

public class Sm1Manager {

    private String key;
    private String esamId;
    private static Sm1Manager instance;
    private State state=State.Idle;
    public String session1;
    public String mac1;
    public String session2;
    public String mac2;
    public String ecpMac;
    public String ecpData;
    public String clrData;
    public String toString(){
        String ret="";
        ret="esamId:"+esamId+" session1:"+session1+" mac1:"+mac1+" session2:"+session2+
                " mac2:"+mac2+" key:"+key+" ecpData:"+ecpData+" ecpMac:"+ecpMac+" clrData:"+clrData;
        return ret;
    }
    public void setEsamId(String esamId) {
        this.esamId = esamId;
        state=State.ReadNum;

    }

    public void setNum(String num) {
        String data="";
        if(state==State.ReadNum){
            state=State.readyForKeyInit;
            data="00"+esamId+num;
            String info=getRemoteInfo(data);
            if(info!=null){
                process(info);
            }
        }
    }

    /**
     * 解析服务器返回的数据
     * @param info
     */
    private void process(String info) {
        String temp="";
        temp=info.substring(0,2);
        int index=Integer.parseInt(temp,16);
        try{
            if(index==0){
                //会话协商 "00"+32字节session+4字节mac
                if(state==State.readyForKeyInit){
                    session1=info.substring(2,66);
                    mac1=info.substring(66,74);
                    state=State.sessionVerify;
                }

            }else if(index==1){
                //会话验证 "01"+177字节秘钥
                if(state==State.sessionVerify){
                    key=info.substring(2,356);
                    state=State.keyUpadte;
                }
            }else if(index==2){
                //密钥更新 "02"+4字节MAC+密文
                if(state==State.keyUpadte){
                    mac2=info.substring(2,10);
                    session2=info.substring(10);
                    state=State.DATA;
                }
            }else if(index==3){
                //加密 "03"+4字节MAC+密文
                if(state==State.DATA){
                    ecpMac=info.substring(2,10);
                    ecpData=info.substring(10);
                }
            }else if(index==4){
                //解密  "04"+明文
                if(state==State.DATA){
                    clrData=info.substring(2);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 向主站发出请求
     * @param data
     * @return
     */
    private String getRemoteInfo(String data) {
        return TcpSocket.getInstance().getInfo(data);
    }

    public static Sm1Manager getInstance() {
        synchronized (Sm1Manager.class){
            if(instance==null){
                instance=new Sm1Manager();
            }
            return instance;
        }
    }

    /**
     * 初始化
     * @param
     */
    public void init(Protocol11778 protocol){

        protocol.read("F100",(byte)2,(byte)0);
        state=State.ReadEsamId;
    }

    public void setSessionVerify(String s, String s1) {
        if(state==State.sessionVerify){
            String remoteInfo = getRemoteInfo("01" + s + s1);
            process(remoteInfo);
        }
    }
    public void setKeyUpdate(String biao) {
        if(state==State.keyUpadte){
            String str=getRemoteInfo("02"+biao);
            process(str);
        }
    }
    public void setDataEpt(String mode, String s1) {
        if(state==State.DATA){
            String remoteInfo = getRemoteInfo("03" + mode + s1);
            process(remoteInfo);
        }
    }
    public void clearData(String mode, String s1,String data) {
        if(state==State.DATA){
            String remoteInfo = getRemoteInfo("04" + mode + s1 + data);
            process(remoteInfo);
        }
    }

    public enum  State{
        Idle,
        ReadEsamId,
        ReadNum,
        readyForKeyInit,
        sessionVerify,
        keyUpadte,
        DATA        //密文传输
    }
}
