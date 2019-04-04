package com.xyw.smartlock.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xyw.smartlock.bean.GuideBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by acer on 2016/5/12.
 */
public class GetPressword {
    String passnum;
    String mylid;
    GuideBean guideBean = new GuideBean();
    OkHttpClient client = new OkHttpClient();
    public GuideBean run(String url) throws IOException, JSONException {
        Log.e("TAG",url);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String strState = object.getString("result");
        passnum = object.getString("PASSNUM");
        mylid = object.getString("LID");
        guideBean.setPressword(passnum);
        guideBean.setMyLid(mylid);
        guideBean.setGuresult(strState);
        System.out.println("密码结果是="+strState);
        System.out.println("回来的Lid"+mylid);
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return guideBean;
    }

}
