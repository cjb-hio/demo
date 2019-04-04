package com.xyw.smartlock.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by acer on 2016/6/7.
 */
public class GetService {
    String process;
    OkHttpClient client = new OkHttpClient();
    public String run(String url) throws IOException, JSONException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String strState = object.getString("result");
        process = object.getString("process");
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return process;
    }
}
