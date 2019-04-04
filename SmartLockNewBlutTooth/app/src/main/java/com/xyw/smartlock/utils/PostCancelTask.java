package com.xyw.smartlock.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by acer on 2016/5/9.
 */
public class PostCancelTask {
    OkHttpClient client = new OkHttpClient();

    public String run(String path) throws IOException, JSONException {
        Log.e("URL", path);
        Request request = new Request.Builder().url(path).build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String strState = object.getString("result");
        System.out.println("删除请求结果==" + strState);
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return strState;
    }
}
