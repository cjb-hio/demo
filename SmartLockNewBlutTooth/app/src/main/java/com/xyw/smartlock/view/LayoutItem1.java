package com.xyw.smartlock.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by HP on 2017/7/22.
 */

public class LayoutItem1 extends RelativeLayout {

    private TextView item1_tv1, item1_tv2;
    private ImageView item1_iv1;
    private View view;

    /** @hide */
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface Visibility {}

    public LayoutItem1(Context context) {
        super(context);
        initView(context);
    }

    public LayoutItem1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LayoutItem1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_item1, this);
        item1_tv1 = (TextView) view.findViewById(R.id.item1_tv1);
        item1_tv2 = (TextView) view.findViewById(R.id.item1_tv2);
        item1_iv1 = (ImageView) view.findViewById(R.id.item1_iv1);
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LayoutItem1);
        item1_tv1.setText(array.getText(R.styleable.LayoutItem1_itemText));
        item1_tv1.setTextColor(array.getColor(R.styleable.LayoutItem1_itemTextColor, 0xff727272));
        item1_tv1.setTextSize(array.getDimensionPixelSize(R.styleable.LayoutItem1_itemTextSize, 16));
        int tv1_visibility = array.getInt(R.styleable.LayoutItem1_itemTextVisible, 0);
        if (tv1_visibility == 0)
            item1_tv1.setVisibility(VISIBLE);
        else if (tv1_visibility == 4)
            item1_tv1.setVisibility(INVISIBLE);
        else if (tv1_visibility == 8)
            item1_tv1.setVisibility(GONE);

        item1_tv2.setText(array.getText(R.styleable.LayoutItem1_itemText2));
        item1_tv2.setTextColor(array.getColor(R.styleable.LayoutItem1_itemText2Color, 0xff727272));
        item1_tv2.setTextSize(array.getDimensionPixelSize(R.styleable.LayoutItem1_itemText2Size, 14));
        int tv2_visibility = array.getInt(R.styleable.LayoutItem1_itemTextVisible, 0);
        if (tv2_visibility == 0)
            item1_tv2.setVisibility(VISIBLE);
        else if (tv2_visibility == 4)
            item1_tv2.setVisibility(INVISIBLE);
        else if (tv2_visibility == 8)
            item1_tv2.setVisibility(GONE);

        item1_iv1.setImageResource(array.getResourceId(R.styleable.LayoutItem1_itemImgSrc, 0));
        item1_iv1.getLayoutParams().height = array.getDimensionPixelSize(R.styleable.LayoutItem1_itemImgHeight, 20);
        item1_iv1.getLayoutParams().width = array.getDimensionPixelSize(R.styleable.LayoutItem1_itemImgWidth, 20);
        int iv1_visbility = array.getInt(R.styleable.LayoutItem1_itemImgVisible, 0);
        if (iv1_visbility == 0)
            item1_iv1.setVisibility(VISIBLE);
        else if (iv1_visbility == 4)
            item1_iv1.setVisibility(INVISIBLE);
        else if (iv1_visbility == 8)
            item1_iv1.setVisibility(GONE);
        array.recycle();
    }

    public void setTextView1Text(String text) {
        item1_tv1.setText(text);
    }

    public CharSequence getTextView1Text() {
        return item1_tv1.getText();
    }

    public void setTextView1Text(int resInt) {
        item1_tv1.setText(resInt);
    }

    public void setTextView1TextColor(int color) {
        item1_tv1.setTextColor(color);
    }

    public void setTextView1TextColor(ColorStateList color) {
        item1_tv1.setTextColor(color);
    }

    public void setTextView1TextSize(int size) {
        item1_tv1.setTextSize(size);
    }

    public void setTextView1Visible(@Visibility int visibility) {
        item1_tv1.setVisibility(visibility);
    }

    public void setTextView2Text(String text) {
        item1_tv2.setText(text);
    }

    public CharSequence getTextView2Text() {
        return item1_tv2.getText();
    }

    public void setTextView2Text(int resInt) {
        item1_tv2.setText(resInt);
    }

    public void setTextView2TextColor(int color) {
        item1_tv2.setTextColor(color);
    }

    public void setTextView2TextColor(ColorStateList color) {
        item1_tv2.setTextColor(color);
    }

    public void setTextView2TextSize(int size) {
        item1_tv2.setTextSize(size);
    }

    public void setTextView2Visible(@Visibility int visibility) {
        item1_tv2.setVisibility(visibility);
    }

    public void setImg1Scr(int src) {
        item1_iv1.setImageResource(src);
    }

    public void setImg1Visible(@Visibility int visibility) {
        item1_iv1.setVisibility(visibility);
    }

    public void setImg1Width(int width) {
        item1_iv1.getLayoutParams().width = width;
    }

    public void setImgHieght(int height) {
        item1_iv1.getLayoutParams().height = height;
    }
}
