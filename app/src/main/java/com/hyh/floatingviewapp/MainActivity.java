package com.hyh.floatingviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.hyh.floating.helper.PermissionManager;

public class MainActivity extends AppCompatActivity implements PermissionManager.PermissinCallBack {
    private PermissionManager mPermissionManager;
    private FloatingService mService;
    private boolean mServiceConnected;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("hyh", "MainActivity: onServiceConnected: connected");
            FloatingService.MyBinder binder = (FloatingService.MyBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("hyh", "MainActivity: onServiceDisconnected: unconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionManager = new PermissionManager(this,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mPermissionManager!=null){
            mPermissionManager.onActivityResult(requestCode,resultCode,data);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public void start(View view){
        if(mPermissionManager!=null){
            mPermissionManager.start();
        }
    }

    public void stop(View view){
       unbindService();
    }

    public void showFloating(View view){
        if(mService!=null){
            mService.showFloating();
        }
    }

    public void hideFloating(View view){
        if(mService!=null){
            mService.hideFloating();
        }
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(this, FloatingService.class);
        mServiceConnected = bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    private void unbindService(){
        if(mServiceConnected) {
            unbindService(mServiceConnection);
            mServiceConnected = false;
        }
    }
}
