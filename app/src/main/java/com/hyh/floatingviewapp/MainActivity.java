package com.hyh.floatingviewapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (FloatPermission.checkPermissionAbove6(this)) {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingButtonService.class));
            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    public void startFloatingButtonService(View view) {
        if (FloatingButtonService.isStarted) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6以上机型需要动态授权悬浮窗权限
            if(!Settings.canDrawOverlays(this)){
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }else{
                startService(new Intent(MainActivity.this, FloatingButtonService.class));
            }
        }else {
            //6以下机型默认会授权悬浮窗权限，但有些rom会有问题，所以这里通过反射去判断是否已经授权
            if(FloatPermission.checkOp(this)){
                startService(new Intent(MainActivity.this, FloatingButtonService.class));
            }else{
                //未授权跳转到对应机型权限设置页面，需要适配不同机型
            }
        }
    }

}
