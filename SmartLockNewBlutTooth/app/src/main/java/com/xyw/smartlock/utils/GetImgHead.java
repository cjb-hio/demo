package com.xyw.smartlock.utils;

import android.annotation.SuppressLint;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by acer on 2016/7/18.
 */
public class GetImgHead {
    //头像
    @SuppressLint("SdCardPath")
    private static String path = "/sdcard/myHead/";// sd路径
    OkHttpClient client = new OkHttpClient();
    public void run(String url,String name) throws IOException {
        File file1 = new File(path);
        if (file1.exists()){

        }else {
            file1.mkdirs();// 创建文件夹
        }


        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            byte[] bt =response.body().bytes();
            File file = new File(path+name);
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
