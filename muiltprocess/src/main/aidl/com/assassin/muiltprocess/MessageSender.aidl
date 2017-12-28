// MessageSender.aidl
package com.assassin.muiltprocess;
import com.assassin.muiltprocess.MessageModel; 
import com.assassin.muiltprocess.MessageReceiver;
// Declare any non-default types here with import statements

interface MessageSender 
{
    void sendMessage(in MessageModel messageModel);
    void registerReciverListener(MessageReceiver messageReceiver);
    void unregisterReceiveListener(MessageReceiver messageReceiver);
}
