package com.assassin.binderpool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.assassin.binderpool.remote.BinderPool;
import com.assassin.binderpool.remote.BinderPoolImpl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BinderPoolActivity";
    //aidl接口
    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
        IBinder securityBinder=binderPool.queryUBinderByCode(BinderPoolImpl.BINDER_SECURITY_CENTER);
        //将本地的
        mSecurityCenter=SecurityCenterImpl.asInterface(securityBinder);
        Log.d(TAG, "visit ISecurityCenter");
        String msg = "helloworld-安卓";
        System.out.println("content:" + msg);

        String password = null;
        try {
            password = mSecurityCenter.encrypt(msg);System.out.println("encrypt:" + password);
            System.out.println("decrypt:" + mSecurityCenter.decrypt(password));
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "visit ICompute");
        IBinder computeBinder=binderPool.queryUBinderByCode(BinderPoolImpl.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        try {
            System.out.println("3+5=" + mCompute.add(3, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        
        
    }
}
