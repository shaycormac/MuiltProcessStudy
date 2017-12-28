package com.goldmantis.binderstudy.remoteServer;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.goldmantis.binderstudy.Book;
import com.goldmantis.binderstudy.IBookManager;
import com.goldmantis.binderstudy.INewBookArrivedInterface;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    public static final String TAG = "BookManagerService服务器";
    //使用多线程的容器
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    //感兴趣的用户(使用这个不行，必须使用RemoteCallBacklist)
    private CopyOnWriteArrayList<INewBookArrivedInterface> listeners = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<INewBookArrivedInterface> remoteCallbackList = new RemoteCallbackList<>();
    //原子操作
    private AtomicBoolean isServiceDestory = new AtomicBoolean(false);
    //权限校验
    public static final String Permission_Book = "com.goldmantis.binderstudy.permission.ACCESS_BOOK_SERVICE";

    public BookManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //可以在这里进行权限验证
        int check = checkCallingOrSelfPermission(Permission_Book);
        if (check== PackageManager.PERMISSION_DENIED)
        {
            return null;  
        }
           
        return bookManager;
    }


    //得到服务器的Binder
    private Binder bookManager = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            //将服务端的数据返回给客户端
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            //客户端要求插入一条数据
            mBookList.add(book);
        }

        @Override
        public void registerListener(INewBookArrivedInterface listener) throws RemoteException 
        {
            //就是在集合中加上或者减去
            /*if (!listeners.contains(listener))
            {
                listeners.add(listener);
            }else 
            {
                Log.i(TAG, "已经注册过了");
            }*/

            remoteCallbackList.register(listener);
          //  Log.i(TAG, "注册的个数：" + remoteCallbackList.beginBroadcast());

        }

        @Override
        public void unregisterListener(INewBookArrivedInterface listener) throws RemoteException 
        {

           /* if (listeners.contains(listener))
            {
                listeners.remove(listener);
            }else
            {
                Log.i(TAG, "没有注册过，不能解放");
            }*/
            remoteCallbackList.unregister(listener);
         //   Log.i(TAG, "解除注册的个数：" + remoteCallbackList.beginBroadcast());
        }
    };
    //创建的时候，先假设有了好几本书

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        //开启一个线程，新书生成
        new Thread(new AddBookRunnable()).start();
    }
    
    //新开一个线程，5秒钟就新添加一本书，并通知感兴趣的用户
    private class AddBookRunnable implements Runnable
    {
        @Override
        public void run()
        {
            //只要服务没有死
            while (!isServiceDestory.get())
            {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //新建一个Book
                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "新书*" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
            
            
        }
    }
    
    //通知所有注册的用户，新书到来
    private void onNewBookArrived(Book book) throws RemoteException 
    {
        mBookList.add(book);
        Log.i(TAG, "服务器新书来了，通知所有的订阅者：" + listeners.size());
        for (int i = 0; i < listeners.size(); i++) {
            INewBookArrivedInterface listener = listeners.get(i);
            listener.onNewBookArrived(book);
        }
        //规范写法
        int N = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < N; i++)
        {
            INewBookArrivedInterface listener = remoteCallbackList.getBroadcastItem(i);
            if (listener!=null)
            listener.onNewBookArrived(book);
        }
        //成对使用
        remoteCallbackList.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        //死了
        isServiceDestory.set(true);
        super.onDestroy();
    }
}
