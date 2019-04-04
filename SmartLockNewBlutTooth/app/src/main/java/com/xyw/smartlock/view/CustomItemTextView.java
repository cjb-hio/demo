package com.xyw.smartlock.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;


/**
 * Created by HP on 2017/8/4.
 */

public class CustomItemTextView extends RelativeLayout {

    private View view;
    private TextView custom_tv_title, custom_tv_content;
    private float density;

    public CustomItemTextView(Context context) {
        super(context);
        initView(context);
    }

    public CustomItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomItemTextView);
        initTitle(array);
        initContent(array);
        array.recycle();
    }

    private void initContent(TypedArray array) {
        custom_tv_content.setText(array.getText(R.styleable.CustomItemTextView_content));
        custom_tv_content.setTextSize(array.getDimensionPixelSize((R.styleable.CustomItemTextView_contentSize), 48) / density);
        custom_tv_content.setTextColor(array.getColor(R.styleable.CustomItemTextView_contentColor, 0xff727272));
        custom_tv_content.setLines(array.getInt(R.styleable.CustomItemTextView_contentLines, 1));
        int et_tv_visibility = array.getInt(R.styleable.CustomItemTextView_contentVisible, 0);
        if (et_tv_visibility == 0)
            custom_tv_content.setVisibility(VISIBLE);
        else if (et_tv_visibility == 4)
            custom_tv_content.setVisibility(INVISIBLE);
        else if (et_tv_visibility == 8)
            custom_tv_content.setVisibility(GONE);
    }

    private void initTitle(TypedArray array) {
        custom_tv_title.setText(array.getText(R.styleable.CustomItemTextView_title));
        custom_tv_title.setTextSize(array.getDimensionPixelSize(R.styleable.CustomItemTextView_titleSize, 48) / density);
        custom_tv_title.setTextColor(array.getColor(R.styleable.CustomItemTextView_titleColor, 0xff727272));
        custom_tv_title.setLines(array.getInt(R.styleable.CustomItemTextView_titleLines, 1));
        int et_tv_visibility = array.getInt(R.styleable.CustomItemTextView_titleVisible, 0);
        if (et_tv_visibility == 0)
            custom_tv_title.setVisibility(VISIBLE);
        else if (et_tv_visibility == 4)
            custom_tv_title.setVisibility(INVISIBLE);
        else if (et_tv_visibility == 8)
            custom_tv_title.setVisibility(GONE);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.custom_textview, this);
        custom_tv_title = (TextView) view.findViewById(R.id.custom_tv_title);
        custom_tv_content = (TextView) view.findViewById(R.id.custom_tv_content);
    }

    public void setTitle(CharSequence text) {
        custom_tv_title.setText(text);
    }

    public void setTitle(int resid) {
        custom_tv_title.setText(resid);
    }

    public void setTitleColor(int color) {
        custom_tv_title.setTextColor(color);
    }

    public void setTitleColor(ColorStateList colors) {
        custom_tv_title.setTextColor(colors);
    }

    public void setTitleSize(int size) {
        custom_tv_title.setTextSize(size);
    }

    public void setContent(CharSequence text) {
        custom_tv_content.setText(text);
    }

    public void setContent(int resid) {
        custom_tv_content.setText(resid);
    }

    public void setContentColor(int color) {
        custom_tv_content.setTextColor(color);
    }

    public void setContentColor(ColorStateList colors) {
        custom_tv_content.setTextColor(colors);
    }

    public void setContentSize(int size) {
        custom_tv_content.setTextSize(size);
    }
}
