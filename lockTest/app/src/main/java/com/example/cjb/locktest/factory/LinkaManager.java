package com.example.cjb.locktest.factory;

import android.content.Context;

import com.example.cjb.locktest.factory.base.ILink;
import com.example.cjb.locktest.utils.Utils;

public class LinkaManager {

    private Context context;
    private ILink link;
    private static LinkaManager instance;

    public static LinkaManager getInstance(Context context,ILink link) {
        synchronized (LinkaManager.class){
            if(instance==null){
                instance=new LinkaManager(context,link);
            }
            return instance;
        }
    }

    private LinkaManager(Context context, ILink link){
        this.context=context;
        this.link=link;

        link.init(context);

    }

    public void connect(String address){
        link.connect(address);
    }

    public void send(String data){
        link.write(Utils.hexStr2bytes(data));
    }
}
