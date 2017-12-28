package com.assassin.binderpool;

import android.os.RemoteException;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/17 16:13
 * @version:
 * @description:
 */

public class ComputeImpl extends ICompute.Stub 
{
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
