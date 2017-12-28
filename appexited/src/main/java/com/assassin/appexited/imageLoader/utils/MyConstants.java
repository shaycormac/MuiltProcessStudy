package com.assassin.appexited.imageLoader.utils;

import android.os.Environment;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/15 13:23
 * @version: 1.0
 * @description:  图片储存类
 */

public class MyConstants 
{

    public static final String CHAPTER_2_PATH = Environment.getExternalStorageDirectory().getPath() 
            + "/shay/image_loader/";

    public static final String CACHE_FILE_PATH = CHAPTER_2_PATH + "image_cache";
    
    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVICE = 1;
}
