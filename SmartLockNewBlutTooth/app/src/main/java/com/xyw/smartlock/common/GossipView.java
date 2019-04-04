package com.xyw.smartlock.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xyw.smartlock.R;
import com.xyw.smartlock.utils.MyUtils;

import java.util.List;

public class GossipView extends View {
    private int mNumber;

    public class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public interface OnPieceClickListener {
        void onPieceClick(int whitchPiece);
    }

    private static final String TAG = "com.jcodecraeer.gossipview";
    private RectF mOuterArcRectangle = new RectF();
    private RectF mInnerArcRectangle = new RectF();
    private float mOuterArcRadius;
    private float mInnerArcRadius;
    private Paint mOuterArcPaint;
    private Paint mInnerArcPaint;
    private Paint mOuterTextPaint;
    private Paint mNumberTextPaint;
    private Paint mProgressPaint;
    private Paint dzPaint;
    private Paint dzPaint0;
    private Paint dzPaint1;
    private Paint dzPaint2;
    private float outArctrokeWidth;
    private float mInnerArctrokeWidth;
    private int mPieceNumber = 6;
    private int mPieceDegree = 360 / mPieceNumber;
    private int mDividerDegree = 5;
    private int mWidth;
    private Drawable mInnerBackGroud;
    private Drawable mOuterBackGroud;
    private int mSelectIndex = -2;
    // private int mNumber;
    private int[] outArcColor = {0xff0597d2, 0xff3f8073, 0xffcc324b,
            0xff1a4e95, 0xff3f8073, 0xffe55f3a};
    private Context mContext;
    private SweepGradient mSweepGradient;
    private int overTouchDistance = MyUtils.dip2px(getContext(), 0); // 扩展距离，
    // 增加外围button的点击效果
    // private
    // int
    // progressAnimateStartAngle
    // = 0;
    // //
    // 用于动画
    private int padding = MyUtils.dip2px(getContext(), 0);

    private List<GossipItem> items;
    private static int HOME_NUMBER_TEXT_SIZE = 25;
    private static float mScale = 0; // Used for supporting different screen
    // densities
    private OnPieceClickListener mListener;
    /**
     * mInnerArcPaintAngle 画解锁中间圆弧
     */
    private float mInnerArcPaintAngle;
    private float mInnerArcPaintAngle0;
    private float mInnerArcPaintAngle1;
    //private float mInnerArcPaintAngle2= mInnerArcPaintAngle-outArctrokeWidth*9/10;

    private WindowManager windowManager;
    private Window window;

    public GossipView(Context context) {
        super(context);
        init(context, null, 0);

    }

