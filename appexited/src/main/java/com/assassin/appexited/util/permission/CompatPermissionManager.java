package com.assassin.appexited.util.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/8 13:09
 * @version: 1.0
 * @description: 解决之前版本的内存泄露 ,在activity或者fragment中的onDestroy中调用recycleActivity方法
 */

public enum CompatPermissionManager
{
    INSTANCE;
    private Activity activity;
    private PermissionsCallback mPermissionsCallback;
    private ArrayList<PermissionEnum> mPermissions;
    private ArrayList<PermissionEnum> mPermissionsGranted;
    private ArrayList<PermissionEnum> mPermissionsDenied;
    private int mTag = 100;


    public CompatPermissionManager with(Activity activity) 
    {
        this.activity = activity;
        return this;
    }

    public  void handleResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        
        if (requestCode == mTag) 
        {
            for (int i = 0; i < permissions.length; i++) 
            {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) 
                {
                    mPermissionsGranted.add(PermissionEnum.onResultPermissions(permissions[i]));
                } else {
                    mPermissionsDenied.add(PermissionEnum.onResultPermissions(permissions[i]));
                }
            }
            showResult();
        }

    }

    /**
     * 权限请求的返回状态区分
     *
     * @param tag
     * @return
     */
    public CompatPermissionManager tag(int tag) {
        this.mTag = tag;
        return this;
    }

    public void checkAsk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initArrayList();
            String[] permissionToAsk = permissionToAsk();
            if (permissionToAsk.length == 0) {
                showResult();
            } else {//权限不明
                for (int i = 0; i < permissionToAsk.length; i++) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionToAsk[i])) {
                        mPermissionsDenied.add(PermissionEnum.onResultPermissions(permissionToAsk[i]));
                    }
                }
                // TODO: 2017/3/16 修复部分bug
                //权限已经被拒绝
                if (mPermissionsDenied.size() != 0) {
                    showResult();
                } else {//权限没有被拒绝 直接申请
                    ActivityCompat.requestPermissions(activity,permissionToAsk,mTag);
                }
            }
        } else {
            initArrayList();
            mPermissionsGranted.addAll(mPermissions);
            showResult();
        }
    }

    /**
     * @param permissions an array of permission that you need to ask
     * @return current instance
     */
    public CompatPermissionManager permissions(ArrayList<PermissionEnum> permissions) {
        this.mPermissions = new ArrayList<>();
        this.mPermissions.addAll(permissions);
        return this;
    }


    /**
     * @param permissionEnum permission you need to ask
     * @return current instance
     */
    public CompatPermissionManager permission(PermissionEnum permissionEnum) {
        this.mPermissions = new ArrayList<>();
        this.mPermissions.add(permissionEnum);
        return this;
    }


    /**
     * @param permissions permission you need to ask
     * @return current instance
     */
    public CompatPermissionManager permission(PermissionEnum... permissions) {
        this.mPermissions = new ArrayList<>();
        Collections.addAll(this.mPermissions, permissions);
        return this;
    }

    public CompatPermissionManager callback(PermissionsCallback callback) {
        this.mPermissionsCallback = callback;
        return this;
    }


    private void initArrayList() {
        this.mPermissionsGranted = new ArrayList<>();
        this.mPermissionsDenied = new ArrayList<>();
    }


    /**
     * 检查是否拥有权限
     * @return permission that you realy need to ask
     */
    @NonNull
    private String[] permissionToAsk() {
        ArrayList<String> permissionToAsk = new ArrayList<>();
        for (PermissionEnum permission : mPermissions) {
            if (!isGranted(permission)) {
                permissionToAsk.add(permission.getPermisson());
            } else {
                mPermissionsGranted.add(permission);
            }
        }
        return permissionToAsk.toArray(new String[permissionToAsk.size()]);
    }

    private boolean isGranted(PermissionEnum permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(activity, permission.getPermisson()) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isGranted(Context context, PermissionEnum... permission) {
        for (PermissionEnum permissionEnum : permission) {
            if (!isGranted(context, permissionEnum)) {
                return false;
            }
        }
        return true;
    }

    private void showResult() {
        if (mPermissionsCallback != null) {
            if (mPermissionsDenied.size() > 0) {
                mPermissionsCallback.onDenied(mPermissionsDenied);
            } else {
                mPermissionsCallback.onGranted(mPermissionsGranted);
            }
        }
    }
    
    public void recycleActivity()
    {
        if (activity!=null) {
            activity = null;
        }
        if (mPermissionsCallback !=null)
        {
            mPermissionsCallback = null;
        }
    }

}
