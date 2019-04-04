package com.xyw.smartlock.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by HP on 2017/5/31.
 */

public class MessageDialog extends Dialog {

    public MessageDialog(Context context, int themeResId, View layout) {
        super(context, themeResId);
        setContentView(layout);
    }
}
