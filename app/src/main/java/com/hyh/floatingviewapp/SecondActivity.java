package com.hyh.floatingviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class SecondActivity extends AppCompatActivity implements PermissionManager.PermissinCallBack {
    private PermissionManager mPermissionManager;
    private FloatingButtonService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FloatingButtonService.MyBinder binder = (FloatingButtonService.MyBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mPermissionManager = new PermissionManager(this,this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(mPermissionManager!=null){
            mPermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
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
        unbindService(mServiceConnection);
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
        Intent intent = new Intent(this, FloatingButtonService.class);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
