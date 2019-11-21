package com.hyh.floatingviewapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;

/**
 * created by curdyhuang on 2019-11-18
 */
public class PermissionManager {
    private static final int REQUEST_AUDIO_PERMISSIONS = 0;//请求录音权限
    private static final int REQUEST_FLOATING_PERMISSIONS = 1;//请求悬浮窗权限
    private static final int REQUEST_MEDIA_PROJECTION = 2;//请求截屏权限

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private PermissinCallBack mCallBack;

    private Activity mHost;

    @TargetApi(LOLLIPOP)
    public PermissionManager(@NonNull Activity host,PermissinCallBack callBack) {
        mHost = host;
        mCallBack = callBack;
        //android 5.0以上才支持
        mMediaProjectionManager = (MediaProjectionManager) host.getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    public void start(){
        //检查录音权限
        if (checkAudioPermissions()) {
            //检查悬浮窗权限
            if(checkFloatingPermission()){
                //检查录屏权限
                if(checkRecordPermission()){
                    if(mCallBack!=null){
                        mCallBack.onSuccess();
                    }
                }else{
                    //请求录屏权限
                    requestMediaProjection();
                }
            }else{
                //请求悬浮窗权限
                requestFloatingPermissions();
            }
        } else if (Build.VERSION.SDK_INT >= M) {
            //请求录音权限
            requestAudioPermissions();
        }
    }

    /**
     * 检查录音权限
     * @return
     */
    private boolean checkAudioPermissions() {
        PackageManager pm = mHost.getPackageManager();
        if(pm != null) {
            String packageName = mHost.getPackageName();
            int granted = pm.checkPermission(RECORD_AUDIO, packageName);
            return granted == PackageManager.PERMISSION_GRANTED;
        }
        return false;
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
     * 检查录屏权限
     * @return
     */
    private boolean checkRecordPermission(){
        return mMediaProjection != null;
    }

    /**
     * 请求录音权限
     */
    @TargetApi(M)
    private void requestAudioPermissions() {
        mHost.requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_AUDIO_PERMISSIONS);
    }

    /**
     * 请求悬浮窗权限
     */
    private void requestFloatingPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6以上机型需要动态申请悬浮窗权限
            mHost.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mHost.getPackageName())), REQUEST_FLOATING_PERMISSIONS);
        }else {
            //6以下机型未授权跳转到对应机型权限设置页面，需要适配不同机型
        }
    }

    /**
     * 请求录屏权限
     */
    @TargetApi(LOLLIPOP)
    private void requestMediaProjection() {
        if(mMediaProjectionManager!=null) {
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            mHost.startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_PERMISSIONS) {
            int granted = PackageManager.PERMISSION_GRANTED;
            for (int r : grantResults) {
                granted |= r;
            }
            if (granted == PackageManager.PERMISSION_GRANTED) {
                if(checkFloatingPermission()){
                    if (checkRecordPermission()) {
                        if(mCallBack!=null){
                            mCallBack.onSuccess();
                        }
                    } else {
                        //请求录屏权限
                        requestMediaProjection();
                    }
                }else{
                    //请求悬浮窗权限
                    requestFloatingPermissions();
                }
            }else{
                Toast.makeText(mHost, "授权录音权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FLOATING_PERMISSIONS) {
            if (FloatPermission.checkPermissionAbove6(mHost)) {
                if(checkRecordPermission()){
                    if(mCallBack!=null){
                        mCallBack.onSuccess();
                    }
                }else{
                    //请求录屏权限
                    requestMediaProjection();
                }
            } else {
                Toast.makeText(mHost, "授权悬浮窗权限失败", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_MEDIA_PROJECTION&&mMediaProjectionManager!=null) {
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                Toast.makeText(mHost, "授权录屏权限失败", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaProjection = mediaProjection;
            if(mCallBack!=null){
                mCallBack.onSuccess();
            }
        }
    }

    public interface PermissinCallBack{
        void onSuccess();
    }
}
