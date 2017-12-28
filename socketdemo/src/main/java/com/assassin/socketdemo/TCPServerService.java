package com.assassin.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    public TCPServerService() {
    }


    private boolean mIsServiceDestoryed = false;
    private String[] mDefinedMessages = new String[] {
            "你好啊，哈哈",
            "请问你叫什么名字呀？",
            "今天北京天气不错啊，shy",
            "你知道吗？我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧：据说爱笑的人运气不会太差，不知道真假。"
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }
    
    
    //服务器
    private class TcpServer implements Runnable
    {

        @Override
        public void run() 
        {
            ServerSocket serverSocket = null;

            try {
                //搞个8688端口玩一玩
                serverSocket = new ServerSocket(8188);
            } catch (IOException e) 
            {
                System.err.println("establish tcp server failed, port:8888");
                e.printStackTrace();
                return;
            }
            
            //建立连接
            while (!mIsServiceDestoryed)
            {
                //接受客户端请求
                try {
                    final Socket client = serverSocket.accept();
                    //接受客户端的数据
                    new Thread(new Runnable() 
                    {
                        @Override
                        public void run() 
                        {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            

        }
    }

    private void responseClient(Socket client) throws IOException {
        //用于接收客户端的消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //向客户端发送消息
        PrintWriter out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(client.getOutputStream())), true);
        out.println("欢迎来到聊天室！");
        while (!mIsServiceDestoryed) {
            String str = in.readLine();
            System.out.println("msg from client:" + str);
            //todo  仅仅作为一个判断的标志。表示客户端已经不输入数据了
            if (str == null) 
            {
                break;
            }
            int i = new Random().nextInt(mDefinedMessages.length);
            String msg = mDefinedMessages[i];
            out.println(msg);
            System.out.println("send :" + msg);
        }
        System.out.println("client quit.");
        // 关闭流
        close(out);
        close(in);
        client.close();
        
        
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
