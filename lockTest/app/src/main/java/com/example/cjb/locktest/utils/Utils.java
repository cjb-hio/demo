package com.example.cjb.locktest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     * 将ym位的数据向左移w
     * @param x 数据
     * @param w 向左移位数
     * @param ym 有效位
     * @return
     */
    public static byte mL(int x,int w,int ym){
        int h=1;
        for(int i=0;i<ym;i++){
            h=h*2;
        }
        x=x%h;
        return (byte) (x<<w);
    }

    /**
     * 将16进制字符串转换成字节流
     * @param string
     * @return
     */
    public static byte[] hexStr2bytes(String string){
        string=Utils.trimA(string);
        if(string.length()%2==1){
            //奇数
            return null;
        }
        int len=string.length()/2;
        byte[] ret=new byte[len];
        for(int i=0;i<len;i++){
            String temp=string.substring(i*2,i*2+2);
            try {
                ret[i]= (byte) (Integer.parseInt(temp,16)&0xff);
            }catch (Exception e){
                return null;
            }

        }

        return ret;
    }


    static public byte[] turnPer2(byte[] bytes){
        if(bytes.length%2==1){
            //奇数
            return null;
        }
        int cs=bytes.length/2;
        byte[] ret=new byte[bytes.length];
        for(int i=0;i<cs;i++){
            ret[bytes.length-2-i*2]=bytes[i*2];
            ret[bytes.length-1-i*2]=bytes[i*2+1];
        }

        return ret;
    }

    static public String turnPer2(String data){
        if(data.length()%2==1){
            //奇数
            return null;
        }
        int cs=data.length()/2;
        String ret="";
        for(int i=0;i<cs;i++){
           ret=ret+data.substring(data.length()-2-i*2,data.length()-i*2);
        }

        return ret;
    }


    /**
     * 字符串转16进制并翻转
     * @param str
     * @return
     */
    static public byte[] hexStr2bytesTurn(String str){
        byte[] b=hexStr2bytes(str);
        return turnPer2(b);
    }

    /**
     * 十进制转化为16进制的字符串,不满的位数补零
     * @param x
     * @param w 字节数
     * @return
     */
    public static String inter2HexString(int x,int w){
        String ret=Integer.toHexString(x);
        return bu(ret,w*2);
    }

    /**
     * 补零
     * @param str
     * @param len  字符串长度
     * @return
     */
    public static String bu(String str,int len){
        String ret="";
        int bu=len-str.length();
        if(bu<0){
            ret=str.substring(str.length()-2,str.length());
        }else{
            for(int i=0;i<bu;i++){
                ret=ret+"0";
            }
            ret=ret+str;
        }

        return ret;
    }

    static public String byte2String(byte b){
        int h=b&0xff;
        String ret=Integer.toHexString(h);
        if(ret.length()==1){
            ret="0"+ret;
        }
        return ret;
    }

    static public String bytes2String(byte[] b){
        String x="";
        try{
            for(int i=0;i<b.length;i++){
                int h=b[i]&0xff;
                String ret=Integer.toHexString(h);
                if(ret.length()==1){
                    ret="0"+ret;

                }
                x=x+ret;
            }
        }catch (Exception e){

        }
        x=x.toLowerCase();
        return x;
    }

    /**
     * 构造字符串
     * @param len   字节数
     * @return
     */
    public static String makeTestStr(int len){
        String ret="";
        for(int i=0;i<(len-1);i++){
            ret=ret+"00";
        }
        ret=ret+"01";
        return ret;
    }

    /**
     * 09+len+data
     * @param str
     * @return
     */
    static public String getObjectString(String str){
        int len=str.length()/2;
        String ret="09"+inter2HexString(len,1)+str;
        return ret;
    }

    static int[] fcstab={
        0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
        0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
        0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
        0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
        0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
        0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
        0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
        0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
        0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
        0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
        0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
        0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
        0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
        0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
        0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
        0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
        0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
        0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
        0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
        0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
        0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
        0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
        0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
        0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
        0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
        0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
        0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
        0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
        0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
        0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
        0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
        0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78
    };
    
    
    private static int pppfcs(int fcs,byte[] cp){
        int len=cp.length;
        for(int i=0;i<len;i++){
            int x=cp[i]&0x0ff;
            fcs=  ((fcs>>8)^fcstab[(fcs^x)&0xff])&0xffff;
        }
        return fcs;
    }

    public static int hcsChech(byte[] cp){
        int len=cp.length;
        int PPPINITFCS16=0xffff;
        int trialfcs;
        /* add on output */
        trialfcs=pppfcs( PPPINITFCS16,cp);
        trialfcs^=0xffff;
        return trialfcs;
    }

    public static boolean check(byte[] cp,int trialfcs){
        int len=cp.length;
        int PPPINITFCS16=0xffff;
        int PPPGOODFCS16=0xf0b8;
        /* add on output */
        byte[] cp2=new byte[len+2];
        System.arraycopy(cp,0,cp2,0,len);
        cp2[len]= (byte) (trialfcs & 0x00ff); /* least significant byte first */
        cp2[len+1]= (byte) ((trialfcs >> 8) & 0x00ff);
        /* check on input */
        trialfcs=pppfcs( PPPINITFCS16,cp2 );
        if ( trialfcs == PPPGOODFCS16 ){
            return true;
        }else{
            return true;
        }

    }


    public static void tryfcs16(byte[] cp){
        int len=cp.length;
        int PPPINITFCS16=0xffff;
        int PPPGOODFCS16=0xf0b8;
        int trialfcs;
        /* add on output */
        trialfcs=pppfcs( PPPINITFCS16,cp);
        trialfcs ^= 0xffff; /* complement */
        byte[] cp2=new byte[len+2];
        cp2[len]= (byte) (trialfcs & 0x00ff); /* least significant byte first */
        cp2[len+1]= (byte) ((trialfcs >> 8) & 0x00ff);
        /* check on input */
        trialfcs=pppfcs( PPPINITFCS16,cp2 );
        if ( trialfcs == PPPGOODFCS16 ){

        }

    }

    //2016-05-19 08:05:00:0164
    public static String parseDateTime(String s) {
        String[] split = s.split(" ");
        String[] split1 = split[0].split("-");
        String[] split2 = split[1].split(":");

        String ret="";
        ret=ret+Utils.inter2HexString(Integer.valueOf(split1[0]),2)+
                Utils.inter2HexString(Integer.valueOf(split1[1]),1)+
                Utils.inter2HexString(Integer.valueOf(split1[2]),1);
        String dayOfWeekByDate = getDayOfWeekByDate(split[0]);
        switch (dayOfWeekByDate){
            case "周一":
                ret=ret+"01";
                break;
            case "周二":
                ret=ret+"02";
                break;
            case "周三":
                ret=ret+"03";
                break;
            case "周四":
                ret=ret+"04";
                break;
            case "周五":
                ret=ret+"05";
                break;
            case "周六":
                ret=ret+"06";
                break;
            case "周日":
                ret=ret+"00";
                break;
            default:
                ret=ret+"00";
                break;
        }
        ret=ret+Utils.inter2HexString(Integer.valueOf(split2[0]),1)+
                Utils.inter2HexString(Integer.valueOf(split2[1]),1)+
                Utils.inter2HexString(Integer.valueOf(split2[2]),1)+
                Utils.inter2HexString(Integer.valueOf(split2[3]),2);
        return ret;

    }
    public static String getDayOfWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;
            } catch (Exception e) {

        }
        return dayOfweek;
    }

    static public String getCurTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");//设置日期格式
        String date=df.format(new Date());
        return date;
    }

    /**
     *
     * @param b 数组
     * @param w 字节数
     */
    public static int bytes2int(byte[] b,int pos,int w){
        int ret=0;
        for(int i=0;i<w;i++){
            ret=ret*256;
            int x=(b[pos+i]&0xff);
            ret+=x;
        }
        return ret;
    }

    public static byte[] getBytes(byte[] b,int len){
        byte[] x=new byte[len];
        System.arraycopy(b,0,x,0,len);
        return x;

    }
    public static String trimA(String data){
        String ret=data.toString().trim();
        ret=ret.replaceAll(" " ,"") ;
        return ret;
    }

}
