package com.hyh.floatingviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyh.floatingviewapp.floating.FloatingService;

public class MainActivity extends AppCompatActivity implements PermissionManager.PermissinCallBack {
    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        stopService(new Intent(this, FloatingService.class));
    }

    public void skipNextActivity(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccess() {
        startService(new Intent(this, FloatingService.class));
    }
}
