package com.dzy.swipeback;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 *
 * Created by dzysg on 2015/10/4 0004.
 */
public class SwipeBackLayout extends HorizontalScrollView {

    private AppCompatActivity mActivity;
    private LinearLayout mWrapper;
    private ViewGroup mEmptyLayout;
    private ViewGroup mContent;
    private View mCustomView;
    private int mScreenWidth;
    private ScrollListener mScrollListener;
    private VelocityTracker mTracker;
    private int mPointerId;
    private int mMaxVelocity;
    private float mVelocityX;
    private float X;
    private boolean mShouldScroll = false;
    private int mDuration = 400;


    public SwipeBackLayout(Context context)
    {
        super(context);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SwipeBackLayout(Context context, View view)
    {
        super(context);
        mCustomView = view;
        initLayout(-1);
    }


    public SwipeBackLayout(Context context, int contentid) {
        super(context);
        initLayout(contentid);

    }


    private  void initLayout(int contentid)
    {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();

        LayoutInflater.from(getContext()).inflate(R.layout.swipeback_layout, this);
        //根布局
        mWrapper = (LinearLayout) findViewById(R.id.wrapper);

        //左则空白布局
        mEmptyLayout = (ViewGroup) findViewById(R.id.emptyLayout);

        //右则内容容器
        mContent = (ViewGroup) findViewById(R.id.content);

        //内容布局
        if (contentid>0)
        mCustomView = View.inflate(getContext(), contentid, null);

        mContent.addView(mCustomView);

        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        this.setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWrapper.getLayoutParams().width = 2 * mScreenWidth;
        mEmptyLayout.getLayoutParams().width = mScreenWidth;
        mContent.getLayoutParams().width = mScreenWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.scrollTo(mScreenWidth, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (l == 0)
            ((Activity)getContext()).finish();

        //根据滑动的进度设置背景透明度
        float width = (float) mScreenWidth;
        float alpha = l / width;
        mEmptyLayout.setAlpha(alpha * 0.6f);


        if (mScrollListener != null)
            this.mScrollListener.onScroll(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        acquireVelocityTracker(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                X = ev.getX();
                mPointerId = ev.getPointerId(0);
                if (!mShouldScroll)
                    return false;
                break;
            case MotionEvent.ACTION_MOVE:

                if (!mShouldScroll)
                    return false;
                mTracker.computeCurrentVelocity(1000, mMaxVelocity);
                mVelocityX = mTracker.getXVelocity(mPointerId);
                break;

            case MotionEvent.ACTION_UP:

                if (this.getScrollX() < mScreenWidth / 2 || mVelocityX > 2000) {

                    this.smoothScrollTo(0,0);
                } else
                    this.smoothScrollTo(mScreenWidth,0);
                releaseVelocityTracker();
                return true;
        }
        return super.onTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        acquireVelocityTracker(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                X = ev.getX();
                mShouldScroll = ev.getX() < mScreenWidth / 6;
                boolean re = super.onInterceptTouchEvent(ev);
                return re;
            case MotionEvent.ACTION_MOVE:
                return mShouldScroll;


            case MotionEvent.ACTION_UP:
                boolean re3 = super.onInterceptTouchEvent(ev);
                Log.i("vo", "Intercept MOVE  re    " + re3);
                return re3;
        }

        return super.onInterceptTouchEvent(ev);
    }



    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mTracker) {
            mTracker = VelocityTracker.obtain();
        }
        mTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mTracker) {
            mTracker.clear();
            mTracker.recycle();
            mTracker = null;
        }
    }


    private void close(boolean b) {
        int x = 0;
        if (!b)
            x = mScreenWidth;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "scrollX", x);
        animator.setDuration(mDuration);
        animator.start();
    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX);
    }

    public void setScrollListener(ScrollListener l) {
        this.mScrollListener = l;
    }

    public interface ScrollListener {
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
