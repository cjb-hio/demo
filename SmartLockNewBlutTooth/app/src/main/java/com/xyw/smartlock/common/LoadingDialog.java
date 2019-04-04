package com.xyw.smartlock.common;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.xyw.smartlock.R;

/**
 * Created by Administrator on 2016/5/25.
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.5f;//透明度
        View view1 = getLayoutInflater().inflate(R.layout.loadingdialog,null);
        this.setContentView(view1);
        ProgressBar loading = (ProgressBar) view1.findViewById(R.id.loading);
//        Animation animation = AnimationUtils.loadAnimation(context,R.anim.loadingrotate);
//        LinearInterpolator lir = new LinearInterpolator();
//        animation.setInterpolator(lir);
//        loading.startAnimation(animation);



    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
