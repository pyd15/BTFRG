package com.example.btf.viewpage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Dr.P on 2017/12/2.
 * runas /user:Dr.P "cmd /k"
 */

public class GuardViewPager extends ViewPager {
    private boolean mIsSlide = true;//是否可以滑动
    private boolean mRightCanSlide = true;//是否可以右滑动

    private float downX;//落下的x坐标
    private float moveX;//移动的xzuobiao
    private float appartX;//两者相差

    public GuardViewPager(Context context) {
        super(context);
    }

    public GuardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mIsSlide)
            super.scrollTo(x, y);
    }

    //对外暴漏一个开关禁止滑动的方法
    public void toggleSlide(boolean isSlide) {
        this.mIsSlide = isSlide;
    }
//    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mRightCanSlide) {
            return super.dispatchTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = ev.getX();
                    appartX = moveX - downX;
                    if (appartX<0) {
                        return true;
                    }
                    downX = moveX;
                    break;

            }
            return super.dispatchTouchEvent(ev);
        }
    }

    public void toggleRightSlide(boolean rightCanSlide) {
        this.mRightCanSlide = rightCanSlide;
    }
//    */
}
