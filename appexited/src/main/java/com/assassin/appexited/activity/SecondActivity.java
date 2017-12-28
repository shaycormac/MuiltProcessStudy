package com.assassin.appexited.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.assassin.appexited.R;

public class SecondActivity extends BaseActivity {
    private  final String TAG = this.getClass().getSimpleName();
    
    @Override
    public int getLayoutId() {
        Log.d(TAG, "SecondActivity启动了");
        return R.layout.activity_second;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        TextView textView = findViewById(R.id.tv_first_layout2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("张三", "张三");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {

    }
}
