package com.hyh.floatingviewapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by curdyhuang on 2019-11-11
 */
public class CustomRecyclerView extends RecyclerView {
    private float mInitMotionX;
    private float mInitMotionY;

    public CustomRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getRawX();
        float y = ev.getRawY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mInitMotionX = x;
                mInitMotionY = y;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            break;
            case MotionEvent.ACTION_MOVE:{
                //处理横向冲突
                float absX = Math.abs(x - mInitMotionX);
                float absY = Math.abs(y - mInitMotionY);
                if (absX > absY && absX / absY > 2) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
