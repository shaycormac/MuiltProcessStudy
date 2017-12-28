package com.goldmantis.binderstudy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.goldmantis.binderstudy.remoteServer.AddService;

public class MainActivity extends AppCompatActivity {

    private IAddAidlInterface addAidlInterface;
    private EditText etA;
    private EditText etB;
    private TextView tvResult;
    private Button btnCalculate;
    private Button btnSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        tvResult = findViewById(R.id.tvResult);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSecond = findViewById(R.id.btnSecond);
        //绑定Service
        Intent intent = new Intent(MainActivity.this, AddService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把数据传过去
                String aa = etA.getText().toString();
                String bb = etB.getText().toString();
                try {

                    int result = addAidlInterface.add(Integer.parseInt(aa), Integer.parseInt(bb));
                    tvResult.setText("得到的结果为：" + result);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BookManagerClientActivity.class));
            }
        });
        
       
    }

    //绑定服务
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) 
        {
            //得到这个代理对象service,从而得到这个饮用1
            addAidlInterface = IAddAidlInterface.Stub.asInterface(service);
           

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }
    };
}
