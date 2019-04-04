package com.xyw.smartlock.utils;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Dacer on 10/8/13.
 */
public class MyUtils {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static String getHttpParam(HashMap<String, String> hashMap) {
		StringBuffer value = new StringBuffer();
		if (hashMap != null) {
			for (String key : hashMap.keySet()) {
				value.append("&");
				value.append(key);
				value.append("=");
				value.append(hashMap.get(key));
			}
		}
		return value.toString();
	}
}
