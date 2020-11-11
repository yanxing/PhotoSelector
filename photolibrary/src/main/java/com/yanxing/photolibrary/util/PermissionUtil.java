package com.yanxing.photolibrary.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * android6.0以上权限请求
 * Created by lishuangxiang on 2016/10/11
 */
public class PermissionUtil {

    /**
     * Fragment中检查权限并申请
     *
     * @param fragment
     * @param permissions
     * @param requestCode
     */
    public static void requestPermission(Fragment fragment, String permissions[], int requestCode) {
        if (permissions.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestCode);
            }
        }
    }

    /**
     * Activity中申请权限
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermission(Activity activity, String permissions[], int requestCode) {
        if (permissions.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissions, requestCode);
            }
        }
    }

    /**
     * 查找没有被授权的权限
     *
     * @param activity
     * @param permissions
     */
    public static String[] findNeedRequestPermissions(Activity activity, String permissions[]) {
        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permissions[i]);
                }
            }
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }

}
