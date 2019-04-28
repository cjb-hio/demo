package com.example.cjb.locktest.protocol.base;

import android.util.Log;
import android.widget.TextView;

import com.example.cjb.locktest.bluetooth.BluetoothLink;
import com.example.cjb.locktest.factory.LinkaManager;
import com.example.cjb.locktest.factory.base.ILink;
import com.example.cjb.locktest.utils.TextInfo;
import com.example.cjb.locktest.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Protocol11778 {

    public int index;
    private  String serverAddress="05000000000001";

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private BluetoothLink linkaManager;
    public Protocol11778(BluetoothLink linkaManager) {
        this.linkaManager=linkaManager;
    }

    public String makeFrame(String address, String C, int biaozhi,String apdu){
        if(biaozhi==1||biaozhi==129||biaozhi==16||biaozhi==144){

        }else if(biaozhi<130){
            apdu=apdu+"00";
        }else{
            apdu=apdu+"0000";
        }
        String bz=Utils.inter2HexString(biaozhi,1);
        String ret="";
        String temp="";
        temp=temp+C+makeA(0,0,address, (byte) 0);
        ret=ret+temp;
        //ret=ret+makeHcs(Utils.hexStr2bytes(ret));
        temp=temp+"0000"+bz+apdu;
        //加上Len
        int len=temp.length()/2+4;
        temp=Utils.inter2HexString(len,2);
        ret=Utils.turnPer2(temp)+ret;
        //加上hcs
        ret=ret+makeHcs(Utils.hexStr2bytes(ret));
        ret=ret+bz+apdu;
        ret=ret+makeHcs(Utils.hexStr2bytes(ret));
        //
        ret="68"+ret+"16";
        return ret;
    }

    /**
     * type:单元定义： 0：预连接 1：clent-apdu 2:server-apdu 3:安全
     * subType:选择码
     * @return
     */
    private String makeAPDU(int type,int subType,IFrame frame) {
        String ret="";

        ret=ret+Utils.inter2HexString(subType,1);
        if(type==0){
            ret="01"+frame.make();

        }else if(type==1){

        }
        return null;
    }

    private String makeHcs(byte[] bytes) {
        int ret = Utils.hcsChech(bytes);
        String s = Utils.inter2HexString(ret, 2);
        s=Utils.turnPer2(s);
        return s;
    }


    /**
     *
     * @param type  地址类型 0：单地址 1：通配地址 2：组地址 3：广播地址
     * @param logic 逻辑地址
     * @param address   服务器地址
     * @param b 客户机地址
     * @return
     */
    private String makeA(int type,int logic,String address, byte b) {
        String ret="";
        int len=address.length();
        if(len%2==1){
            address=address+"F";
        }
        byte first= (byte) (Utils.mL(type,6,2)+Utils.mL(logic,4,2)+Utils.mL(address.length()/2-1,0,4));
        ret=Utils.byte2String(first)+Utils.turnPer2(address)+Utils.byte2String(b);
        return ret;
    }


    /**
     *
     * @param dir   传输方向位： 0:客户机发送 1：服务器发送
     * @param prm   启动标志位： 0：服务器发送 1：客户机发送
     * @param isSplite
     * @param code  1：链路管理 3：应用数据
     * @return
     */
    private String makeC(int dir,int prm,int isSplite,int code){
        byte b= (byte) (Utils.mL(dir,7,1)+Utils.mL(prm,6,1)
                        +Utils.mL(isSplite,5,1)+Utils.mL(code,0,3));

        return Utils.byte2String(b);
    }


    /**
     * 处理接收的数据
     * @param data
     */
    public void processData(byte[] data) {

        String getData = Utils.bytes2String(data);
        TextInfo.getInstance().appenText("解析数据:"+getData);
        Log.i("test","getData:"+getData);

        Header header = processHeader(data);
        if(header==null){
            return;
        }
        processAPDU(data,header,header.pos);
    }

    /**
     * 预连接响应
     */
    public void linkResponse() {
        if(serverAddress.length()==0){
            return ;
        }
        ClassFrame.LinkResponse linkRequest=new ClassFrame.LinkResponse((byte)0x00,(byte)0x80,Utils.getCurTime(),Utils.getCurTime(),Utils.getCurTime());
        String frame=makeFrame(serverAddress,"01",129,linkRequest.make());
        byte[] x=Utils.hexStr2bytes(frame);
        linkaManager.write(x);
    }

    public void connect(){
        if(serverAddress.length()==0){
            return ;
        }

        ClassFrame.ConnectRequest connectRequest=new ClassFrame.ConnectRequest((byte)0,16,"FFFFFFFFFFFFFFFF","FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
                1024,1024,(byte)0x01,1024,100,(byte) 0);
        String frame=makeFrame(serverAddress,"43",2,connectRequest.make());
        byte[] x=Utils.hexStr2bytes(frame);
        linkaManager.write(x);
    }

    public void read(String OI,byte attr,byte index){
        if(serverAddress.length()==0){
            return ;
        }
        ClassFrame.GetRequest getRequest=new ClassFrame.GetRequest((byte) 0x01,(byte)0x01,OI,attr,index);
        String frame=makeFrame(serverAddress,"43",5,getRequest.make());
        byte[] x=Utils.hexStr2bytes(frame);
        linkaManager.write(x);
    }

    public void set(String OI,byte attr,byte index,String data){
        if(serverAddress.length()==0){
            return ;
        }
        ClassFrame.SetRequest setRequest=new ClassFrame.SetRequest((byte)0x01,(byte)0x02,OI,attr,index,data);
        String frame=makeFrame(serverAddress,"43",6,setRequest.make());
        byte[] x=Utils.hexStr2bytes(frame);
        linkaManager.write(x);
    }
    public void action(String OI,byte attr,byte index,String data){
        if(serverAddress.length()==0){
            return ;
        }
        ClassFrame.ActionRequest actionRequest= new ClassFrame.ActionRequest((byte)0x01,(byte)0x05,OI,attr,index,data);
        String frame=makeFrame(serverAddress,"43",7,actionRequest.make());
        byte[] x=Utils.hexStr2bytes(frame);
        linkaManager.write(x);
    }

    private void processAPDU(byte[] data,Header header,int pos) {
        int off=0;
        IFrame iFrame=null;
        byte[] test;
        int x1;
        String temp="";
        int x2;
        int x;

        //解析应用层数据单元
        x=data[pos]&0xff;
        pos++;
        switch (x){
            case 1:
                //处理预连接
                iFrame =new ClassFrame.LinkRequest();
                break;
            case 128:
                //读取数据响应
                iFrame=new ClassFrame.GetResponse();
                off=2;
                break;
            case 130:
                //建立应用连接响应
                iFrame=new ClassFrame.ConnectResponse();
                off=2;
                break;
            case 133:
                //读取数据响应
                iFrame= new ClassFrame.GetResponse();
                off=2;
                break;
            case 134:
                //设置响应
                iFrame= new ClassFrame.SetResponse();
                off=2;
                break;
            case 135:
                //
                iFrame= new ClassFrame.ActionResponse();
                off=2;
                break;

        }

        //
        if(iFrame==null){
            return;
        }
        iFrame.parse(data,pos);
        if(iFrame.isParse==false){
            return;
        }
        //校验
        /*
        pos=iFrame.pos+off;
        x1=data[pos]&0xff;
        x2=data[pos+1]&0xff;
        x=x1+x2*256;
        pos+=2;
        test=new byte[pos-3];
        System.arraycopy(data,1,test,0,pos-3);
        boolean isCheck=Utils.check(test,x);

        if(isCheck==false){
            Log.i("test","fcs check error");
            return;
        }
        */
        switch (iFrame.getClass().getSimpleName()){
            case "LinkRequest":

                ClassFrame.LinkRequest linkRequest= (ClassFrame.LinkRequest) iFrame;
                if(linkRequest.type==0){
                    TextInfo.getInstance().appenText("时间:"+linkRequest.time+" 类型:登录");
                    Log.i("test","link login");
                }else if(linkRequest.type==1){
                    TextInfo.getInstance().appenText("时间:"+linkRequest.time+" 类型:心跳");
                    Log.i("test","link heart");
                }else if(linkRequest.type==2){
                    TextInfo.getInstance().appenText("时间:"+linkRequest.time+" 类型:退出登录");
                    Log.i("test","link dislogin");
                }
                linkResponse();
                break;

            case "ConnectResponse":
                //解析连接返回
                ClassFrame.ConnectResponse response= (ClassFrame.ConnectResponse) iFrame;
                if(response.autresult==0){
                    TextInfo.getInstance().appenText("连接成功");
                }

                break;
            case "GetResponse":
                //解析连接返回
                ClassFrame.GetResponse getResponse= (ClassFrame.GetResponse) iFrame;
                if(getResponse.OI.equals("4001")){
                    //读地址
                    temp=getResponse.data;
                    TextInfo.getInstance().appenText("得到地址:"+temp);
                }else if(getResponse.OI.equals("4003")){
                    //读编号
                    temp=getResponse.data;
                    TextInfo.getInstance().appenText("得到编码:"+temp);
                }else if(getResponse.OI.equals("4310")){
                    //读锁状态
                    temp=getResponse.data;
                    if(temp.equals("0")){
                        TextInfo.getInstance().appenText("得到状态:关");
                    }else if(temp.equals("1")){
                        TextInfo.getInstance().appenText("得到状态:开");
                    }

                }else if(getResponse.OI.equals("f100")){
                    //读ESAM信息
                    if(getResponse.attr==2){
                        //esam序列号
                        TextInfo.getInstance().appenText("ESAM序列号:"+getResponse.data);
                        Sm1Manager.getInstance().setEsamId(getResponse.data);
                        read("F100",(byte)7,(byte)0);
                    }else if(getResponse.attr==7){
                        //会话计数器
                        TextInfo.getInstance().appenText("会话计数器:"+getResponse.data);
                        Sm1Manager.getInstance().setNum(getResponse.data);
                    }
                }else if(getResponse.OI.equals("4004")){
                    //自定义协议
                    temp=getResponse.data;
                    TextInfo.getInstance().appenText("得到密文:"+temp);
                }
                break;
            case "SetResponse":
                //解析连接返回
                ClassFrame. SetResponse setResponse= (ClassFrame.SetResponse) iFrame;
                int dar=setResponse.dar&0xff;
                if(setResponse.OI.equals("4001")){
                    if(dar==0){
                        TextInfo.getInstance().appenText("设置地址成功");
                    }
                }else if(setResponse.OI.equals("4003")){
                    if(dar==0){
                        TextInfo.getInstance().appenText("设置编码成功");
                    }
                }
                break;
            case "ActionResponse":
                //解析连接返回
                ClassFrame.ActionResponse actionResponse= (ClassFrame.ActionResponse) iFrame;
                dar=actionResponse.dar;
                if(dar!=0){
                    TextInfo.getInstance().appenText("返回结果错误");
                    return;
                }
                if(actionResponse.OI.equals("4310")) {
                    int h=actionResponse.bz&0xff;
                    if(h==127){
                        if(actionResponse.data.equals("1")){
                            TextInfo.getInstance().appenText("开锁成功");
                        }else{
                            TextInfo.getInstance().appenText("开锁失败");
                        }
                    }else if(h==128){
                        if(actionResponse.data.equals("1")){
                            TextInfo.getInstance().appenText("关锁成功");
                            index++;
                            if(index==1){
                                TextInfo.getInstance().appenText("关锁:");
                                //temp=Utils.getObjectString("12345678");
                                action("4310",(byte)0x80,(byte)0,"00");
                            }
                        }else{
                            TextInfo.getInstance().appenText("关锁失败");
                        }
                    }else{
                        TextInfo.getInstance().appenText("操作锁失败");
                    }
                }
                break;
        }


    }

    private Header processHeader(byte[] data) {
        int pos=0;
        byte[] test;
        Header header=new Header();
        int x1;
        String temp="";
        int x2;
        int x;
        if(data[0]!=0x68){
            return null;
        }
        if(data.length<5){
            return null;
        }
        x1=data[1]&0xff;
        x2=data[2]&0x3f;
        int L=x1+x2*256;
        header.L=L;
        //解析C
        x1=data[3];
        header.dir=(x1>>7)&0x01;
        header.prn=(x1>>6)&0x01;
        header.isSplite=(x1>>5)&0x01;
        header.code=(x1>>0)&0x03;
        //解析地址
        x1=data[4]&0xff; //地址特征
        header.addressType=(x1>>6)&0x03;
        header.logic=(x1>>4)&0x03;
        header.addrLen=(x1>>0)&0x0f;
        header.addrLen=header.addrLen+1;
        //得到服务器地址
        if(data.length<(header.addrLen+8)){
            return null;
        }
        byte[] address=new byte[header.addrLen];
        System.arraycopy(data,5,address,0,header.addrLen);
        temp=Utils.bytes2String(address);
        header.address= Utils.turnPer2(temp);
        serverAddress=header.address;

        pos=header.addrLen+5;
        x=data[pos];
        header.ca=x&0xff;
        pos++;
        //检测hcs
        x1=data[pos]&0xff;
        x2=data[pos+1]&0xff;
        x=x1+x2*256;
        pos+=2;
        test=new byte[pos-3];
        System.arraycopy(data,1,test,0,pos-3);
        boolean isCheck=Utils.check(test,x);
        if(isCheck){
            header.pos=pos;
            return header;
        }else{
            return null;
        }

    }



    class Header{
        int L;
        int dir;
        int prn;
        int isSplite;
        int code;

        int addressType;
        int logic;
        int addrLen;

        String address;
        int ca;
        int pos;
    }
}
