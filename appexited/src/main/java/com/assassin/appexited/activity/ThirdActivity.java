package com.assassin.appexited.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.assassin.appexited.MainActivity;
import com.assassin.appexited.R;

import static com.assassin.appexited.activity.FirstActivity.isNotCrash;

public class ThirdActivity extends BaseActivity
{
    private  final String TAG = this.getClass().getSimpleName();
    private static String haha ="呵呵啊";
    
    @Override
    public int getLayoutId() {
        //在这里判断

        Log.d(TAG, "ThirdActivity启动了");
        return R.layout.activity_thrid;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        String name = getIntent().getStringExtra("张三");

        TextView textView = findViewById(R.id.tv_first_layout3);
        textView.setText(MainActivity.nima);
        //  textView.setText(name);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState启动了");
        String name = (String) outState.get("张三");
        Log.d(TAG, "得到储存的值："+name);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState启动了启动了");
        String name = (String) savedInstanceState.get("张三");
        Log.d(TAG, "得到回复的值："+name);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("此时的只", "" + isNotCrash);
    }
}
