package com.assassin.appexited;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.assassin.appexited.activity.BaseActivity;
import com.assassin.appexited.activity.FirstActivity;
import com.assassin.appexited.asyncTask.AsyncTaskActivity;
import com.assassin.appexited.imageLoader.activity.ImageLoaderActivity;
import com.assassin.appexited.intentService.ShayIntentServiceActivity;

public class MainActivity extends BaseActivity {
    private  final String TAG = this.getClass().getSimpleName();
 
    //
    public static String nima;

   
    
    @Override
    public int getLayoutId() {
        FirstActivity.isNotCrash = true;
        Log.d(TAG,"MainActivity启动了");
        nima = "哈哈";
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) 
    {
      
        TextView textView = findViewById(R.id.tv_first_layout);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_async_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AsyncTaskActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_intent_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ShayIntentServiceActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_image_loader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ImageLoaderActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {

    }
}
