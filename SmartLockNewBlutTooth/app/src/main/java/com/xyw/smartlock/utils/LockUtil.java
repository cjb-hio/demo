package com.xyw.smartlock.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 锁设备解密加密工具类
 * Created by HP on 2017/4/10.
 */

public class LockUtil {

    private static LockUtil mLockUtil;

    public static LockUtil getInstance() {
        if (mLockUtil == null) {
            synchronized (LockUtil.class) {
                if (mLockUtil == null){
                    mLockUtil = new LockUtil();
                }
            }
        }
        return mLockUtil;
    }

    /**
     * 检测返回数据
     * @param data
     * @return
     */
    public byte checkPacket(byte[] data) {
        byte len;
        if (data[0] == 0x55) {
            len = data[1];
            byte[] data1 = new byte[len + 3];
            for (int i = 0; i < len + 3; i++) data1[i] = data[i];
            if (len > 32 - 5) return 0;
            if (CheckSum(data1) == data[len + 3]) {
                if (data[len + 4] == (byte) 0xaa)
                    return 1;
            }
        }
        return 0;
    }

    /**
     *
     * @param command
     * @param data
     * @return
     */
    public byte[] makePacket(byte command, byte[] data) {
        byte[] buffer = new byte[data.length + 5];
        buffer[0] = (byte) 0x55;
        buffer[1] = (byte) data.length;
        buffer[2] = command;
        if (data != null && data.length > 0)
            for (int i = 0; i < data.length; i++) buffer[3 + i] = data[i];

        buffer[3 + data.length] = (byte) CheckSum(data);
        buffer[3 + data.length] += buffer[0];
        buffer[3 + data.length] += buffer[1];
        buffer[3 + data.length] += buffer[2];
        buffer[3 + data.length + 1] = (byte) 0xaa;
        Log.e("LockUtil", "makePacket: buffer = " + bytes2HexString(buffer));
        return buffer;
    }

    /**
     * 检测发送数据
     * @param data
     * @return
     */
    private byte CheckSum(byte[] data) {
        byte sum = 0;
        if (data == null) return sum;
        for (int i = 0; i < data.length; i++)
            sum += data[i];
        return sum;
    }

