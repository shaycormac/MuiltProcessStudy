package com.goldmantis.binderstudy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.goldmantis.binderstudy.remoteServer.BookManagerService;

import java.util.List;

public class BookManagerClientActivity extends AppCompatActivity 
{
    //在客户端得到binder的引用
    private IBookManager iBookManager;

    /**
     * 静态变量的含义
     */
    public  final String TAG = this.getClass().getSimpleName();
    
    //当有新书到达时，服务端调用客户端的对象，由于是在客户端的BInder线程池中执行的，并不在主线程中，需要Handeler来切换线程
    /**
     * 静态变量的含义
     */
    public static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) 
        {
            switch(msg.what){
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i(TAG, "服务端调用客户端的方法，由于在客户端的BIndr线程池中运行，需要借助Handler来切换线程" + msg.obj);
                    break;
               
                default: 
                    super.handleMessage(msg);
            }
           
            
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager_client);
        //绑定服务端
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, bookManagerConnection, BIND_AUTO_CREATE);
    }


    //返回的
    private ServiceConnection bookManagerConnection = new ServiceConnection() 
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) 
        {
            iBookManager = IBookManager.Stub.asInterface(service);
            //得到这个远程的对象，便可以进行操作了。
            //假如这时我想那啥了，查询所有的图书，由于调用下面的方法，当前的线程会挂起
            //因此应该新开一个线程进行处理，因为你不知道远程的会怎么样，参照activity的生成过程，
            //通过handler进行进程通信  
            try {
                List<Book> bookList = iBookManager.getBookList();
                Log.i("TAG", "查询图书，得到集合的类型为：" + bookList.getClass().getCanonicalName());
                Log.i("TAG", "查询图书，" + bookList.toString());
                //注册监听新书到达的通知
                iBookManager.registerListener(arrivedInterface);
                
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    @Override
    protected void onDestroy() {
        //监听也要解除注册
        if (iBookManager!=null && iBookManager.asBinder().isBinderAlive())
        {
            try {
                iBookManager.unregisterListener(arrivedInterface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        //解除绑定
        unbindService(bookManagerConnection);
        super.onDestroy();
    }

    //新书到达的监听
    private INewBookArrivedInterface arrivedInterface = new INewBookArrivedInterface.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException 
        {
            //新书到达,通过handler来转发送消息
            Message message = Message.obtain();
            message.obj = book;
            message.what = MESSAGE_NEW_BOOK_ARRIVED;
            handler.sendMessage(message);
            

        }
    };
}
