package com.example.cjb.myglide.glide;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestManagerRetriever implements Handler.Callback {

    GlideContext glideContext;
    HashMap<String, RequestmanagerFragment> fragmentHashMap = new HashMap<>();
    Handler handler = new Handler(Looper.getMainLooper(), this);

    public RequestManagerRetriever(GlideContext glideContext) {
        this.glideContext = glideContext;
    }

    public RequestManager get(Context activity) {
        if (!(activity instanceof Application)) {
            if (activity instanceof FragmentActivity) {
                return getFragmentRequestmanager((FragmentActivity) activity);
            }
        }

        return getApplicationRequestmanager();
    }

    private RequestManager getFragmentRequestmanager(FragmentActivity context) {
        RequestmanagerFragment fragment = null;
        FragmentManager fm = context.getSupportFragmentManager();
        if (fragmentHashMap.get("fragment") == null) {
            fragment = (RequestmanagerFragment) fm.findFragmentByTag("fragment");
            if (fragment == null) {
                fragment = new RequestmanagerFragment();
                fragmentHashMap.put("fragment", fragment);
                fm.beginTransaction().add(fragment, "fragment").commit();
            }
        } else {
            fragment = fragmentHashMap.get("fragment");

        }
        handler.obtainMessage(10).sendToTarget();//删除

        RequestManager requestManager=fragment.getRequestManager();
        if (requestManager == null) {
            requestManager = new RequestManager(glideContext);
            fragment.setRequestManager(requestManager);
        }

        return requestManager;

    }

    private RequestManager getApplicationRequestmanager() {
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 10) {
            fragmentHashMap.remove("fragment");
        }
        return true;
    }
}
