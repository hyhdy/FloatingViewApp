package com.hyh.floatingviewapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by curdyhuang on 2019-11-11
 */
public class CustomRecyclerView extends RecyclerView {
    private float mInitMotionX;
    private float mInitMotionY;
    private boolean mTriggerMove;
    private int mTouchSlop;

    public CustomRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获得touchslop
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getRawX();
        float y = ev.getRawY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mTriggerMove = false;
                mInitMotionX = x;
                mInitMotionY = y;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            break;
            case MotionEvent.ACTION_MOVE:{
                //处理横向冲突
                float absX = Math.abs(x - mInitMotionX);
                float absY = Math.abs(y - mInitMotionY);
                if(!mTriggerMove) {
                    if (absX >= mTouchSlop || absY >= mTouchSlop) {
                        if ((absX > absY)||!canScrollVertically(-1)||!canScrollVertically(1)) {
                            //横向滑动或不可以上下滑动时，把事件交给父控件拦截
                            Log.d("hyh", "CustomRecyclerView: dispatchTouchEvent: horizontal");
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        mTriggerMove = true;
                    }
                }
            }
            break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
