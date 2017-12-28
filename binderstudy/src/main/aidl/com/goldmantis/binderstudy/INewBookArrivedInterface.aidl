// INewBookArrivedInterface.aidl
package com.goldmantis.binderstudy;

// Declare any non-default types here with import statements
import com.goldmantis.binderstudy.Book;
interface INewBookArrivedInterface 
{
   void onNewBookArrived(in Book book);
}
