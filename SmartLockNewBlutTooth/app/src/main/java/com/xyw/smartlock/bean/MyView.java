package com.xyw.smartlock.bean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.xyw.smartlock.R;

/**
 * Created by acer on 2016/5/19.
 */
public class MyView extends View {
    private float mcount;
    private float mcount0;
    private float mcount1;
    private float mcount2;

    private int mwidth;
    private Context mContext;

    private Paint myPaint;
    private Paint myPaint0;
    private Paint myPaint1;
    private Paint myPaint2;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context c) {
        mContext = c;
        myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(getResources().getColor(R.color.bowen0));

        myPaint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint0.setStyle(Paint.Style.STROKE);
        myPaint0.setColor(getResources().getColor(R.color.bowen0));

        myPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint1.setStyle(Paint.Style.STROKE);
        myPaint1.setColor(getResources().getColor(R.color.bowen0));

        myPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint2.setStyle(Paint.Style.STROKE);
        myPaint2.setColor(getResources().getColor(R.color.bowen0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mcount += 0.5;
        if (mcount >= mwidth * 3 / 10) {
            myPaint.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mcount >= mwidth * 5 / 10) {
            myPaint.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mcount >= mwidth * 7 / 10) {
            myPaint.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mcount >= mwidth * 8 / 10) {
            myPaint.setColor(getResources().getColor(R.color.transprarent));
        }
        if (mcount >= mwidth * 10 / 10) {
            mcount -= mwidth * 10 / 10;
            myPaint.setColor(getResources().getColor(R.color.bowen0));
        }

        mcount0 += 0.5;
        if (mcount0 >= mwidth * 3 / 10) {
            myPaint0.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mcount0 >= mwidth * 5 / 10) {
            myPaint0.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mcount0 >= mwidth * 7 / 10) {
            myPaint0.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mcount0 >= mwidth * 8 / 10) {
            myPaint0.setColor(getResources().getColor(R.color.transprarent));
        }
        if (mcount0 >= mwidth * 10 / 10) {
            mcount0 -= mwidth * 10 / 10;
            myPaint0.setColor(getResources().getColor(R.color.bowen0));
        }

        mcount1 += 0.5;
        if (mcount1 >= mwidth * 3 / 10) {
            myPaint1.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mcount1 >= mwidth * 5 / 10) {
            myPaint1.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mcount1 >= mwidth * 7 / 10) {
            myPaint1.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mcount1 >= mwidth * 8 / 10) {
            myPaint1.setColor(getResources().getColor(R.color.transprarent));
        }
        if (mcount1 >= mwidth * 10 / 10) {
            mcount1 -= mwidth * 10 / 10;
            myPaint1.setColor(getResources().getColor(R.color.bowen0));
        }

        mcount2 += 0.5;
        if (mcount2 >= mwidth * 3 / 10) {
            myPaint2.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mcount2 >= mwidth * 5 / 10) {
            myPaint2.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mcount2 >= mwidth * 7 / 10) {
            myPaint2.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mcount2 >= mwidth * 8 / 10) {
            myPaint2.setColor(getResources().getColor(R.color.transprarent));
        }
        if (mcount2 >= mwidth * 10 / 10) {
            mcount2 -= mwidth * 10 / 10;
            myPaint2.setColor(getResources().getColor(R.color.bowen0));
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                postInvalidate();
            }
        }, 10);

        canvas.drawCircle(MyView.this.getWidth() / 2, MyView.this.getHeight() / 2, mcount, myPaint);
        canvas.drawCircle(MyView.this.getWidth() / 2, MyView.this.getHeight() / 2, mcount0, myPaint0);
        canvas.drawCircle(MyView.this.getWidth() / 2, MyView.this.getHeight() / 2, mcount1, myPaint1);
        canvas.drawCircle(MyView.this.getWidth() / 2, MyView.this.getHeight() / 2, mcount2, myPaint2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //,获取当前控件的布局方式:EXACTLY(精确),ATMOST(最大,wrapcontent),UNSPECIFIED(不指定)
        //获取当前控件的布局模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取控件的具体数值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //更具布局的方式测试控件的大小
        int width;//最后的结果
        int height;//最后的结果

        //如果是精确模式的情况下
        if (widthMode == MeasureSpec.EXACTLY) {
            //martch_parent 或者是具体数值的时候
            width = widthSize;
        } else {//就是wrap_content
            width = 300;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            //martch_parent 或者是具体数值的时候
            height = heightSize;
        } else {//就是wrap_content
            height = 300;
        }
        int min = Math.min(width, height);
        myPaint.setStrokeWidth(min / 100);
        myPaint0.setStrokeWidth(min / 100);
        myPaint1.setStrokeWidth(min / 100);
        myPaint2.setStrokeWidth(min / 100);
        mwidth = width * 3 / 5;
        mcount = mwidth * 1 / 4;
        mcount0 = mwidth * 2 / 4;
        mcount1 = mwidth * 3 / 4;
        mcount2 = mwidth * 4 / 4;
        //设置控件
        //设置当前控件的尺寸
        setMeasuredDimension(width, height);
    }
}
