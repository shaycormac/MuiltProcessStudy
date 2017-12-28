package com.assassin.appexited.asyncTask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.assassin.appexited.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 3.0之后。多个task是串行执行，效率们，自己实现并行搞起来~~~
 */
public class AsyncTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        findViewById(R.id.btn_task_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) 
            {
                testAsyncTask();
                
            }
        });
        findViewById(R.id.btn_task_concureent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) 
            {
                asyncTaskConcurrent();
                
            }
        });
    }

    private void asyncTaskConcurrent() {
        new MySyncTask("AsyncTask#1").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        new MySyncTask("AsyncTask#2").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        new MySyncTask("AsyncTask#3").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        new MySyncTask("AsyncTask#4").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        new MySyncTask("AsyncTask#5").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
    }

    private void testAsyncTask() 
    {
        new MySyncTask("AsyncTask#1").execute("");
        new MySyncTask("AsyncTask#2").execute("");
        new MySyncTask("AsyncTask#3").execute("");
        new MySyncTask("AsyncTask#4").execute("");
        new MySyncTask("AsyncTask#5").execute("");
        
    }
    
    static class MySyncTask extends AsyncTask<String,Integer,String>
    {
        private String name = getClass().getSimpleName();
        private String TAG=getClass().getSimpleName();

        public MySyncTask(String name) {
            this.name = name;
        }

        @Override
        protected String doInBackground(String... strings) 
        {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            return name;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Log.d(TAG, s + "执行完毕在：" + format.format(new Date()));
        }
    }
}
