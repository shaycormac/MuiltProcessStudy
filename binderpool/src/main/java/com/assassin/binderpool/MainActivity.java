package com.assassin.binderpool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.assassin.binderpool.remote.BinderPool;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BinderPoolActivity";
    //aidl接口,套路，客户端想要拿到服务端的binder引用，只需要这么做
    //首先用接口作为句柄，找到真身就是在返回来的（IBinder service）接口中强转
    // mSecurityCenter=SecurityCenterImpl.asInterface(service);
    //得到这个实例后，就可以使用远程的方法了。
    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //之所以把它放在一个异步线程中，是因为调用远程的可能是耗时工作，不能在UI线程中，而binder池使用的CountDownLatch
        //也是一个同步的操作，可能耗时，因此要放在异步线程中去执行。
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(MainActivity.this);
        IBinder securityBinder=binderPool.queryUBinderByCode(BinderPool.BINDER_SECURITY_CENTER);
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
        IBinder computeBinder=binderPool.queryUBinderByCode(BinderPool.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        try {
            System.out.println("3+5=" + mCompute.add(3, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        
        
    }
}
