// IBinderPool.aidl
package com.assassin.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     * binder池，根据不同的code码，返回不同的binder,把他们使用一个Service来搞定
     */
   IBinder queryBinder(int binderCode);
}
