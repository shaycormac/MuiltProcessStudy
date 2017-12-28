package com.assassin.appexited.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.assassin.appexited.R;

public class FirstActivity extends BaseActivity {
    //设置一个静态布尔的变量，
    public static boolean isNotCrash;
   

    @Override
    public int getLayoutId() {
        isNotCrash = true;
        return R.layout.activity_first;
    }

    @Override
    protected void initView(Bundle savedInstanceState) 
    {
       
        TextView textView = findViewById(R.id.button_first);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(mActivity, SecondActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {

    }
}
