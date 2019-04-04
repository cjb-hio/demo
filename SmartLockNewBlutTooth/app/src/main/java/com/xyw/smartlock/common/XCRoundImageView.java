package com.xyw.smartlock.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * * 自定义的圆形ImageView，可以直接当组件在布局中使用。
 *
 * @author caizhiming
 */
public class XCRoundImageView extends ImageView {

    public XCRoundImageView(Context context) {
        this(context, null);
    }

    public XCRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XCRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 绘制圆形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (null == b) {
            return;
        }

        Bitmap bitmap = b.copy(Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        int radius = w > h ? h : w;
        //比较设置属性的宽高,对值小的进行裁剪获取圆形图片
        Bitmap roundBitmap = getCroppedBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, 0, 0, null);

//        rotateBitmap(canvas, roundBitmap);
    }

    /**
     * 旋转图片
     * @param canvas
     * @param roundBitmap
     */
    private void rotateBitmap(Canvas canvas, Bitmap roundBitmap) {
        Matrix matrix = new Matrix();
        matrix.postTranslate(-roundBitmap.getWidth() / 2, -roundBitmap.getHeight() / 2);
        matrix.postRotate(1 * times);
        matrix.postTranslate(this.getWidth() / 2, this.getHeight() / 2); // 自定义view的中心点旋转

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                times++;
                postInvalidate();
                if (times == 360) {
                    times = 0;
                }
            }
        }, 10);

        canvas.drawBitmap(roundBitmap, matrix, null);
    }


    /**
     * 获取圆形图片方法
     *
     * @param bmp
     * @param radius
     * @return
     */
    private Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        //设置是否抗锯齿
        paint.setAntiAlias(true);
        //用来对位图进行滤波处理
        paint.setFilterBitmap(true);
        //设置是否防抖动
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(Color.parseColor("#BAB399"));
        paint.setColor(color);
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        return output;
    }

    private int times = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}