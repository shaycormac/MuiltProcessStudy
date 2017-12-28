package com.assassin.binderpool.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.assassin.binderpool.ComputeImpl;
import com.assassin.binderpool.IBinderPool;
import com.assassin.binderpool.SecurityCenterImpl;

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
    //将binder接口整合到这里来。
    private IBinderPool mBinderPool;
    //单例
    private static  volatile  BinderPool sInstance;
    //这个家伙主要工作是把异步请求转换成同步请求
    private CountDownLatch mConnectBinderPoolCountDownLatch;
    //两个静态常量表明两个binder远程接口，当然还可以更多
    /**
     * 静态变量的含义
     */
    public static final int BINDER_NONE = -1;
    /**
     * 计算的请求
     */
    public static final int BINDER_COMPUTE = 0;
    /**
     * 加密，解密
     */
    public static final int BINDER_SECURITY_CENTER = 1;

    private BinderPool(Context context) 
    {
        //单例，使用app的引用。
        mContext = context.getApplicationContext();
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
                //设置死亡的时候，尝试重新连接
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
    
    //静态方法，实现binder池的接口
    public static class BinderPoolImpl extends IBinderPool.Stub
    {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException 
        {
            IBinder binder = null;
            switch(binderCode){
                case BINDER_SECURITY_CENTER:
                    //根据不同的code，返回不同的binder
                    binder = new SecurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
                default:break;
            }
            return binder;
        }
    }
}
