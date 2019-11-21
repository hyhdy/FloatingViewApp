package com.hyh.floatingviewapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
    public static final String CHANNEL_ID ="com.hyh.floatingviewapp.hyh";
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

        setForeground();
    }

    /**
     * 开启前台进程，这里需要做适配
     */
    private void setForeground(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //设定的通知渠道名称
            String channelName = "channel_floating";
            //设置通知的重要程度
            int importance = NotificationManager.IMPORTANCE_LOW;
            //构建通知渠道
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription("none");
            //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            //在创建的通知渠道上发送通知
            startForeground(FLAG_FOREGROUND,createNotification(CHANNEL_ID));
        }else{
            startForeground(FLAG_FOREGROUND, createNotification(CHANNEL_ID));
        }
    }

    private Notification createNotification(String channelId){
        String title = "标题";
        String content = "内容";
        int icon = R.drawable.ic_launcher_background;

        Intent intent = new Intent(this, SecondActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(icon)//设置通知图标
                .setContentTitle(title)//设置通知标题
                .setContentText(content)//设置通知内容
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi);
        return builder.build();
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
