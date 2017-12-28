// IBookManager.aidl
package com.goldmantis.binderstudy;
import com.goldmantis.binderstudy.Book;
// Declare any non-default types here with import statements
//aidl接口，当有新的图书到来时，通知客户端
import com.goldmantis.binderstudy.INewBookArrivedInterface;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    
    //这两个接口
    void registerListener(INewBookArrivedInterface listener);
    void unregisterListener(INewBookArrivedInterface listener);
}
