package com.assassin.binderpool.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.assassin.binderpool.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/17 16:25
 * @version: 1.0
 * @description: 单例，提供binder
 */

public class BinderPool 
{
    private static final String TAG = "BinderPool";
    private Context mContext;
    private IBinderPool mBinderPool;
    //单例
    private static  volatile  BinderPool sInstance;
    //???
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context mContext) 
    {
        this.mContext = mContext.getApplicationContext();
        connectBinderPoolService();
        
    }
    
    //单例
    public static BinderPool getInstance(Context context)
    {
        if (sInstance == null) {
            synchronized (BinderPool.class)
            {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }

        return sInstance;
    }
    
    //对外提供binder
    public IBinder queryUBinderByCode(int binderCode)
    {
        IBinder binder = null;
       
            try
            {
                if (mBinderPool != null)
                {
                binder = mBinderPool.queryBinder(binderCode);}
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        return binder;
        
    }

    //确保之窗见一次
    private synchronized void connectBinderPoolService()
    {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        //打开服务
        Intent service = new Intent(mContext,BinderPoolService.class );
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //连接和死亡通知
    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 已经连接上远程的服务器！！");
            mBinderPool = IBinderPool.Stub.asInterface(service);

            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceConnected: 已经失去连接！！");
        }
    };

    //死亡通知
    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() 
        {
            Log.w(TAG, "binder died.");
            //干掉连接后再重新连接
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            //重新连接
            connectBinderPoolService();
        }
    };
}
