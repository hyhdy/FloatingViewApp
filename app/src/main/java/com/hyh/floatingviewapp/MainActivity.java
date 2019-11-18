package com.hyh.floatingviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionManager = new PermissionManager(this);
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

//    public void startFloatingButtonService() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //6以上机型需要动态授权悬浮窗权限
//            if(!Settings.canDrawOverlays(this)){
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_FLOATING_PERMISSIONS);
//            }else{
//                startFloating();
//            }
//        }else {
//            //6以下机型默认会授权悬浮窗权限，但有些rom会有问题，所以这里通过反射去判断是否已经授权
//            if(FloatPermission.checkOp(this)){
//                startFloating();
//            }else{
//                //未授权跳转到对应机型权限设置页面，需要适配不同机型
//            }
//        }
//    }
}
