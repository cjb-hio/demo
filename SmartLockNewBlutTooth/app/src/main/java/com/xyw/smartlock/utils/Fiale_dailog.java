package com.xyw.smartlock.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xyw.smartlock.R;

/**
 * Created by acer on 2016/5/16.
 */
public class Fiale_dailog extends Dialog {
    public Fiale_dailog(Context context) {
        super(context);
    }

    public Fiale_dailog(Context context, int themeResId) {
        super(context, themeResId);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.9f;//透明度
        View view1 = getLayoutInflater().inflate(R.layout.dailog_fail,null);
        this.setContentView(view1);
        Button cancel = (Button) view1.findViewById(R.id.fanhui);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    protected Fiale_dailog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
