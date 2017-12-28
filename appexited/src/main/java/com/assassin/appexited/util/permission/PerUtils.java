package com.assassin.appexited.util.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * permissionUtils
 */

public class PerUtils {

    public static Intent openApplicationSettings(String packageName) {
        
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            return intent;
    }

    public static void openApplicationSettings(Context context, String packageName) {
       
            Intent intent = openApplicationSettings(packageName);
            context.startActivity(intent);
        
    }
}