    public GossipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GossipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context c, AttributeSet attrs, int defStyle) {
        mContext = c;

        if (mScale == 0) {
            mScale = getContext().getResources().getDisplayMetrics().density;
            if (mScale != 1) {
                HOME_NUMBER_TEXT_SIZE *= mScale;
            }
        }

        mOuterArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterArcPaint.setStyle(Paint.Style.STROKE);

        mInnerArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerArcPaint.setStyle(Paint.Style.STROKE);
        mInnerArcPaint.setColor(0xfff39700);

        dzPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dzPaint.setStyle(Paint.Style.STROKE);
        dzPaint.setColor(getResources().getColor(R.color.bowen0));

        dzPaint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dzPaint0.setStyle(Paint.Style.STROKE);
        dzPaint0.setColor(getResources().getColor(R.color.bowen0));

        dzPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dzPaint1.setStyle(Paint.Style.STROKE);
        dzPaint1.setColor(getResources().getColor(R.color.bowen0));


        mOuterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterTextPaint.setColor(0xffffffff);
        mOuterTextPaint.setTextSize(outArctrokeWidth / 8);
        mOuterTextPaint.setAntiAlias(true);

        mNumberTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumberTextPaint.setColor(0xff076291);
        mNumberTextPaint.setTextSize(HOME_NUMBER_TEXT_SIZE);
        mInnerBackGroud = mContext.getResources().getDrawable(
                R.drawable.home_score_bg_selector);
        mOuterBackGroud = mContext.getResources().getDrawable(
                R.mipmap.home_view_bg);
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mOuterBackGroud.draw(canvas);
        for (int i = 0; i < mPieceNumber; i++) {
            drawArc(i, canvas);
        }
        if (mSelectIndex == -1) {
            mInnerBackGroud.setState(PRESSED_FOCUSED_STATE_SET);
        } else {
            mInnerBackGroud.setState(EMPTY_STATE_SET);
        }
        mInnerBackGroud.draw(canvas);

//***********************************************************************************************//
        // 绘制内圆底色

        mInnerArcPaint.setColor(0xFFFF0000);
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
				mInnerArcPaintAngle += 3;
				if (mInnerArcPaintAngle >= 363) {
					try {
						mInnerArcPaintAngle -= 360;
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				postInvalidate();
			}
		}, 10);*/
        mInnerArcPaintAngle += 0.5;
        if (mInnerArcPaintAngle >= outArctrokeWidth * 3 / 10) {
            dzPaint.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mInnerArcPaintAngle >= outArctrokeWidth * 6 / 10) {
            dzPaint.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mInnerArcPaintAngle >= outArctrokeWidth * 8 / 10) {
            dzPaint.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mInnerArcPaintAngle >= outArctrokeWidth * 9 / 10) {
            mInnerArcPaintAngle -= outArctrokeWidth * 9 / 10;
            dzPaint.setColor(getResources().getColor(R.color.bowen0));
        }

        mInnerArcPaintAngle0 += 0.5;
        if (mInnerArcPaintAngle0 >= outArctrokeWidth * 3 / 10) {
            dzPaint0.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mInnerArcPaintAngle0 >= outArctrokeWidth * 6 / 10) {
            dzPaint0.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mInnerArcPaintAngle0 >= outArctrokeWidth * 8 / 10) {
            dzPaint0.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mInnerArcPaintAngle0 >= outArctrokeWidth * 9 / 10) {
            mInnerArcPaintAngle0 -= outArctrokeWidth * 9 / 10;
            dzPaint0.setColor(getResources().getColor(R.color.bowen0));
        }

        mInnerArcPaintAngle1 += 0.5;
        if (mInnerArcPaintAngle1 >= outArctrokeWidth * 3 / 10) {
            dzPaint1.setColor(getResources().getColor(R.color.bowen1));
        }
        if (mInnerArcPaintAngle1 >= outArctrokeWidth * 6 / 10) {
            dzPaint1.setColor(getResources().getColor(R.color.bowen2));
        }
        if (mInnerArcPaintAngle1 >= outArctrokeWidth * 8 / 10) {
            dzPaint1.setColor(getResources().getColor(R.color.bowen3));
        }
        if (mInnerArcPaintAngle1 >= outArctrokeWidth * 9 / 10) {
            mInnerArcPaintAngle1 -= outArctrokeWidth * 9 / 10;
            dzPaint1.setColor(getResources().getColor(R.color.bowen0));
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                postInvalidate();
            }
        }, 10);

//		canvas.drawArc(mInnerArcRectangle, 0, mInnerArcPaintAngle, false,
//				mInnerArcPaint);
        canvas.drawCircle(GossipView.this.getWidth() / 2, GossipView.this.getHeight() / 2, mInnerArcPaintAngle, dzPaint);
        canvas.drawCircle(GossipView.this.getWidth() / 2, GossipView.this.getHeight() / 2, mInnerArcPaintAngle0, dzPaint0);
        canvas.drawCircle(GossipView.this.getWidth() / 2, GossipView.this.getHeight() / 2, mInnerArcPaintAngle1, dzPaint1);
        //canvas.drawCircle(GossipView.this.getWidth()/2, GossipView.this.getHeight()/2, mInnerArcPaintAngle2, dzPaint);


