package com.assassin.muiltprocessstudy.selfBinder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.assassin.muiltprocessstudy.Book;

import java.util.List;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/24 11:06
 * @version: 1.0
 * @description: 模仿aidl，自定义进程通信的binder
 */

public interface ShayBookManager extends IInterface {
    //标识符
    String DESCRIPTOR = "com.assassin.muiltprocessstudy.selfBinder.ShayBookManager";
    //两个方法的标识符
    int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION + 0;
    int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

     List<Book> getBookList() throws RemoteException;

    void addBook(Book book) throws RemoteException;

}
