package com.assassin.muiltprocess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity 
{

    private static final String TAG = "测试服务连接";

    //获取要发送的对象
    private MessageSender messageSender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupService();
    }

    /**
     * bindService & startService 同时使用。
     */
    private void setupService() 
    {
        //连接
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
        
        
    }

    @Override
    protected void onDestroy() {
        //解除消息监听接口
        if(messageSender!=null && messageSender.asBinder().isBinderAlive())
        {
            try {
                messageSender.registerReciverListener(messageReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }
    
    //消息监听回调借口
    private MessageReceiver messageReceiver = new MessageReceiver.Stub() 
    {
        @Override
        public void onMessageReceived(MessageModel receivedMessage) throws RemoteException
        {
            Log.d(TAG, "onMessageReceived: " + receivedMessage.toString());
        }
    };

    //连接条件
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        //IBinder iBinder 是一切的基础，得到服务端在Binder里面的代理binder对象。
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            Log.d(TAG, "onServiceConnected");
            //设置Binder死亡监听
            try {
                messageSender.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) 
            {
                e.printStackTrace();
            }
            //使用asInterface方法取得AIDL对应的操作接口
            messageSender = MessageSender.Stub.asInterface(iBinder);
            //生成消息对象
            MessageModel messageModel = new MessageModel();
            messageModel.setFrom("client user id");
            messageModel.setTo("receiver user id");
            messageModel.setContent("This is message content");
            //调用远程Service的sendMessage方法，并传递消息实体对象
            try {
                //把接收消息的回调接口注册到服务端
                messageSender.registerReciverListener(messageReceiver);
                //调用远程Service的sendMessage方法，并传递消息实体对象
                messageSender.sendMessage(messageModel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) 
        {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() 
        {
            Log.d(TAG, "binderDied");
            if (messageSender!=null)
            {
                messageSender.asBinder().unlinkToDeath(this, 0);
                messageSender = null;
            }
            ////  重连服务或其他操作
            setupService();
            

        }
    };
}
