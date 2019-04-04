package com.xyw.smartlock.utils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * Created by Administrator on 2016/5/23.
 */
public class Volley_Default_Time {
    public static void setDefaultRetryPolicy(JsonObjectRequest jsonObjectRequest){
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
