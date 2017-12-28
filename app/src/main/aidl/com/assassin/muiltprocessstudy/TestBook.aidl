// TestBook.aidl
package com.assassin.muiltprocessstudy;
import com.assassin.muiltprocessstudy.Book;

// Declare any non-default types here with import statements

interface TestBook {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void addBook(in Book book);
    
    List<Book> getBookList();
}
