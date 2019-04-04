package com.xyw.smartlock.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xyw.smartlock.R;
import com.xyw.smartlock.listener.OnCustomViewOnclickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2017/7/17.
 */

public class CustomView extends RelativeLayout {

    private Button btn_main;
    private int[] ids = {R.id.btn_child1, R.id.btn_child2, R.id.btn_child3, R.id.btn_child4, R.id.btn_child5, R.id.btn_child6, R.id.btn_child7};
    private CustomButton[] btns = new CustomButton[ids.length];
    private List<Point> mPoints = new ArrayList<>();
    private Point centerPoint;
    private double degree;

    private static final String TAG = "CustomView";
    private int translation;
    private boolean isFirstShow = true;

    //获取边长类型
    private static final int SIN_TYPE = 1;
    private static final int COS_TYPE = 2;

    private OnCustomViewOnclickListener mOnCustomViewOnclickListener;

    public void setOnCustomViewOnclickListener(OnCustomViewOnclickListener listener) {
        this.mOnCustomViewOnclickListener = listener;
    }

    public CustomView(Context context) {
        super(context);
        initView(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private boolean isShow = false;
    private void initView(Context context) {
        View.inflate(context, R.layout.btn_layout, CustomView.this);
        for (int i = 0; i < ids.length; i++) {
            final int child = i + 1;
            btns[i] = (CustomButton) this.findViewById(ids[i]);
            btns[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnCustomViewOnclickListener != null)
                        mOnCustomViewOnclickListener.onclick(view, child);
                }
            });
        }
        btn_main = (Button) this.findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShow) {
                    openBtn();
                } else {
                    closeBtn();
                }
                isShow = !isShow;
            }
        });
    }

    private void openBtn() {
        btn_main.setText("收起");
        for (int i = 0; i < btns.length; i++) {
            final CustomButton btn = btns[i];
            btns[i].animate()
                    .alpha(1f)
                    .translationX(mPoints.get(i).x)
                    .translationY(mPoints.get(i).y)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            btn.setVisibility(VISIBLE);
                        }
                    })
                    .setStartDelay(100)
                    .setInterpolator(new BounceInterpolator())
                    .start();
        }
    }

    private void closeBtn() {
        btn_main.setText("展开");
        for (int i = 0; i < btns.length; i++) {
            final CustomButton btn = btns[i];
            btn.animate()
                    .alpha(1f)
                    .translationX(0)
                    .translationY(0)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationStart(animation);
                            btn.setVisibility(GONE);
                        }
                    })
                    .setStartDelay(100)
                    .setInterpolator(new BounceInterpolator())
                    .start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        degree = Math.PI * 2 / ids.length;
        centerPoint = new Point(getWidth() / 2, getHeight() / 2);
        translation = getMeasuredWidth() / 3;
        for (int i = 0; i < ids.length; i++) {
            mPoints.add(new Point(getWidth() / 2 + getP_X(degree * i, i), getHeight() / 2 + getP_Y(degree * i, i)));
        }
        if (isFirstShow) {
            isFirstShow = false;
            openBtn();
            isShow = true;
            btn_main.setVisibility(GONE);
        }
    }

    private int getP_X(double degree, int index) {
        if (degree < Math.PI / 2 && degree >= 0) {
            return (int) (Math.cos(getD(degree, index, COS_TYPE)) * translation);
        } else if (degree >= Math.PI / 2 && degree < Math.PI) {
            return (int) (Math.sin(getD(degree, index, SIN_TYPE)) * translation);
        } else if (degree >= Math.PI && degree < Math.PI * 3 / 2) {
            return (int) (Math.cos(getD(degree, index, COS_TYPE)) * translation);
        } else if (degree >= Math.PI * 3 / 2 && degree < Math.PI * 2) {
            return (int) (Math.sin(getD(degree, index, SIN_TYPE)) * translation);
        }
        return 0;
    }

    private int getP_Y(double degree, int index) {
        if (degree < Math.PI / 2 && degree >= 0) {
            return (int) (Math.sin(getD(degree, index, SIN_TYPE)) * translation);
        } else if (degree >= Math.PI / 2 && degree < Math.PI) {
            return (int) (Math.cos(getD(degree, index, COS_TYPE)) * translation);
        } else if (degree >= Math.PI && degree < Math.PI * 3 / 2) {
            return (int) (Math.sin(getD(degree, index, SIN_TYPE)) * translation);
        } else if (degree >= Math.PI * 3 / 2 && degree < Math.PI * 2) {
            return (int) (Math.cos(getD(degree, index, COS_TYPE)) * translation);
        }
        return 0;
    }

    private double getD(double degree, int index, int type) {
        if (degree < Math.PI / 2 && degree >= 0) {
            return degree;
        } else if (degree >= Math.PI / 2 && degree < Math.PI) {
            if (type == COS_TYPE) {
                return degree - Math.PI / 2;
            } else if (type == SIN_TYPE) {
                return -(degree - Math.PI / 2);
            }
        } else if (degree >= Math.PI && degree < Math.PI * 3 / 2) {
            if (type == COS_TYPE) {
                return Math.PI - (degree - Math.PI);
            } else if (type == SIN_TYPE) {
                return -(degree - Math.PI);
            }
        } else if (degree >= Math.PI * 3 / 2 && degree < Math.PI * 2) {
            if (type == SIN_TYPE) {
                return degree - Math.PI * 3 / 2;
            }
            if (type == COS_TYPE) {
                return Math.PI - (degree - Math.PI * 3 / 2);
            }
        }
        return 0;
    }
}
