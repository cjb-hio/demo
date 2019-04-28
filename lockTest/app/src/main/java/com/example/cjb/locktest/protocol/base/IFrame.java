package com.example.cjb.locktest.protocol.base;

import com.example.cjb.locktest.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class IFrame {

    int pos;
    public boolean isParse=false;

    protected String getOI(){
        return null;
    }
    protected byte getAttr(){
        return 0x0;
    }
    public  void parse(byte[] data,int pos){
        int x=0;
        int x1;
        int x2;
        String temp="";
        byte b=0x0;

        ArrayList<Field> fieldList=new ArrayList<>();
        HashMap<String, Field> map=new HashMap<>();
        String ret="";
        Class<?> aClass = getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        for(Field field:declaredFields){
            order annotation = field.getAnnotation(order.class);
            if(annotation!=null){
                map.put(Integer.toString(annotation.value()),field);
            }
        }
        //开始解析
        try{
            for(int i=0;i<map.size();i++){
                if(pos>=data.length){
                    return;
                }
                Field field=map.get(Integer.toString(i+1));
                annocation annotation = field.getAnnotation(annocation.class);

                if(annotation==null) {
                    //如果是正常的类型
                    String type = field.getGenericType().toString();
                    switch (type) {
                        case "byte":
                            b=data[pos];
                            pos++;
                            field.set(this,b);
                            break;
                        case "int":
                            byte[] f=new byte[4];
                            System.arraycopy(data,pos,f,0,4);
                            x = Utils.bytes2int(f, 0, 4);
                            pos+=4;
                            field.set(this,x);
                            break;
                    }
                }else{
                    String value = annotation.value();
                    switch (value){
                        case "long":
                            //两个字节
                            x1=data[pos]&0xff;
                            x2=data[pos+1]&0xff;
                            pos+=2;
                            x=x1*256+x2;
                            field.set(this,x);
                            break;
                        case "date_time":
                            temp="";
                            //年
                            x=Utils.bytes2int(data,pos,2);
                            pos+=2;
                            temp=temp+Integer.toString(x)+"-";
                            //月
                            x=Utils.bytes2int(data,pos,1);
                            temp=temp+Utils.bu(Integer.toString(x),2)+"-";
                            pos+=1;
                            //日
                            x=Utils.bytes2int(data,pos,1);
                            temp=temp+Utils.bu(Integer.toString(x),2)+" ";
                            pos+=2;
                            //时
                            x=Utils.bytes2int(data,pos,1);
                            temp=temp+Utils.bu(Integer.toString(x),2)+":";
                            pos+=1;
                            //分
                            x=Utils.bytes2int(data,pos,1);
                            temp=temp+Utils.bu(Integer.toString(x),2)+":";
                            pos+=1;
                            //秒
                            x=Utils.bytes2int(data,pos,1);
                            temp=temp+Utils.bu(Integer.toString(x),2)+":";
                            pos+=1;
                            //毫秒
                            x=Utils.bytes2int(data,pos,2);
                            temp=temp+Integer.toString(x);
                            pos+=2;
                            field.set(this,temp);
                            break;
                        case "size2":
                            //0个字节
                            byte[] m=new byte[2];
                            System.arraycopy(data,pos,m,0,2);
                            pos+=2;
                            temp=Utils.bytes2String(m);
                            field.set(this,temp);
                            break;

                        case "size8":
                            //0个字节
                            m=new byte[8];
                            System.arraycopy(data,pos,m,0,8);
                            pos+=8;
                            temp=Utils.bytes2String(m);
                            field.set(this,temp);
                            break;
                        case "size16":
                            //0个字节
                            m=new byte[16];
                            System.arraycopy(data,pos,m,0,16);
                            pos+=16;
                            temp=Utils.bytes2String(m);
                            field.set(this,temp);
                            break;
                        case "size32":
                            //0个字节
                            m=new byte[32];
                            System.arraycopy(data,pos,m,0,32);
                            pos+=32;
                            temp=Utils.bytes2String(m);
                            field.set(this,temp);
                            break;
                        case "data"://结果：0：dar    1:data
                            processData(getOI(),getAttr(),data,pos,field);
                            break;
                        case "op_result"://操作的返回结果

                            x1=data[pos]&0xff;//类型
                            x2=data[pos+1]&0xff;//type
                            x=data[pos+2]&0xff;//size
                            if(x1==1){
                                //基础类型
                                if(x2==9){
                                    pos+=3;
                                    //字符串
                                    m=new byte[x];
                                    System.arraycopy(data,pos,m,0,x);
                                    pos+=x;
                                    field.set(this,Utils.bytes2String(m));
                                }else if(x2==22){
                                    int em=x&0xff;
                                    field.set(this,Integer.toString(em));
                                }else if(x2==3){
                                    int em=x&0xff;
                                    field.set(this,Integer.toString(em));
                                }
                            }else if(x1==3){
                                //特殊类型
                                int em=x2&0xff;
                                field.set(this,Integer.toString(em));
                            }
                            break;
                    }
                }

            }
            isParse=true;
            this.pos=pos;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processData(String oi, byte attr, byte[] data, int pos, Field field) throws IllegalAccessException {
        int x,x1,x2;
        byte[] m;
        if(oi.equals("")){

        }else if(oi.equals("f100")&&attr==7){
            //当前计数器
           x=Utils.bytes2int(data,pos+4,4);
           field.set(this,Integer.toString(x));
           pos+=18;
        }else{
            x1=data[pos]&0xff;//类型
            x2=data[pos+1]&0xff;//type
            x=data[pos+2]&0xff;//size
            if(x1==1){
                //基础类型
                if(x2==9){
                    pos+=3;
                    //字符串
                    m=new byte[x];
                    System.arraycopy(data,pos,m,0,x);
                    pos+=x;
                    field.set(this,Utils.bytes2String(m));
                }else if(x2==22){
                    int em=x&0xff;
                    field.set(this,Integer.toString(em));
                }else if(x2==3){
                    int em=x&0xff;
                    field.set(this,Integer.toString(em));
                }
            }else{
                //错误
                int dar=x2&0xff;
                field.set(this,Integer.toString(dar));
            }
        }
    }

    public String make(){
        ArrayList<Field> fieldList=new ArrayList<>();
        HashMap<String, Field> map=new HashMap<>();
        int x=0;
        String ret="";
        String temp="";
        Class<?> aClass = getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        for(Field field:declaredFields){
            order annotation = field.getAnnotation(order.class);
            if(annotation!=null){
                map.put(Integer.toString(annotation.value()),field);
            }
        }

        try{
            for(int i=0;i<map.size();i++){
                Field field=map.get(Integer.toString(i+1));
                annocation annotation = field.getAnnotation(annocation.class);

                if(annotation==null) {
                    //如果是正常的类型
                    String type = field.getGenericType().toString();
                    switch (type) {
                        case "byte":
                            byte b = field.getByte(this);
                            ret=ret+Utils.byte2String(b);
                            break;
                        case "int":
                             x= field.getInt(this);
                             ret=ret+Utils.inter2HexString(x,4);
                            break;
                    }
                }else{
                    String value = annotation.value();
                    switch (value){
                        case "long":
                            int anInt = field.getInt(this);
                            ret=ret+Utils.inter2HexString(anInt,2);
                            break;
                        case "date_time":
                            String s= (String) field.get(this);
                            ret=ret+Utils.parseDateTime(s);
                            break;
                       default:
                            temp= (String) field.get(this);
                            ret=ret+temp;
                            break;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }
}
