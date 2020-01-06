package com.hyh.floatingviewapp.floating.common;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.hyh.floatingviewapp.helper.FloatPermission;

import static android.content.Context.WINDOW_SERVICE;

/**
 * created by curdyhuang on 2019/11/22
 * 悬浮窗管理类，处理悬浮窗拖拽滑动，显隐等事件
 */
public class FloatingWindowManager {
    private WindowManager mWindowManager;
    private Point mWindowSize;
    private WindowManager.LayoutParams mLayoutParams;
    private FloatingContainer mFloatingContainer;
    private int mWidgetW,mWidgetH;
    private int mOffsetX,mOffsetY;
    private Context mContext;
    private CallBack mCallBack;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private class AnimMoveRunnable implements Runnable {
        private long mStartTime;
        private long mDuration = 200;
        private int mOffsetX;
        private int mOffsetY;
        private int mStartX;
        private int mStartY;

        public AnimMoveRunnable(int offsetX,int offsetY) {
            mStartTime = System.currentTimeMillis();
            mOffsetX = offsetX;
            mOffsetY = offsetY;
            mStartX = mLayoutParams.x;
            mStartY = mLayoutParams.y;
        }

        @Override
        public void run() {
            float t = intercept();
            mLayoutParams.x = (int) (mStartX + t * mOffsetX);
            mLayoutParams.y = (int) (mStartY + t * mOffsetY);
            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
            if(t < 1f){
                mFloatingContainer.post(this);
            }
        }

        private float intercept(){
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mDuration;
            t = Math.min(1f,t);
            t = mInterpolator.getInterpolation(t);
            return t;
        }
    }

    public FloatingWindowManager(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mWindowSize = new Point();
        mWindowManager.getDefaultDisplay().getSize(mWindowSize);
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT| Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.x = 100;
        mLayoutParams.y = 100;
    }

    /**
     * 添加悬浮控件，添加到悬浮窗容器里
     */
    public void addFloatingView(View view){
        if(FloatPermission.checkPermission(mContext)){
            if (mFloatingContainer == null) {
                mFloatingContainer = new FloatingContainer(mContext);
                mFloatingContainer.setOnFloatingListener(new FloatingContainer.OnFloatingListener() {
                    @Override
                    public void onMove(int movedX, int movedY,int widgetW,int widgetH) {
                        //滑动更新悬浮窗位置
                        int newX = mLayoutParams.x + movedX;
                        int newY = mLayoutParams.y + movedY;
                        if(newX < 0 || newY < 0 || newX > mWindowSize.x-widgetW || newY > mWindowSize.y-widgetH){
                            //处理边界问题
                            return;
                        }

                        mLayoutParams.x = newX;
                        mLayoutParams.y = newY;
                        mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                    }

                    @Override
                    public void onSizeChanged(int widgetW, int widgetH) {
                        if(mWidgetW==widgetW&&mWidgetH==widgetH){
                            //悬浮窗尺寸没变化不更新位置
                            return;
                        }
                        Log.d("hyh","FloatingWindowManager: onSizeChanged: widthtW="+widgetW+" ,widgetH="+widgetH);
                        mWidgetW = widgetW;
                        mWidgetH = widgetH;
                        //悬浮窗尺寸改变需要更新悬浮窗位置
                        if(mLayoutParams.x > mWindowSize.x-widgetW || mLayoutParams.y > mWindowSize.y-widgetH) {
                            if (mLayoutParams.x > mWindowSize.x - widgetW) {
                                int newX = mWindowSize.x - widgetW;
                                //记下位置偏移量，用于恢复位置
                                mOffsetX = mLayoutParams.x - newX;
                                Log.d("hyh", "FloatingWindowManager: onSizeChanged: mOffsetX=" + mOffsetX);
                                //处理边界问题
                                mLayoutParams.x = newX;
                            }
                            if (mLayoutParams.y > mWindowSize.y - widgetH) {
                                int newY = mWindowSize.y - widgetH;
                                //记下位置偏移量，用于恢复位置
                                mOffsetY = mLayoutParams.y - newY;
                                Log.d("hyh", "FloatingWindowManager: onSizeChanged: mOffsetY=" + mOffsetY);
                                //处理边界问题
                                mLayoutParams.y = newY;
                            }
                            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                        }else if(mOffsetX!=0||mOffsetY!=0){
                            if(mCallBack!=null&&mCallBack.revocerenable()){
                                //恢复悬浮窗位置，这里做了个动画移动，不然会有明显的闪烁，体验不好
                                mFloatingContainer.post(new AnimMoveRunnable(mOffsetX,mOffsetY));
                                mOffsetX = 0;
                                mOffsetY = 0;
                            }
                        }
                    }
                });
                mWindowManager.addView(mFloatingContainer, mLayoutParams);
            }
            mFloatingContainer.addView(view,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public void onDestroy(){
        if(mWindowManager!=null){
            mWindowManager.removeView(mFloatingContainer);
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hideFloating(){
        if(mFloatingContainer!=null) {
            mFloatingContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 显示悬浮窗
     */
    public void showFloating(){
        if(mFloatingContainer!=null) {
            mFloatingContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 横竖屏切换更新悬浮窗位置
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        //发生横竖屏切换需要更新窗口大小并且处理悬浮窗位置边界问题
        mWindowManager.getDefaultDisplay().getSize(mWindowSize);
        if(mLayoutParams.x > mWindowSize.x-mWidgetW){
            mLayoutParams.x = mWindowSize.x - mWidgetW;
            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
        }

        if(mLayoutParams.y > mWindowSize.y-mWidgetH){
            mLayoutParams.y = mWindowSize.y - mWidgetH;
            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack{
        boolean revocerenable();
    }
}
