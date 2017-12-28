package com.goldmantis.binderstudy.selfBinder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.goldmantis.binderstudy.Book;

import java.util.List;

/**
 * @Author: Shay-Patrick-Cormac
 * @Email: fang47881@126.com
 * @Ltd: 金螳螂企业（集团）有限公司
 * @Date: 2017/12/27 14:25
 * @Version: 1.0
 * @Description: 自定义Binder的类
 */

public interface ISelfBookManagerInterface extends IInterface
{
    //设置静态的标识符和两个需要整的方法
    String DESCRIPTOR = "com.goldmantis.binderstudy.selfBinder.ISelfBookManagerInterface";
    int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;
    int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;
     List<Book> getBookList() throws RemoteException;
     void addBook(Book book) throws RemoteException;
    
}
