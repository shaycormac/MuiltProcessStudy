package com.assassin.muiltprocessstudy.selfBinder;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.assassin.muiltprocessstudy.Book;

import java.util.List;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/24 11:13
 * @version:
 * @description:
 */

public class ShayBookManagerImpl extends Binder implements ShayBookManager
{
    public ShayBookManagerImpl() 
    {
        //将标识符传入Binder的方法中
        this.attachInterface(this,DESCRIPTOR);
    }

    //三个未实现的方法
    @Override
    public List<Book> getBookList() throws RemoteException {
        //待实现
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
    //待实现
    }

    //返回本身。
    @Override
    public IBinder asBinder() 
    {
        return this;
    }
    
    
    //下面是binder的方法实现


    //服务端把结果写出去
    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException 
    {
        switch(code){
            //先把标识符写进去
            case INTERFACE_TRANSACTION:
            {
                
                reply.writeString(DESCRIPTOR);
                return true;
            }
            
            default:break;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
