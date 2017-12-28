package com.goldmantis.binderstudy.remoteServer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.goldmantis.binderstudy.IAddAidlInterface;

public class AddService extends Service 
{
    public AddService() 
    {
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    private Binder binder = new IAddAidlInterface.Stub()

    {
        @Override
        public int add(int a, int b) throws RemoteException 
        {
            return a+b;
        }
    };
}
