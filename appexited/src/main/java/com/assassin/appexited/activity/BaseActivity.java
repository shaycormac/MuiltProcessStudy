package com.assassin.appexited.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import static com.assassin.appexited.activity.FirstActivity.isNotCrash;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/22 09:40
 * @version: 1.0
 * @description: 作为基类。
 */

public abstract class BaseActivity extends AppCompatActivity 
{
    
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) 
    {
        mActivity = this;
        //不是主页面，判断此时的静态标志位
       
        super.onCreate(savedInstanceState);
        doBeforeSetView();
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        setContentView(getLayoutId());
        if(!(mActivity instanceof FirstActivity) && !isNotCrash)
        {
            //到主页面，并关闭当前页面
            Intent intent = new Intent(this, FirstActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        //兼容4.4状态栏透明'
        initView(savedInstanceState);
        initData();
    }
    

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    protected  void getBundleExtras(Bundle extras){};

    /**
     * 布局id
     * @return
     */
    public abstract int getLayoutId();
    /**
     * 初始化控件
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    protected abstract void initData();

    private void doBeforeSetView() {

        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }




}
