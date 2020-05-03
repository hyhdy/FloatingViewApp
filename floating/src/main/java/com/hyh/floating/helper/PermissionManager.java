package com.hyh.floating.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * created by curdyhuang on 2019-11-18
 */
public class PermissionManager {
    private static final int REQUEST_FLOATING_PERMISSIONS = 1;//请求悬浮窗权限
    private PermissinCallBack mCallBack;

    private Activity mHost;

    @TargetApi(LOLLIPOP)
    public PermissionManager(@NonNull Activity host,PermissinCallBack callBack) {
        mHost = host;
        mCallBack = callBack;
    }

    public void start(){
        //检查悬浮窗权限
        if(checkFloatingPermission()){
            if(mCallBack!=null){
                mCallBack.onSuccess();
            }
        }else{
            //请求悬浮窗权限
            requestFloatingPermissions();
        }
    }

    /**
     * 检查悬浮窗权限
     * @return
     */
    private boolean checkFloatingPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(mHost);
        }else {
            //6以下机型默认会授予悬浮窗权限，但有些rom会有问题，所以这里通过反射去判断是否已经授权
            return FloatPermission.checkOp(mHost);
        }
    }

    /**
     * 请求悬浮窗权限
     */
    private void requestFloatingPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6以上机型需要动态申请悬浮窗权限
            mHost.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mHost.getPackageName())), REQUEST_FLOATING_PERMISSIONS);
        }else {
            //todo 6以下机型未授权跳转到对应机型权限设置页面，需要适配不同机型
        }
    }

    @TargetApi(LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FLOATING_PERMISSIONS) {
            if (FloatPermission.checkPermissionAbove6(mHost)) {
                if(mCallBack!=null){
                    mCallBack.onSuccess();
                }
            } else {
                Toast.makeText(mHost, "授权悬浮窗权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface PermissinCallBack{
        void onSuccess();
    }
}
