package com.xyw.smartlock.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xyw.smartlock.bean.TaskBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2016/5/3.
 */
public class GetTaskList {
    List<TaskBean> taskBeanList = new ArrayList<TaskBean>();
    List<SendWork> SendWorkList = new ArrayList<SendWork>();
    String str;
    OkHttpClient client = new OkHttpClient();

    public List<TaskBean> run(String url) throws IOException {
        Log.e("URL", url);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        try {
            JSONObject js = new JSONObject(response.body().string());
            JSONArray jar = js.getJSONArray("LOCK_TASK");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject object = jar.getJSONObject(i);
                if (object.getString("HAVE_INFO").equals("1")) {
                    SendWork sendWork = new SendWork();
                    sendWork.setTask_no(object.getString("TASK_NO"));
                    sendWork.setR_op_no(object.getString("R_OP_NO"));
                    sendWork.setArea(object.getString("ZONE_NAME"));
                    sendWork.setR_ZONE_NO(object.getString("R_ZONE_NO"));
                    sendWork.setStartTime(object.getString("V_BEGINTIME"));
                    sendWork.setEndTime(object.getString("V_ENDTIME"));
                    sendWork.setContent(object.getString("DEMO"));
                    sendWork.setRET_V(object.getString("RET_V"));
                    sendWork.setRET_OP_NO(object.getString("RET_OP_NO"));
                    sendWork.setPath1(object.getString("AUDIO_PATH1"));
                    sendWork.setPath2(object.getString("AUDIO_PATH2"));
                    sendWork.setMyName(object.getString("OP_NAME"));
                    sendWork.setROLE_ID(object.getString("ROLE_ID"));
                    sendWork.setHAVE_INFO(object.getString("HAVE_INFO"));
                    SendWorkList.add(sendWork);
                } else if (object.getString("HAVE_INFO").equals("0")) {
                    TaskBean bean = new TaskBean();
                    bean.setTask_no(object.getString("TASK_NO"));
                    bean.setR_op_no(object.getString("R_OP_NO"));
                    bean.setArea(object.getString("ZONE_NAME"));
                    bean.setR_ZONE_NO(object.getString("R_ZONE_NO"));
                    bean.setStartTime(object.getString("V_BEGINTIME"));
                    bean.setEndTime(object.getString("V_ENDTIME"));
                    bean.setContent(object.getString("DEMO"));
                    bean.setRET_V(object.getString("RET_V"));
                    bean.setRET_OP_NO(object.getString("RET_OP_NO"));
                    bean.setPath1(object.getString("AUDIO_PATH1"));
                    bean.setPath2(object.getString("AUDIO_PATH2"));
                    bean.setMyName(object.getString("OP_NAME"));
                    bean.setROLE_ID(object.getString("ROLE_ID"));
                    bean.setHAVE_INFO(object.getString("HAVE_INFO"));
                    taskBeanList.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            return taskBeanList;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public List<SendWork> run1(String url) throws IOException {
        Log.e("URL", url);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        try {
            JSONObject js = new JSONObject(response.body().string());
            JSONArray jar = js.getJSONArray("LOCK_TASK");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject object = jar.getJSONObject(i);
                if (object.getString("HAVE_INFO").equals("0")) {
                    TaskBean bean = new TaskBean();
                    bean.setTask_no(object.getString("TASK_NO"));
                    bean.setR_op_no(object.getString("R_OP_NO"));
                    bean.setArea(object.getString("ZONE_NAME"));
                    bean.setR_ZONE_NO(object.getString("R_ZONE_NO"));
                    bean.setStartTime(object.getString("V_BEGINTIME"));
                    bean.setEndTime(object.getString("V_ENDTIME"));
                    bean.setContent(object.getString("DEMO"));
                    bean.setRET_V(object.getString("RET_V"));
                    bean.setRET_OP_NO(object.getString("RET_OP_NO"));
                    bean.setPath1(object.getString("AUDIO_PATH1"));
                    bean.setPath2(object.getString("AUDIO_PATH2"));
                    bean.setMyName(object.getString("OP_NAME"));
                    bean.setROLE_ID(object.getString("ROLE_ID"));
                    bean.setHAVE_INFO(object.getString("HAVE_INFO"));
                    taskBeanList.add(bean);
                } else {
                    SendWork sendWork = new SendWork();
                    sendWork.setTask_no(object.getString("TASK_NO"));
                    sendWork.setR_op_no(object.getString("R_OP_NO"));
                    sendWork.setArea(object.getString("ZONE_NAME"));
                    sendWork.setR_ZONE_NO(object.getString("R_ZONE_NO"));
                    sendWork.setStartTime(object.getString("V_BEGINTIME"));
                    sendWork.setEndTime(object.getString("V_ENDTIME"));
                    sendWork.setContent(object.getString("DEMO"));
                    sendWork.setRET_V(object.getString("RET_V"));
                    sendWork.setRET_OP_NO(object.getString("RET_OP_NO"));
                    sendWork.setPath1(object.getString("AUDIO_PATH1"));
                    sendWork.setPath2(object.getString("AUDIO_PATH2"));
                    sendWork.setMyName(object.getString("OP_NAME"));
                    sendWork.setROLE_ID(object.getString("ROLE_ID"));
                    sendWork.setHAVE_INFO(object.getString("HAVE_INFO"));
                    SendWorkList.add(sendWork);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            return SendWorkList;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}
