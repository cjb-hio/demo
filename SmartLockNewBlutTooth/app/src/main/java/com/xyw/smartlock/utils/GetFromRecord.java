package com.xyw.smartlock.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by acer on 2016/5/5.
 */
public class GetFromRecord {
    /** 录音存储路径 */
    private static final String PATH = "/sdcard/MyVoiceForder/";
    private static String result;
    OkHttpClient client = new OkHttpClient();
    public void run(String url,String name) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            byte[] bt =response.body().bytes();
            File file = new File(PATH+name);
            if (file.exists())
            {
                file.delete();
            }
            FileOutputStream fileout =  new FileOutputStream(file);
            fileout.write(bt, 0, bt.length);
            fileout.flush();

        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}

