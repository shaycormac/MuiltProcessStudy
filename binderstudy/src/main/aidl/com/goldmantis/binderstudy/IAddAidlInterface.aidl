// IAddAidlInterface.aidl
package com.goldmantis.binderstudy;

// Declare any non-default types here with import statements
//相加的方法，远程服务器操作，测试

interface IAddAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int add(int a,int b);
}
