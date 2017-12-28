package com.assassin.appexited.intentService;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/14 10:16
 * @version:
 * @description:
 */

public class ShayIntentService extends IntentService 
{
    /**
     * 静态变量的含义
     */
    public static final String TAG = "ShayIntentService";
    
    public ShayIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        //在这里执行后台任务！ 得到Itent
        String shayAction = intent.getStringExtra("shay_task");
        Log.d(TAG, "onHandleIntent: recive task:" + shayAction);
        SystemClock.sleep(3000);
        if ("shay.action.task1".equals(shayAction))
        {
            Log.d(TAG, "onHandleIntent: handle task:" + shayAction);
        }
    }

    @Override
    public void onDestroy() 
    {
        Log.d(TAG, "onDestroy: shayIntentService is destoryed!!");
        super.onDestroy();
    }
}
