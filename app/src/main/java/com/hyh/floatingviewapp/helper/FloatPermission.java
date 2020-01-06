package com.hyh.floatingviewapp.helper;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * created by curdyhuang on 2019-11-08
 */
public class FloatPermission {

    /**
     * 检查悬浮窗权限
     * @return
     */
    public static boolean checkPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6以上机型需要动态授权悬浮窗权限
            return Settings.canDrawOverlays(context);
        }else{
            //6以下机型默认会授权悬浮窗权限，但有些rom会有问题，所以这里通过反射去判断是否已经授权
            return checkOp(context);
        }
    }

    /**
     * 6.0以上机型检查悬浮窗权限
     * @return
     */
    public static boolean checkPermissionAbove6(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6以上机型需要动态授权悬浮窗权限
            return Settings.canDrawOverlays(context);
        }
        return false;
    }

    private static final int OP_SYSTEM_ALERT_WINDOW = 24;

    public static boolean checkOp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, OP_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
            }
        }
        return false;
    }
}
