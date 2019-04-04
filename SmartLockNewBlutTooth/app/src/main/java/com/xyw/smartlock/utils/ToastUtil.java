package com.xyw.smartlock.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void MyToast(Context context ,String message){
		  Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); 
	}

}
