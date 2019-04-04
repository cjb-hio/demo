package com.xyw.smartlock.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by acer on 2016/6/12.
 */
public class GetSpace {
    String strState;
    OkHttpClient client = new OkHttpClient();
    public String run(String url) throws IOException, JSONException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        strState = object.getString("result");
        if (response.isSuccessful()) {
            return strState;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}
