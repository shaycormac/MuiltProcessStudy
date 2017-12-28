package com.assassin.muiltprocessstudy.muiltMessenger;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class MessengerService extends Service 
{
    //jvm预加载，常量池中，外部类调用这两个常量，不会初始化这个类。
    public static final int GET_DATA = 1;
    public static final int SET_DATA = 2;
    /**
     * 向客户端发送的消息
     */
    Messenger replyMessenger;
    /**
     * 服务端发送的数据
     */
    Messenger messenger = new Messenger(new ServiceHandler());
    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //获取数据
        return messenger.getBinder();
        // TODO: Return the communication channel to the service.
      //  throw new UnsupportedOperationException("Not yet implemented");
    }
    
    //需要一个Handler来支持
    class ServiceHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) 
        {
            //得到消息
            replyMessenger = msg.replyTo;
            //super.handleMessage(msg);
            switch(msg.what){
                case GET_DATA:
                    //客服端向服务端请求数据
                    
                    break;
                default:break;
            }
        }
    }
}
