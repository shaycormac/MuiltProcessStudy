package com.assassin.muiltprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class MessageService extends Service {
    private static final String TAG = "MessageService";
    //
    private AtomicBoolean serviceStop = new AtomicBoolean(false);
    //RemoteCallbackList专门用来管理多进程回调接口
    private RemoteCallbackList<MessageReceiver> listenerList = new RemoteCallbackList<>();
    
    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return messageSender;
    }

    IBinder messageSender = new MessageSender.Stub() 
    {
        @Override
        public void sendMessage(MessageModel messageModel) throws RemoteException 
        {
            //这是从客户端得到发过来的消息。
            Log.d(TAG, "messageModel: " + messageModel.toString());

        }

        //这两个借口是远程客户端得到消息，返回去？
        @Override
        public void registerReciverListener(MessageReceiver messageReceiver) throws RemoteException
        {
            listenerList.register(messageReceiver);
            
        }

        @Override
        public void unregisterReceiveListener(MessageReceiver messageReceiver) throws RemoteException 
        {
            listenerList.unregister(messageReceiver);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new FakeTCPTask()).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        serviceStop.set(true);
        super.onDestroy();
    }


    //模拟长连接，通知客户端有新消息到达
    private class FakeTCPTask implements Runnable {
        @Override
        public void run() {
            while (!serviceStop.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageModel messageModel = new MessageModel();
                messageModel.setFrom("Service");
                messageModel.setTo("Client");
                messageModel.setContent(String.valueOf(System.currentTimeMillis()));
                /**
                 * RemoteCallbackList的遍历方式
                 * beginBroadcast和finishBroadcast一定要配对使用
                 */
                final int listenerCount = listenerList.beginBroadcast();
                Log.d(TAG, "listenerCount == " + listenerCount);
                for (int i = 0; i < listenerCount; i++) {
                    MessageReceiver messageReceiver = listenerList.getBroadcastItem(i);
                    if (messageReceiver != null) {
                        try {
                            messageReceiver.onMessageReceived(messageModel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                listenerList.finishBroadcast();
            }
        }
    }
}
