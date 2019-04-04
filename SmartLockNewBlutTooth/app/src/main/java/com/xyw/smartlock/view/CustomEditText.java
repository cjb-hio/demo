package com.xyw.smartlock.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyw.smartlock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HP on 2017/7/24.
 */

public class CustomEditText extends RelativeLayout {

    private View view;

    private TextView et_tv_title;
    private EditText et_input;
    private ImageView et_img_delete, et_img_title;
    private boolean isGone = false;

    /**
     * @hide
     */
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public interface CustomTextWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    private CustomTextWatcher mCustomTextWatcher;

    public void setCustomTextWatcher(CustomTextWatcher watcher) {
        this.mCustomTextWatcher = watcher;
    }

    public CustomEditText(Context context) {
        super(context);
        initView(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.edit_text_item, this);
        et_tv_title = (TextView) view.findViewById(R.id.et_tv_title);
        et_input = (EditText) view.findViewById(R.id.et_input);
        et_img_delete = (ImageView) view.findViewById(R.id.et_img_delete);
        et_img_title = (ImageView) view.findViewById(R.id.et_img_title);
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        setTitle(array);
        setTitleImg(array);
        setDeleteImg(array);
        setEditText(array);
        array.recycle();
    }

    private void setTitle(TypedArray array) {
        et_tv_title.setText(array.getText(R.styleable.CustomEditText_etTextTitle));
        et_tv_title.setTextSize(array.getDimensionPixelSize(R.styleable.CustomEditText_etTextTitleSize, 16));
        et_tv_title.setTextColor(array.getColor(R.styleable.CustomEditText_etTextTitleColor, 0xff727272));
//        et_tv_title.setLines(array.getInt(R.styleable.CustomEditText_etTextTitleLines, 1));
        int et_tv_visibility = array.getInt(R.styleable.CustomEditText_etTextTitleVisible, 0);
        if (et_tv_visibility == 0)
            et_tv_title.setVisibility(VISIBLE);
        else if (et_tv_visibility == 4)
            et_tv_title.setVisibility(INVISIBLE);
        else if (et_tv_visibility == 8)
            et_tv_title.setVisibility(GONE);
    }

    private void setTitleImg(TypedArray array) {
        et_img_title.setImageResource(array.getResourceId(R.styleable.CustomEditText_etImgTitleSrc, 0));
        et_img_title.getLayoutParams().height = array.getDimensionPixelSize(R.styleable.CustomEditText_etImgTitleHeight, 15);
        et_img_title.getLayoutParams().width = array.getDimensionPixelSize(R.styleable.CustomEditText_etImgTitleWidth, 15);
        int img_title_visbility = array.getInt(R.styleable.CustomEditText_etImgTitleVisible, 0);
        if (img_title_visbility == 0)
            et_img_title.setVisibility(VISIBLE);
        else if (img_title_visbility == 4)
            et_img_title.setVisibility(INVISIBLE);
        else if (img_title_visbility == 8)
            et_img_title.setVisibility(GONE);
    }

