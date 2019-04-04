package com.xyw.smartlock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;


/**
 * Created by HP on 2017/7/18.
 */

public class CustomButton extends RelativeLayout {

    private ImageView custom_iv;
    private TextView custom_tv;
    private View view;

    public CustomButton(Context context) {
        super(context);
        initView(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.custom_button, this);
        custom_iv = (ImageView) view.findViewById(R.id.custom_iv);
        custom_tv = (TextView) view.findViewById(R.id.custom_tv);
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        custom_iv.setImageResource(array.getResourceId(R.styleable.CustomButton_imgsrc, R.mipmap.ic_launcher));
        custom_iv.getLayoutParams().width = array.getDimensionPixelSize(R.styleable.CustomButton_imgwidth, 40);
        custom_iv.getLayoutParams().height = array.getDimensionPixelSize(R.styleable.CustomButton_imgheight, 40);
        custom_tv.setText(array.getText(R.styleable.CustomButton_text));
        custom_tv.setTextSize(array.getDimensionPixelSize(R.styleable.CustomButton_textsize, 16));
        custom_tv.setTextColor(array.getColor(R.styleable.CustomButton_textcolor, 0xffffffff));
        array.recycle();
    }
}