    // add by yins
    /**
     * 16进制字符串转二进制字节数组
     * @param s
     * @return
     */
    public byte[] hexStr2Bytes(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    /**
     * 二进制转字符串
     * @param b
     * @return
     */
    public String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[ i ] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
    private byte[] newKeyofLid(String ZoneKey, byte[] lid)
    {
        //设置新密
        //String ZoneKey=PublicKeyStr;
        String LidKeyM="A1B2C3D4E5F613243546587A8B9CADBC";
        //加密初始化数据
        byte[]dat=hexStr2Bytes(ZoneKey);
        byte[]key1=hexStr2Bytes(LidKeyM);
        //生成子密
        for(int i=0;i<4;i++)
        {
            key1[i]=lid[i+4];
            key1[i+8]=(byte)(255-lid[i+4]);
        }
        dat=encrypt(dat, key1, 32);//这是当前锁的密钥
        return dat;

    }
    public byte[] encrypt(byte[] content,  byte[] key1, int times){//times为加密轮数
        int[] tempInt = byteToInt(content, 0);
        int[] key = byteToInt(key1, 0);
        int tempx,tempy,tempz;
        int y = tempInt[0], z = tempInt[1], sum = 0, i;
        int delta=0x9e3779b9; //这是算法标准给的值
        int a = key[0], b = key[1], c = key[2], d = key[3];

        for (i = 0; i < times; i++) {

            sum += delta;
            tempx=((z<<4) + a);
            tempy=(z + sum);
            tempz=((z>>>5) + b);
            y+=tempx^tempy^tempz;

            tempx= ((y<<4) + c);
            tempy=(y + sum);
            tempz=((y>>>5) + d);
            z+=tempx^tempy^tempz;

        }
        tempInt[0]=y;
        tempInt[1]=z;
        return intToByte(tempInt, 0);
    }

    //解密
    public byte[] decrypt(byte[] encryptContent,  int[] key, int times){
        int[] tempInt = byteToInt(encryptContent, 0);
        int y = tempInt[0], z = tempInt[1], sum = 0, i;
        int delta=0x9e3779b9; //这是算法标准给的值
        int a = key[0], b = key[1], c = key[2], d = key[3];
        if (times == 32)
            sum = 0xC6EF3720; /* delta << 5*/
        else if (times == 16)
            sum = 0xE3779B90; /* delta << 4*/
        else
            sum = delta * times;

        for(i = 0; i < times; i++) {
            z -= ((y<<4) + c) ^ (y + sum) ^ ((y>>5) + d);
            y -= ((z<<4) + a) ^ (z + sum) ^ ((z>>5) + b);
            sum -= delta;
        }
        tempInt[0] = y;
        tempInt[1] = z;

        return intToByte(tempInt, 0);
    }
    //若某字节为负数则需将其转成无符号正数
    public int transform(byte temp){
        int tempInt = (int)temp;
        if(tempInt < 0){
            tempInt += 256;
        }
        return tempInt;
    }
    //byte[]型数据转成int[]型数据
    private int[] byteToInt(byte[] content, int offset){

        int[] result = new int[content.length >> 2];//除以2的n次方 == 右移n位 即 content.length / 4 == content.length >> 2
        for(int i = 0, j = offset; j < content.length; i++, j += 4){
            result[i] = transform(content[j + 0]) | transform(content[j + 1]) << 8 |
                    transform(content[j + 2]) << 16 | (int)content[j+3] << 24;
        }
        return result;

    }
    //int[]型数据转成byte[]型数据
    private byte[] intToByte(int[] content, int offset){
        byte[] result = new byte[content.length << 2];//乘以2的n次方 == 左移n位 即 content.length * 4 == content.length << 2
        for(int i = 0, j = offset; j < result.length; i++, j += 4){
            result[j + 0] = (byte)(content[i] & 0xff);
            result[j + 1] = (byte)((content[i] >> 8) & 0xff);
            result[j + 2] = (byte)((content[i] >> 16) & 0xff);
            result[j + 3] = (byte)((content[i] >> 24) & 0xff);
        }
        return result;
    }


    public byte []  makeorder( byte [] lid,byte [] rand1,String thisZoneKey)
    {
        String PublicKeyStr="0810151308107781";
        byte [] req=new byte[13];
        byte [] key1=new byte[16];
        // byte [] lid=new byte[8];
        // byte [] rand1=new byte[4];
        byte [] rand2=new byte[4];
        byte [] dat=new byte [8];
        int i;
        Random random=new Random();
        rand2[0]=(byte)random.nextInt(256);
        rand2[1]=(byte)random.nextInt(256);
        rand2[2]=(byte)random.nextInt(256);
        rand2[3]=(byte)random.nextInt(256);

        req=new byte[13];
        String FirstKey=PublicKeyStr;
        if(lid[2]==0x00)
        {
            //加密初始化数据
            dat=hexStr2Bytes(FirstKey);
        }else
        {
            dat=newKeyofLid(thisZoneKey,lid);//这是当前锁的密钥
        }
        for(i=0;i<8;i++)
        {
            key1[i]=dat[i];
            key1[i+8]=(byte)(255-dat[i]);
        }
        for(i=0;i<4;i++)
        {
            dat[i]=rand1[i];
            dat[i+4]=rand2[i];
        }
        dat=encrypt(dat, key1, 32);
        //设置发送值
        for(i=0;i<8;i++)
        {
            req[i]=dat[i];
        }
        req[8]=0x00;
        //
        for(i=0;i<4;i++)
        {
            req[9+i]=rand2[i];
        }
        return req;
    }

    /**
     * 中文字符转16进制字节
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     */
    public byte[] getBytesFromChs(String s) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("GB2312");
        return bytes;
    }

    /**
     * 16进制转中文字符
     *
     * @param bytes
     * @return
     */
    public String getChsFromHex(byte[] bytes) {
        String str = "";
        try {
            str = new String(bytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
}
