package com.hyh.floatingviewapp.floating;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

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
    private Context mContext;

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
        mLayoutParams.x = 200;
        mLayoutParams.y = 200;
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
                        //悬浮窗尺寸改变需要更新悬浮窗位置
                        mWidgetW = widgetW;
                        mWidgetH = widgetH;
                        if(mLayoutParams.x > mWindowSize.x-widgetW){
                            //处理边界问题
                            mLayoutParams.x = mWindowSize.x - widgetW;
                            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                        }

                        if(mLayoutParams.y > mWindowSize.y-widgetH){
                            //处理边界问题
                            mLayoutParams.y = mWindowSize.y - widgetH;
                            mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
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
}
