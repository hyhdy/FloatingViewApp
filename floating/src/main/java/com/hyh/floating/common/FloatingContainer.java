package com.hyh.floating.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * created by curdyhuang on 2019-11-11
 * 悬浮窗容器，负责监听滑动事件并且回调
 */
public class FloatingContainer extends FrameLayout {
    private int mTouchSlop;
    private int mInitMotionX,mInitMotionY,mLastMotionX,mLastMotionY;
    private OnFloatingListener mOnFloatingListener;
    private int mWidgetW;
    private int mWidgetH;

    public FloatingContainer(@NonNull Context context) {
        this(context,null);
    }

    public FloatingContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获得touchslop
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(oldw!=w||oldh!=h){
            mWidgetW = w;
            mWidgetH = h;
            if(mOnFloatingListener !=null){
                mOnFloatingListener.onSizeChanged(w,h);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitMotionX = mLastMotionX = (int) ev.getRawX();
                mInitMotionY = mLastMotionY =  (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int absDx = Math.abs(x - mInitMotionX);
                int absDy = Math.abs(y - mInitMotionY);
                if (absDx > mTouchSlop/4 || absDy > mTouchSlop/4) {
                    //拦截滑动事件
                    intercept = true;
                }
                break;
            default:
        }
        return intercept||super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{

            }
                break;
            case MotionEvent.ACTION_MOVE: {
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - mLastMotionX;
                int movedY = nowY - mLastMotionY;
                mLastMotionX = nowX;
                mLastMotionY = nowY;
                if (mOnFloatingListener != null) {
                    mOnFloatingListener.onMove(movedX, movedY, mWidgetW, mWidgetH);
                }
            }
                break;
            case MotionEvent.ACTION_UP:{

            }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnFloatingListener(OnFloatingListener onFloatingListener) {
        mOnFloatingListener = onFloatingListener;
    }

    public interface OnFloatingListener {
        /**
         * 移动悬浮窗
         * @param movedX x轴移动的像素
         * @param movedY y轴移动的像素
         * @param widgetW 悬浮窗宽度
         * @param widgetH 悬浮窗高度
         */
        void onMove(int movedX, int movedY, int widgetW, int widgetH);

        /**
         * 悬浮窗尺寸更新
         * @param widgetW 悬浮窗宽度
         * @param widgetH 悬浮窗高度
         */
        void onSizeChanged(int widgetW, int widgetH);
    }
}
