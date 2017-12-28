package com.assassin.binderpool.remote;

import android.os.IBinder;
import android.os.RemoteException;

import com.assassin.binderpool.ComputeImpl;
import com.assassin.binderpool.IBinderPool;
import com.assassin.binderpool.SecurityCenterImpl;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/17 16:23
 * @version: 1.0
 * @description: 远程服务端，根据不同的code,返回不同的Binder
 */

public class BinderPoolImpl extends IBinderPool.Stub 
{
    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;
    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException 
    {
        IBinder binder = null;
        switch (binderCode) {
            case BINDER_SECURITY_CENTER: {
                binder = new SecurityCenterImpl();
                break;
            }
            case BINDER_COMPUTE: {
                binder = new ComputeImpl();
                break;
            }
            default:
                break;
        }

        return binder;
    }
}
