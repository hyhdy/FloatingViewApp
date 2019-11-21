package com.hyh.floatingviewapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class FloatingButtonService extends Service {
    public static final int FLAG_FOREGROUND = 1;
    private WindowManager mWindowManager;
    private Point mWindowSize;
    private WindowManager.LayoutParams mLayoutParams;

    private FloatingContainer mFloatingContainer;

    private IBinder mBinder;

    public class MyBinder extends Binder{
        FloatingButtonService getService(){
            return FloatingButtonService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d("hyh", "FloatingButtonService: onCreate: ");
        super.onCreate();
        mBinder = new MyBinder();
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

        switchtToForeground();
    }

    private void switchtToForeground(){
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
//        mBuilder.setContentTitle("这是标题");
//        mBuilder.setContentText("这是内容");
//        startForeground(FLAG_FOREGROUND, mBuilder.build());

//        Intent activityIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
//        Notification notification = new Notification.Builder(this).setAutoCancel(true).
//                setSmallIcon(R.drawable.ic_launcher_background).setTicker("前台Service启动")
//                .setContentTitle("前台Service运行中").
//                setContentText("这是一个正在运行的前台Service")
//                .setWhen(System.currentTimeMillis()).
//                setContentIntent(pendingIntent).build();
//        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("hyh", "FloatingButtonService: onBind: ");
        if(mFloatingContainer == null) {
            showFloatingWindow();
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("hyh", "FloatingButtonService: onStartCommand: ");
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

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("hyh", "FloatingButtonService: onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("hyh", "FloatingButtonService: onDestroy: ");
        mWindowManager.removeView(mFloatingContainer);
        super.onDestroy();
    }

    public void hideFloating(){
        mFloatingContainer.setVisibility(View.GONE);
    }

    public void showFloating(){
        mFloatingContainer.setVisibility(View.VISIBLE);
    }
}
