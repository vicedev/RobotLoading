package com.vice.robotloadinglib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by vice
 * E-mail:vicedev1001@gmail.com
 */

public class RobotLoadingLayout extends LinearLayout {


    private RobotLoadingView mRobotLoadingView;
    private TextView mTvBottomText;

    private String bottomText;

    private StringBuilder mSb;

    private static final int POST_TIME = 50;

    private int visibleLength = 0;//显示字的个数
    private int percent = 0;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (percent % 4 == 0) {
                visibleLength++;
                drawBottomText();
            }
            percent++;
            drawPercent();
            postDelayed(this, POST_TIME);
        }
    };
    private CloseListener mListener;

    private void drawBottomText() {
        if (visibleLength > mSb.length()) {
            visibleLength = visibleLength - 3;
        }
        mSb.replace(0, mSb.length() - 1, bottomText);
        for (int i = visibleLength - 1; i < mSb.length(); i++) {
            mSb.setCharAt(i, ' ');
        }
        mTvBottomText.setText(mSb.toString());
    }

    public RobotLoadingLayout(Context context) {
        super(context);
        init();
    }

    public RobotLoadingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bottomText = getContext().getString(R.string.robot_calculating);
        mSb = new StringBuilder(bottomText);
    }

    /**
     * 设置底部文字
     */
    public void setBottomText(String text) {
        bottomText = text;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRobotLoadingView = (RobotLoadingView) findViewById(R.id.robot_loading);
        mTvBottomText = (TextView) findViewById(R.id.tv_bottom_text);
    }

    private void drawPercent() {
        if (percent > 99) {
            mRobotLoadingView.setCenterText(99 + "%");
            if (mListener != null) {
                mListener.close();
                mListener = null;
            }
        } else {
            mRobotLoadingView.setCenterText(percent + "%");
        }
    }

    public void close(CloseListener listener) {
        mListener = listener;
    }

    public void startLoading() {
        postDelayed(mRunnable, POST_TIME);
    }

    public interface CloseListener {
        void close();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mRunnable);
        super.onDetachedFromWindow();
    }
}
