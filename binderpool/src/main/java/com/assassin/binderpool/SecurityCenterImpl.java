package com.assassin.binderpool;

import android.os.RemoteException;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/17 16:14
 * @version:
 * @description:
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub 
{
    private static final char SECRET_CODE = '^';
    @Override
    public String encrypt(String content) throws RemoteException 
    {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
