package com.xyw.smartlock.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xyw.smartlock.R;


public class WritePadDialog extends Dialog {

    private Context context;
    private LayoutParams p;// 布局属性
    private DialogListener dialogListener;// 自定义的一个接口 只有refreshActivity(Object
    // object)方法，在handWritingActivity类中有具体的实现

    public WritePadDialog(Context context, DialogListener dialogListener) {
        super(context);
        this.context = context;
        this.dialogListener = dialogListener;
    }

    private PaintView mView;//自定义view 对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.write_pad);

        p = getWindow().getAttributes(); // 获取对话框当前的参数值

        WindowManager a = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d1 = a.getDefaultDisplay(); // 获取屏幕宽、高用
        Point size = new Point();
        d1.getSize(size);

        p.height = size.y * 3 / 4;// 获取手机屏幕的高
        p.width = size.x;//获取手机屏幕的宽
        getWindow().setAttributes(p); // 设置生效

        mView = new PaintView(context);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
        frameLayout.addView(mView);
        mView.requestFocus();//为当前view 设置焦点
        Button btnClear = (Button) findViewById(R.id.tablet_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mView.clear();
            }
        });

        Button btnOk = (Button) findViewById(R.id.tablet_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dialogListener.refreshActivity(mView.getCachebBitmap());
                    WritePadDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnCancel = (Button) findViewById(R.id.tablet_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    /**
     * This view implements the drawing canvas.
     * <p>
     * It handles all of the input events and drawing functions.
     */
    class PaintView extends View {
        private Paint paint;
        private Canvas cacheCanvas;
        private Bitmap cachebBitmap;
        private Path path;

        public Bitmap getCachebBitmap() {
            return cachebBitmap;
        }

        public PaintView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            path = new Path();
            cachebBitmap = Bitmap.createBitmap(p.width , p.height , Config.ARGB_8888);
            cacheCanvas = new Canvas(cachebBitmap);
            cacheCanvas.drawColor(Color.WHITE);
        }

        public void clear() {
            if (cacheCanvas != null) {
                // 清除canvas上所有内容
                cacheCanvas.drawPaint(paint);

                paint.setColor(Color.BLACK);
                cacheCanvas.drawColor(Color.WHITE);
                // init();
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // canvas.drawColor(BRUSH_COLOR);
            canvas.drawBitmap(cachebBitmap, 0, 0, null);
            canvas.drawPath(path, paint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            //
            // int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
            // int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
            // if (curW >= w && curH >= h) {
            // return;
            // }
            //
            // if (curW < w)
            // curW = w;
            // if (curH < h)
            // curH = h;
            //
            // Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
            // Bitmap.Config.ARGB_8888);
            // Canvas newCanvas = new Canvas();
            // newCanvas.setBitmap(newBitmap);
            // if (cachebBitmap != null) {
            // newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
            // }
            // cachebBitmap = newBitmap;
            // cacheCanvas = newCanvas;
        }

        private float cur_x, cur_y;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    System.out.println("down");
                    cur_x = x;
                    cur_y = y;
                    path.moveTo(cur_x, cur_y);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    System.out.println("move");
                    path.quadTo(cur_x, cur_y, x, y);
                    cur_x = x;
                    cur_y = y;
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    System.out.println("up");
                    cacheCanvas.drawPath(path, paint);
                    path.reset();
                    break;
                }
            }

            invalidate();

            return true;
        }
    }

}