//***********************************************************************************************//
        // if(mNumber == 0){
        // canvas.save();
        // canvas.rotate(progressAnimateStartAngle, getOriginal().x,
        // getOriginal().y);
        // canvas.drawArc(mInnerArcRectangle, 0 , 360, false, mProgressPaint);
        // canvas.restore();
        // }else{
        // mInnerArcPaint.setColor(0xfff39700);
        // canvas.drawArc(mInnerArcRectangle, -90, (360*mNumber/9000), false,
        // mInnerArcPaint);
        // }
        //
        // Rect rect = new Rect();
        // mNumberTextPaint.getTextBounds(mNumber + "", 0, (mNumber +
        // "").length(), rect);
        // int txWidth = rect.width();
        // int txHeight = rect.height();
        // canvas.drawText(mNumber + "", getOriginal().x - txWidth/2,
        // getOriginal().y + txHeight/2, mNumberTextPaint);
    }

    /**
     * 按索引值绘制扇区，第一个扇区的中心位于3点钟方向
     */
    public void drawArc(int index, Canvas canvas) {
        //绘制圆弧起始角度
        int startdegree = mPieceDegree * (index)
                - (mPieceDegree - mDividerDegree) / 2;
        if (index == mSelectIndex) {
            mOuterArcPaint.setColor(0xFFcacccc);
        } else {
            mOuterArcPaint.setColor(outArcColor[index]);
        }
        //弧形中心点的位置
        float radious = ((float) mWidth - (float) outArctrokeWidth) / 2 - padding;
        //绘制角度结束位置角度
        float midDegree = startdegree + (mPieceDegree - mDividerDegree) / 2;
        //获取结束点到中心位置（圆心）x, y 的距离
        double x = radious * Math.cos(midDegree * Math.PI / 180);
        double y = radious * Math.sin(midDegree * Math.PI / 180);
        x = x + getOriginal().x;
        y = y + getOriginal().y;
        //绘制圆弧
        canvas.drawArc(mOuterArcRectangle, startdegree, mPieceDegree
                - mDividerDegree, false, mOuterArcPaint);
        //获取文字的 宽高
        Rect rect = new Rect();
        mOuterTextPaint.getTextBounds(items.get(index).getTitle(), 0, items
                .get(index).getTitle().length(), rect);
        int txWidth = rect.width();
        int txHeight = rect.height();
        //绘制文字
        canvas.drawText(items.get(index).getTitle(), (int) x - txWidth / 2,
                (int) y + txHeight / 2, mOuterTextPaint);
    }

    /**
     * 根据触摸坐标获取扇区的索引
     */
    public int getTouchArea(Point p) {
        int index = -2;
        float absdy = Math.abs(p.y - getOriginal().y);
        float absdx = Math.abs(p.x - getOriginal().x);
        //判断是否在属于圆心空心部分
        if (absdx * absdx + absdy * absdy < ((float) mWidth / 2
                - outArctrokeWidth - overTouchDistance - padding)
                * ((float) mWidth / 2 - outArctrokeWidth - overTouchDistance - padding)) {
            return -1;
        }
        //获取 不在圆心位置的弧度值
        double dx = Math.atan2(p.y - getOriginal().y, p.x - getOriginal().x);
        //将弧度值转为角度 x=180*x/Math.PI//转换为角度值
        float fDegree = (float) (dx / (2 * Math.PI) * 360);
        fDegree = (fDegree + 360) % 360;
        if (fDegree > (360 - (mPieceDegree - mDividerDegree) / 2) && fDegree < 360) {
            fDegree -= 360;
        }
        int start = -(mPieceDegree - mDividerDegree) / 2;
        for (int i = 0; i < mPieceNumber; i++) {
            int end = start + mPieceDegree - mDividerDegree;
            if (start < fDegree && fDegree < end) {
                //求触摸点到中心点的距离， Math.sqrt() 求直角三角形斜边，也就是触摸点到中心的距离，不大于半径
                if (!(Math.sqrt(absdx * absdx + absdy * absdy) > mWidth / 2)) {
                    index = i;
                }
            }
            start = mPieceDegree * (i + 1) - (mPieceDegree - mDividerDegree) / 2;
        }
        return index;
    }

    public Point getOriginal() {
        return new Point((float) mWidth / 2, (float) mWidth / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        mWidth = min;
        outArctrokeWidth = min / 4;
        mInnerArctrokeWidth = outArctrokeWidth / 7;
        mInnerArcPaint.setStrokeWidth(mInnerArctrokeWidth);
        dzPaint.setStrokeWidth(mInnerArctrokeWidth * 1 / 4);
        dzPaint0.setStrokeWidth(mInnerArctrokeWidth * 1 / 4);
        dzPaint1.setStrokeWidth(mInnerArctrokeWidth * 1 / 4);
        mOuterTextPaint.setTextSize(outArctrokeWidth / 5);
        mOuterArcPaint.setStrokeWidth(outArctrokeWidth);

        mOuterArcRadius = mWidth - outArctrokeWidth / 2 - padding;
        mInnerArcRadius = mWidth / 6;

        mProgressPaint.setStrokeWidth(mInnerArctrokeWidth);
        mSweepGradient = new SweepGradient(getOriginal().x, getOriginal().y,
                0xfff39700, Color.WHITE);
        mProgressPaint.setShader(mSweepGradient);

        mOuterArcRectangle.set(outArctrokeWidth / 2 + padding, outArctrokeWidth
                / 2 + padding, mOuterArcRadius, mOuterArcRadius);
        mInnerArcRectangle.set(mWidth / 2 - mInnerArcRadius, mWidth / 2
                - mInnerArcRadius, mWidth / 2 + mInnerArcRadius, mWidth / 2
                + mInnerArcRadius);
        mInnerBackGroud.setBounds((int) outArctrokeWidth + padding,
                (int) outArctrokeWidth + padding,
                (int) (min - outArctrokeWidth - padding), (int) (min
                        - outArctrokeWidth - padding));
        mOuterBackGroud.setBounds(0, 0, mWidth, mWidth);
        setMeasuredDimension(min, min);
        mInnerArcPaintAngle = outArctrokeWidth * 3 / 10;
        mInnerArcPaintAngle0 = outArctrokeWidth * 6 / 10;
        mInnerArcPaintAngle1 = outArctrokeWidth * 9 / 10;

    }

    private int upIndex;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mSelectIndex = getTouchArea(new Point(event.getX(), event.getY()));
            this.invalidate();
            // mSelectIndex = -1;
        } else if (event.getAction() == MotionEvent.ACTION_UP
                && event.getAction() != MotionEvent.ACTION_CANCEL) {
            upIndex = getTouchArea(new Point(event.getX(), event.getY()));
            if (mListener != null) {
                mListener.onPieceClick(upIndex);
            }
            mSelectIndex = -2;
            this.invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            mSelectIndex = -2;
            this.invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mSelectIndex = getTouchArea(new Point(event.getX(), event.getY()));
            this.invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            mSelectIndex = -2;
            upIndex = -2;
            this.invalidate();
        }
        return true;
    }

    /**
     * 设置圆圈中的数字
     */
    public void setNumber(int number) {
        mNumber = number;
        this.invalidate();
    }

    /**
     * 获取圆圈中的数字
     */
    public int getNumber() {
        return mNumber;
    }

    /**
     * 设置动画的起始值
     */
    // public void setProgressAnimateStartAngle(int startAngle) {
    // progressAnimateStartAngle = startAngle;
    // this.invalidate();
    // }
    //
    // public int getProgressAnimateStartAngle() {
    // return progressAnimateStartAngle;
    // }
    public void setItems(List<GossipItem> items1) {
        this.items = items1;
        mPieceNumber = items.size();
        mPieceDegree = 360 / mPieceNumber;
    }

    /**
     * 设置点击事件
     */
    public void setOnPieceClickListener(OnPieceClickListener l) {
        mListener = l;
    }

}
