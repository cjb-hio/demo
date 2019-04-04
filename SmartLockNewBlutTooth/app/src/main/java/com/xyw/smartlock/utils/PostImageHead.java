package com.xyw.smartlock.utils;

/**
 * Created by acer on 2016/4/28.
 */

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public final class PostImageHead {
    /**
     * The imgur client ID for OkHttp recipes. If you're using imgur for anything
     * other than running these examples, please request your own client ID!
     * https://api.imgur.com/oauth2
     */
    private static final String IMGUR_CLIENT_ID = "9199fdef135c125";
    private static final MediaType AUDIO_TYPE_AMR = MediaType.parse("image/png");

    private final OkHttpClient client = new OkHttpClient();

    public String run(String url, String filePath) throws Exception {
        Log.e("URL", url);
        File file = new File(filePath);

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("name", "headiamge")
                .addFormDataPart("filename", file.getName(),
                        RequestBody.create(AUDIO_TYPE_AMR, file))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String strState = object.getString("result");
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return strState;
    }

}
