package com.vice.robotloadinglib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by vice
 * E-mail:vicedev1001@gmail.com
 */

public class RobotLoadingView extends View {
    private int mOutRingColor;
    private int mOutRingGradientStartColor;
    private int mOutRingGradientEndColor;
    private int mInRingColor;
    private int mInRingGradientStartColor;
    private int mInRingGradientEndColor;
    private int mInRingEachWidth;
    private int mInRingEachHeight;
    private int mInRingEachSpacing;
    private int mOutRingWidth;
    private int mCenterTextColor;
    private int mCenterTextSize;

    //wrap_content时的默认大小
    private final int MIN_SIZE = dp2Px(100);

    private Paint mOutRing1Paint;
    private Paint mOutRing2Paint;
    private Paint mOutRing3Paint;
    private Paint mOutRing4Paint;
    private Paint mInRingPaint;

    private int mRadiusSize;
    private int mCenterX;
    private int mCenterY;

    //中心圈的小方块
    private Path mRectPath;
    //中心圈的方块排列路径
    private Path mInRingPath;

    //旋转角度
    private int degress = 0;
    //转的方向
    private int mInRingRotateDirection;
    public static final int INRING_ROTATE_DIRECTION_CW = 1;//顺时针
    public static final int INRING_ROTATE_DIRECTION_CCW = 0;//逆时针
    public static final int INRING_ROTATE_DIRECTION_N = -1;//不转
    //转的单位时间间隔
    private int mInRingRotateInterval;
    //单位时间内转的角度
    private int mInRingRotateIntervalDegree;
    //中心文字
    private String mCenterText;
    private Paint mCenterTextPaint;

