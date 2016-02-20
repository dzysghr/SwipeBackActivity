package com.dzy.swipeback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 右滑返回功能的activity
 *
 * 注：使用此activity后，上一层activity的onstop方法不会被调用
 * Created by dzysg on 2015/10/4 0004.
 */
public abstract class SwipeBackBaseActivity extends AppCompatActivity
{


    SwipeBackLayout mSwipeBackLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID)
    {
        mSwipeBackLayout = new SwipeBackLayout(this,layoutResID);
        super.setContentView(mSwipeBackLayout);
    }

    @Override
    public void setContentView(View view)
    {
        mSwipeBackLayout = new SwipeBackLayout(this,view);
        super.setContentView(mSwipeBackLayout);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(-1,-1);
    }

}
