package com.assassin.appexited.util.permission;

import java.util.ArrayList;

/**
 */

public interface PermissionsCallback {

    void onGranted(ArrayList<PermissionEnum> grantedList);

    void onDenied(ArrayList<PermissionEnum> deniedList);
}
