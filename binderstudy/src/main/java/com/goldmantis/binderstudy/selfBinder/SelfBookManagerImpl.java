package com.goldmantis.binderstudy.selfBinder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.goldmantis.binderstudy.Book;

import java.util.List;

/**
 * @Author: Shay-Patrick-Cormac
 * @Email: fang47881@126.com
 * @Ltd: 金螳螂企业（集团）有限公司
 * @Date: 2017/12/27 14:32
 * @Version: 1.0
 * @Description: 模仿AIDL自己生成的
 */

public class SelfBookManagerImpl extends Binder implements ISelfBookManagerInterface {

    public SelfBookManagerImpl() {
        //很重要，把自己搞进Binder类中，相互结合
        this.attachInterface(this,DESCRIPTOR);
    }

    @Override
    public List<Book> getBookList() throws RemoteException {
        return null;
    }

    @Override
    public void addBook(Book book) throws RemoteException {

    }

    //自己就是Binder
    @Override
    public IBinder asBinder() 
    {
        return this;
    }
    
    //写一个静态的方法，用来把一个服务端的Binder类搞成代理
    public static ISelfBookManagerInterface asInterface(IBinder obj)
    {
        if (obj == null) {
            return null;
        }
        //首先查找是不是同一个进程的
        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        //是的话，返回本地的
        if ((iin!=null) && (iin instanceof ISelfBookManagerInterface))
        {
            return (ISelfBookManagerInterface) iin;
        }
        //否则不是统一进程，使用代理
        return new Proxy(obj);
    }
    //如果使用代理类，最后到服务端，由共享内存中的数据读取到服务端的用户内存中

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException
    {
        switch(code)
        {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
                //这是查找书的方法
            case TRANSACTION_getBookList:
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = getBookList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            case TRANSACTION_addBook:
                data.enforceInterface(DESCRIPTOR);
                Book arg0;
                if (0!=data.readInt())
                {
                    //将Book进行反序列化，搞到服务器的用户内存中
                    arg0 = Book.CREATOR.createFromParcel(data);
                }else 
                {
                    arg0=null;
                }
                this.addBook(arg0);
                reply.writeNoException();
                return true;
            default:break;
        }
        return super.onTransact(code, data, reply, flags);
    }
    
    //内部代理类
    private static class Proxy implements ISelfBookManagerInterface
    {
        //需要代理的Binder
        private IBinder remote;
       //
        public Proxy(IBinder remote)
        {
            this.remote = remote;
        }

        @Override
        public List<Book> getBookList() throws RemoteException 
        {
            
            //首先从Parcel池中得到两个空的对象
            Parcel date = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result;
            try {
                date.writeInterfaceToken(DESCRIPTOR);
                remote.transact(TRANSACTION_getBookList, date, reply, 0);
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR);
            }finally {
                reply.recycle();
                date.recycle();
            }
         
            return result;
        }

        @Override
        public void addBook(Book book) throws RemoteException 
        {
            //首先从Parcel池中得到两个空的对象
            Parcel date = Parcel.obtain();
            Parcel reply = Parcel.obtain();
           try {
               date.writeInterfaceToken(DESCRIPTOR);
               if (book!=null)
               {
                   date.writeInt(1);
                   //将Book序列化，写进共享空间中
                   book.writeToParcel(date,0);

               }else
               {
                   date.writeInt(0);
               }
               remote.transact(TRANSACTION_addBook, date, reply, 0);
               reply.readException(); 
           }finally {
               reply.recycle();
               date.recycle();
           }
            
        }

        @Override
        public IBinder asBinder() {
            return remote;
        }
        //这个方法，返回标志
        public String getInterfaceDescriptior()
        {
            return DESCRIPTOR;
        }
    }
}
