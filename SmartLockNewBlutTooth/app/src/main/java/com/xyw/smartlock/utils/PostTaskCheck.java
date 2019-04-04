package com.xyw.smartlock.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by acer on 2016/5/6.
 */
public class PostTaskCheck {
    OkHttpClient client = new OkHttpClient();
    public String run(String path) throws IOException, JSONException {
        Request request = new Request.Builder().url(path).build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String strState = object.getString("result");
        System.out.println("表单请求结果=="+strState);
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return strState;
    }
}
