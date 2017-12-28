package com.assassin.appexited.app;

import android.app.Application;
import android.util.Log;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/13 14:00
 * @version:
 * @description:
 */

public class ExitedApp extends Application 
{
    @Override
    public void onCreate() 
    {
        super.onCreate();
        Log.d("ExitedApp", "ExitedApp启动了");
    }
}
