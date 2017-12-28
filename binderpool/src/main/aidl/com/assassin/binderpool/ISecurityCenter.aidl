// ISecurityCenter.aidl
package com.assassin.binderpool;

// Declare any non-default types here with import statements

interface ISecurityCenter 
{
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     * 提供加密解密功能
     */
   
            String encrypt(String content);
            String decrypt(String password);
}