    private Rect mCenterTextRect = new Rect();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mInRingRotateDirection == INRING_ROTATE_DIRECTION_CCW) {
                degress -= mInRingRotateIntervalDegree;
            } else {
                degress += mInRingRotateIntervalDegree;
            }
            invalidate();
            postDelayed(this, mInRingRotateInterval);
        }
    };


    public RobotLoadingView(Context context) {
        this(context, null);
    }

    public RobotLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RobotLoadingView);
        mOutRingColor = ta.getColor(R.styleable.RobotLoadingView_outRingColor, Color.WHITE);
        mOutRingGradientStartColor = ta.getColor(R.styleable.RobotLoadingView_outRingGradientStartColor, Color.parseColor("#00ffffff"));
        mOutRingGradientEndColor = ta.getColor(R.styleable.RobotLoadingView_outRingGradientEndColor, Color.parseColor("#a5ffffff"));
        mOutRingWidth = ta.getInt(R.styleable.RobotLoadingView_outRingWidth, dp2Px(15));

        mInRingColor = ta.getColor(R.styleable.RobotLoadingView_inRingColor, Color.WHITE);
        mInRingGradientStartColor = ta.getColor(R.styleable.RobotLoadingView_inRingGradientStartColor, Color.parseColor("#a5ffffff"));
        mInRingGradientEndColor = ta.getColor(R.styleable.RobotLoadingView_inRingGradientEndColor, Color.parseColor("#33ffffff"));
        mInRingEachWidth = ta.getInt(R.styleable.RobotLoadingView_inRingEachWidth, dp2Px(2));
        mInRingEachHeight = ta.getInt(R.styleable.RobotLoadingView_inRingEachHeight, dp2Px(10));
        mInRingEachSpacing = ta.getInt(R.styleable.RobotLoadingView_inRingEachSpacing, dp2Px(8));
        mInRingRotateDirection = ta.getInt(R.styleable.RobotLoadingView_inRingRotateDirection, INRING_ROTATE_DIRECTION_CCW);
        mInRingRotateInterval = ta.getInt(R.styleable.RobotLoadingView_inRingRotateInterval, 5);
        mInRingRotateIntervalDegree = ta.getInt(R.styleable.RobotLoadingView_inRingRotateIntervalDegree, 4);

        mCenterTextColor = ta.getColor(R.styleable.RobotLoadingView_centerTextColor, Color.WHITE);
        mCenterTextSize = ta.getInt(R.styleable.RobotLoadingView_centerTextSize, dp2Px(18));
        mCenterText = ta.getString(R.styleable.RobotLoadingView_centerText);
        ta.recycle();
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {

        //外圈看成两个白圈，两个透明圈，从外到内1:4:6:3
        mOutRing1Paint = new Paint();
        mOutRing1Paint.setColor(mOutRingColor);
        mOutRing1Paint.setAntiAlias(true);
        mOutRing1Paint.setStrokeWidth(mOutRingWidth * 1 / 14);
        mOutRing1Paint.setStyle(Paint.Style.STROKE);

        mOutRing2Paint = new Paint(mOutRing1Paint);
        mOutRing2Paint.setColor(Color.TRANSPARENT);
        mOutRing2Paint.setStrokeWidth(mOutRingWidth * 4 / 14);

        mOutRing3Paint = new Paint(mOutRing1Paint);
        mOutRing3Paint.setStrokeWidth(mOutRingWidth * 6 / 14);

        mOutRing4Paint = new Paint(mOutRing2Paint);
        mOutRing4Paint.setStrokeWidth(mOutRingWidth * 3 / 14);


        mInRingPaint = new Paint();
        mInRingPaint.setColor(mInRingColor);
        mInRingPaint.setAntiAlias(true);

        //中心圈的每一个小方块
        mRectPath = new Path();
        mRectPath.lineTo(0, 0);
        mRectPath.lineTo(mInRingEachWidth, 0);
        mRectPath.lineTo(mInRingEachWidth, mInRingEachHeight);
        mRectPath.lineTo(0, mInRingEachHeight);
        mRectPath.lineTo(0, 0);

        mCenterTextPaint = new Paint();
        mCenterTextPaint.setTextSize(mCenterTextSize);
        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setAntiAlias(true);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
        mCenterTextPaint.setFakeBoldText(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        int minSize = MIN_SIZE;

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minSize, minSize);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(minSize, measureHeight);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidth, minSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int realWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int realHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        mRadiusSize = Math.min(realWidth, realHeight) / 2;
        mCenterX = getPaddingLeft() + realWidth / 2;
        mCenterY = getPaddingTop() + realHeight / 2;


        mOutRing1Paint.setShader(new SweepGradient(mCenterX, mCenterY, new int[]{mOutRingGradientStartColor, mOutRingGradientEndColor}, null));
        mOutRing3Paint.setShader(new SweepGradient(mCenterX, mCenterY, new int[]{mOutRingGradientStartColor, mOutRingGradientEndColor}, null));

        mInRingPath = new Path();
        mInRingPath.addCircle(mCenterX, mCenterY, mRadiusSize - mOutRing1Paint.getStrokeWidth() - mOutRing2Paint.getStrokeWidth() - mOutRing3Paint.getStrokeWidth() - mOutRing4Paint.getStrokeWidth(), Path.Direction.CW);
        PathEffect pathEffect = new PathDashPathEffect(mRectPath, mInRingEachSpacing, 0, PathDashPathEffect.Style.ROTATE);
        mInRingPaint.setPathEffect(pathEffect);
        mInRingPaint.setShader(new SweepGradient(mCenterX, mCenterY, new int[]{mInRingGradientStartColor, mInRingGradientEndColor}, null));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画外部圈
        canvas.save();
        canvas.rotate(-degress, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mRadiusSize - mOutRing1Paint.getStrokeWidth() - mOutRing2Paint.getStrokeWidth() - mOutRing3Paint.getStrokeWidth() / 2, mOutRing3Paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-degress + 15, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, mRadiusSize - mOutRing1Paint.getStrokeWidth() / 2, mOutRing1Paint);
        canvas.drawCircle(mCenterX, mCenterY, mRadiusSize - mOutRing1Paint.getStrokeWidth() - mOutRing2Paint.getStrokeWidth() / 2, mOutRing2Paint);
        canvas.drawCircle(mCenterX, mCenterY, mRadiusSize - mOutRing1Paint.getStrokeWidth() - mOutRing2Paint.getStrokeWidth() - mOutRing3Paint.getStrokeWidth() - mOutRing4Paint.getStrokeWidth() / 2, mOutRing4Paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(degress, mCenterX, mCenterY);
        //画内部圈
        canvas.drawPath(mInRingPath, mInRingPaint);
        canvas.restore();
        //画文字
        if (!TextUtils.isEmpty(mCenterText)) {
            mCenterTextPaint.getTextBounds(mCenterText, 0, mCenterText.length(), mCenterTextRect);
            canvas.drawText(mCenterText, mCenterX, mCenterY + mCenterTextRect.height() / 2, mCenterTextPaint);
        }
    }

    public void setCenterText(String centerText) {
        mCenterText = centerText;
        invalidate();
    }

    private void startRotate() {
        if (mInRingRotateDirection != INRING_ROTATE_DIRECTION_N) {
            //开始转
            postDelayed(mRunnable, mInRingRotateInterval);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //中心圈开始转
        startRotate();
    }

    private int dp2Px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int px2Dp(float px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
