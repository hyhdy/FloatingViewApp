package com.hyh.floatingviewapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hyh.floating.common.FloatingWindowManager;
import com.hyh.floatingviewapp.view.FloatingView;

public class FloatingService extends Service implements FloatingWindowManager.CallBack {
    public static final int FLAG_FOREGROUND = 1;
    public static final String CHANNEL_ID ="com.hyh.floatingviewapp.hyh";

    private FloatingWindowManager mFloatingWindowManager;
    private FloatingView mFloatingView;

    private IBinder mBinder;

    @Override
    public boolean revocerenable() {
        return !mFloatingView.isShowMsg();
    }

    public class MyBinder extends Binder{
        public FloatingService getService(){
            return FloatingService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d("hyh", "FloatingService: onCreate: ");
        super.onCreate();
        mBinder = new MyBinder();
        mFloatingWindowManager = new FloatingWindowManager(this);
        mFloatingWindowManager.setCallBack(this);
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
        int icon = com.hyh.floating.R.drawable.ic_launcher_background;

        Intent intent = new Intent(this, MainActivity.class);
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
        Log.d("hyh", "FloatingService: onBind: ");
        if(mFloatingView == null){
            mFloatingView  = new FloatingView(this);
            if(mFloatingWindowManager != null) {
                mFloatingWindowManager.addFloatingView(mFloatingView);
            }
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("hyh", "FloatingService: onStartCommand: ");
        if(mFloatingView == null){
            mFloatingView  = new FloatingView(this);
            if(mFloatingWindowManager != null) {
                mFloatingWindowManager.addFloatingView(mFloatingView);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("hyh", "FloatingService: onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("hyh", "FloatingService: onDestroy: ");
        if(mFloatingWindowManager!=null){
            mFloatingWindowManager.onDestroy();
        }
        super.onDestroy();
    }

    public void hideFloating(){
        if(mFloatingWindowManager!=null){
            mFloatingWindowManager.hideFloating();
        }
    }

    public void showFloating(){
        if(mFloatingWindowManager!=null){
            mFloatingWindowManager.showFloating();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mFloatingWindowManager!=null){
            mFloatingWindowManager.onConfigurationChanged(newConfig);
        }
    }
}
