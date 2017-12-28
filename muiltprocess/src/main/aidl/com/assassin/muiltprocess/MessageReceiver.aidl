// MessageReceiver.aidl
package com.assassin.muiltprocess;
import com.assassin.muiltprocess.MessageModel; 
// Declare any non-default types here with import statements

interface MessageReceiver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onMessageReceived(in MessageModel receivedMessage);
}