    private void setDeleteImg(TypedArray array) {
        et_img_delete.setImageResource(array.getResourceId(R.styleable.CustomEditText_etImgDeleteSrc, 0));
        et_img_delete.getLayoutParams().height = array.getDimensionPixelSize(R.styleable.CustomEditText_etImgDeleteHeight, 15);
        et_img_delete.getLayoutParams().width = array.getDimensionPixelSize(R.styleable.CustomEditText_etImgDeleteWidth, 15);
        int img_delete_visbility = array.getInt(R.styleable.CustomEditText_etImgDeleteVisible, 0);
        if (img_delete_visbility == 0)
            et_img_delete.setVisibility(VISIBLE);
        else if (img_delete_visbility == 4)
            et_img_delete.setVisibility(INVISIBLE);
        else if (img_delete_visbility == 8) {
            et_img_delete.setVisibility(GONE);
            isGone = true;
        }
        et_img_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEtInput("");
                //设置新光标所在的位置
//                Selection.setSelection(editable, selEndIndex);
            }
        });
    }

    private void setEditText(TypedArray array) {
        setEtInput(array.getText(R.styleable.CustomEditText_etInputText));
//        et_input.setText(array.getText(R.styleable.CustomEditText_etInputText));
        et_input.setHint(array.getText(R.styleable.CustomEditText_etInputHint));
        et_input.setInputType(array.getInt(R.styleable.CustomEditText_etInputType, 0));
        et_input.setTextSize(array.getDimensionPixelSize(R.styleable.CustomEditText_etInputTextSize, 16));
        et_input.setTextColor(array.getColor(R.styleable.CustomEditText_etInputTextColor, 0xff727272));
//        et_input.setLines(array.getInt(R.styleable.CustomEditText_etInputTextLines, 1));
        final int et_input_maxlength = array.getInt(R.styleable.CustomEditText_etInputMaxLength, Integer.MAX_VALUE);
        int et_input_visibility = array.getInt(R.styleable.CustomEditText_etInputVisible, 0);
        if (et_input_visibility == 0)
            et_input.setVisibility(VISIBLE);
        else if (et_input_visibility == 4)
            et_input.setVisibility(INVISIBLE);
        else if (et_input_visibility == 8)
            et_input.setVisibility(GONE);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mCustomTextWatcher != null)
                    mCustomTextWatcher.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCustomTextWatcher != null)
                    mCustomTextWatcher.onTextChanged(s, start, before, count);
                Editable editable = et_input.getText();
                int len = editable.length();

                if(len > et_input_maxlength) {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0,et_input_maxlength);
                    et_input.setText(newStr);
                    editable = et_input.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if(selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCustomTextWatcher != null)
                    mCustomTextWatcher.afterTextChanged(s);
                setChangeDeleteImgVisibility();
            }
        });
        if (mOnEditorActionListener != null)
            et_input.setOnEditorActionListener(mOnEditorActionListener);
    }

    private TextView.OnEditorActionListener mOnEditorActionListener;

    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        this.mOnEditorActionListener = listener;
    }

    public void setDeleteImgVisibility(@Visibility int visibility) {
        et_img_delete.setVisibility(visibility);
        if (visibility == GONE) {
            isGone = true;
        } else {
            isGone = false;
        }
    }

    public void setEtInput(CharSequence text) {
        et_input.setText(text);
        setChangeDeleteImgVisibility();
    }

    public void setEtInput(int resId) {
        et_input.setText(resId);
        setChangeDeleteImgVisibility();
    }

    public void setEtInput(String text) {
        et_input.setText(text);
        setChangeDeleteImgVisibility();
    }

    public void setEtInputColor(int color) {
        et_input.setTextColor(color);
    }

    public void setEtInputColor(ColorStateList colors) {
        et_input.setTextColor(colors);
    }

    public void setEtInputSize(float size) {
        et_input.setTextSize(size);
    }

    public Editable getEtInput() {
        return et_input.getText();
    }

    private void setChangeDeleteImgVisibility() {
        if (!isGone) {
            if (!et_input.getText().toString().trim().equals("")) {
                et_img_delete.setVisibility(VISIBLE);
            } else {
                et_img_delete.setVisibility(INVISIBLE);
            }
        }
    }

    public void setEtTitle(CharSequence text) {
        et_tv_title.setText(text);
    }

    public void setEtTitle(int resId) {
        et_tv_title.setText(resId);
    }

    public void setEtTitle(String text) {
        et_tv_title.setText(text);
    }

    public void setEtTitleColor(int color) {
        et_input.setTextColor(color);
    }

    public void setEtTitleColor(ColorStateList colors) {
        et_input.setTextColor(colors);
    }

    public void setEtTitleSize(float size) {
        et_input.setTextSize(size);
    }

    public CharSequence getEtTitle() {
        return et_tv_title.getText();
    }

    public void setTitleImgSrc(int imgSrc) {
        et_img_title.setImageResource(imgSrc);
    }

    public void setDeleteImgSrc(int imgSrc) {
        et_img_delete.setImageResource(imgSrc);
    }
}
