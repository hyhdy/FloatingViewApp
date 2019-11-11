package com.hyh.floatingviewapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;

/**
 * Created by dongzhong on 2018/5/30.
 */

public class FloatingButtonService extends Service {
    public static boolean isStarted = false;

    private WindowManager mWindowManager;
    private Point mWindowSize;
    private WindowManager.LayoutParams mLayoutParams;

    private FloatingContainer mFloatingContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowSize = new Point();
        mWindowManager.getDefaultDisplay().getSize(mWindowSize);
        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mFloatingContainer == null) {
            showFloatingWindow();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (FloatPermission.checkPermission(this)) {
            mFloatingContainer = new FloatingContainer(getApplicationContext());
            mFloatingContainer.setOnFloatingListener(new FloatingContainer.OnFloatingListener() {
                @Override
                public void onMove(int movedX, int movedY,int widgetW,int widgetH) {
                    int newX = mLayoutParams.x + movedX;
                    int newY = mLayoutParams.y + movedY;
                    if(newX < 0 || newY < 0 || newX > mWindowSize.x-widgetW || newY > mWindowSize.y-widgetH){
                        Log.d("hyh", "FloatingButtonService: onMove: return");
                        return;
                    }

                    mLayoutParams.x = newX;
                    mLayoutParams.y = newY;
                    mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                }

                @Override
                public void onSizeChanged(int widgetW, int widgetH) {
                    if(mLayoutParams.x > mWindowSize.x-widgetW){
                        mLayoutParams.x = mWindowSize.x - widgetW;
                        mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                    }

                    if(mLayoutParams.y > mWindowSize.y-widgetH){
                        mLayoutParams.y = mWindowSize.y - widgetH;
                        mWindowManager.updateViewLayout(mFloatingContainer, mLayoutParams);
                    }
                }
            });
            mWindowManager.addView(mFloatingContainer, mLayoutParams);
        }
    }
}
