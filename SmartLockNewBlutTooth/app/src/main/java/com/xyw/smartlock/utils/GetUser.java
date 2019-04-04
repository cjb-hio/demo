package com.xyw.smartlock.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xyw.smartlock.bean.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/6/4.
 */
public class GetUser {
    List<UserBean> userBeanList = new ArrayList<UserBean>();
    OkHttpClient client = new OkHttpClient();

    public List<UserBean> run(String url) throws IOException {
        Log.e("TAG", url);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        try {
            JSONObject js = new JSONObject(response.body().string());
            JSONArray jar = js.getJSONArray("LOCK_OP");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject object = jar.getJSONObject(i);
                UserBean bean = new UserBean();
                bean.setName(object.getString("OP_NAME"));
                bean.setNumber(object.getString("OP_NO"));
                userBeanList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            return userBeanList;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}

