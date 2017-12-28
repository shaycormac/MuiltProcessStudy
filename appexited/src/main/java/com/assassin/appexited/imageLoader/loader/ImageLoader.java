package com.assassin.appexited.imageLoader.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.assassin.appexited.R;
import com.assassin.appexited.imageLoader.utils.MyUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/15 14:00
 * @version: 1.0
 * @description: 
 */

public class ImageLoader 
{
    /**
     * 静态变量的含义
     */
    public static final String TAG = "ImageLoader";
    /**
     * 网络读取流的默认大小
     */
    private static final int IO_BUFFER_SIZE = 8*1024;
    /**
     * 异步加载图片设置的标记
     */
    private static final int TAG_KEY_URI = R.id.imageloader_uri;
    /**
     *  异步加载图片发送的消息
     */
    private static final int MESSAGE_POST_RESULT = 1;
    /**
     * 内存缓存
     */
    private LruCache<String,Bitmap> mMemoryCache;
    /**
     *磁盘缓存
     */
    private DiskLruCache mDiskLruCache;

    private Context mContext;
/**
 * 磁盘的缓存大小，50m
 */
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
/**
 * 磁盘缓存是否建立了
 */
    private boolean mIsDiskLruCacheCreated = false;
/**
 *磁盘缓存open的节点设置为1，即一个节点只能有一个数据，所以这个静态常量直接设置为0即可。
 */
    private static final int DISK_CACHE_INDEX = 0;
  /**
   * bitmap的大小重新处理
   */
    private ImageResizer mImageResizer = new ImageResizer();


    /**
     * 线程池的配置
     */

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory threadFactory = new ThreadFactory()
    {
        /**
         * 原子操作，用来处理多线程同步，避免数据错乱的问题
         */
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r)
        {
            return new Thread(r,"ShayImageLoader--"+mCount.getAndIncrement());
        }
    };
/**
 * 屌了，线程池
 */
    private static final Executor Thread_Pool_Executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), threadFactory);

    private ImageLoader(Context context)
    {
        //防止内存泄露？？
        mContext = context.getApplicationContext();
        //设置最大的内存缓存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                
                return bitmap.getRowBytes()*bitmap.getHeight()/1024;
            }
        };
        
        //设置及外部磁盘路径
        File diskCacheDir = getDiskCacheDir(mContext, "shayImage_loader");
        //设置为目录
        if (!diskCacheDir.exists())
        {
            diskCacheDir.mkdirs();
        }
        
        //打开磁盘缓存
        //如果用户的磁盘空间不够了，磁盘缓存就会失败。
        if (getUsableSpace(diskCacheDir)>DISK_CACHE_SIZE)
        {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                //
                mIsDiskLruCacheCreated=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }

    /**
     * 对外构造者，创建出一个ImageLoader.
     * @param context
     * @return
     */
     public static  ImageLoader build(Context context)
     {
         return new ImageLoader(context);
     }

    /**
     * 同步加载图片，要求外部线程调用这个方法，不能在主线程中调用
     * 先从内存缓存取，得不到从磁盘，再得不到，从网络取
     * @param uri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
      public Bitmap loadBitmap(String uri,int reqWidth,int reqHeight)
      {
          Bitmap bitmap = loadBitmapFromMemCache(uri);
          if (bitmap!=null)
          {
              Log.d(TAG, "getBitmapFromMemCache,url:" + uri); 
              return bitmap;
          }

          try {
              bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
              if (bitmap != null) 
              {
                  Log.d(TAG, "loadBitmapFromDisk,url:" + uri);
                  return bitmap;
              }
              //还不行的话，就从网络取(并储存在硬盘中)
              bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
              Log.d(TAG, "loadBitmapFromHttp,url:" + uri);
          } catch (IOException e) {
              e.printStackTrace();
          }
          
          //如果还不行的话（从网络中取后直接加到内存中，没有磁盘缓存一说了）
          if (bitmap == null && !mIsDiskLruCacheCreated)
          {
              Log.w(TAG, "encounter error, DiskLruCache is not created.");
              bitmap = downloadBitmapFromUrl(uri);
              
          }

          return bitmap;

      }

    /**
     *  异步加载图片。
     * @param uri
     * @param imageView
     */
    public void bindBitmap(final String uri, final ImageView imageView) 
    {
        bindBitmap(uri, imageView, 0, 0);
    }
      

    /**
     * 异步加载图片，内部使用子线程来处理图片的加载
     * @param uri
     * @param imageView
     * @param reqWidth
     * @param reqHeight
     */
      public void bindBitmap(final String uri, final ImageView imageView, final int reqWidth, final int reqHeight)
      {
          //imageview设置标记
          imageView.setTag(TAG_KEY_URI, uri);
          //首先从内存缓存中取，有就直接加载
          Bitmap bitmap = loadBitmapFromMemCache(uri);
          if (bitmap != null) {
              imageView.setImageBitmap(bitmap);
              return;
          }
          
          //否则开启一个异步线程进行加载
          Runnable loadBitmapTask = new Runnable() {
              @Override
              public void run() 
              {
                  Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                  if (bitmap!=null)
                  {
                      //得到结果，并通过handler发送出去
                      LoaderResult result = new LoaderResult(imageView, uri, bitmap);
                      //发送出去
                      mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                  }

              }
          };
          
          //屌屌屌，放在线程池中使用
          Thread_Pool_Executor.execute(loadBitmapTask);
      }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) 
        {
            if (msg.what==MESSAGE_POST_RESULT)
            {
                LoaderResult result = (LoaderResult) msg.obj;
                ImageView imageView = result.imageView;
                //得到标签的值，如果是，就进行绑定，不是，就不绑定
                String uri = (String) imageView.getTag(TAG_KEY_URI);
                //重点，标签错落的解决方案
                if (uri.equals(result.uri))
                {
                    imageView.setImageBitmap(result.bitmap);
                } else {
                    Log.w(TAG, "set image bitmap,but url has changed, ignored!");
                }
                
            }
            
            
        }
    };

    /**
     * 直接从网络上下载图片，不进行裁剪了，也没法裁剪，原因在于只要执行这个方法时，说明磁盘无法使用，
     * 就没法把流的标识符识别出来？？？？（疑问！！）
     * @param urlString
     * @return
     */
    private Bitmap downloadBitmapFromUrl(String urlString) 
    {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        final URL url;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            //直接读取
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Log.e(TAG, "Error in downloadBitmap: " + e);
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            MyUtils.close(in);
        }
        return bitmap;
    }

    /**
     * 获取磁盘缓存的路径，先判断，没有挂载在外部储存的话，就用内部的缓存
     * @param context
     * @param uniqueName
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName)
    {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        
        final  String cachePath;
        if (externalStorageAvailable)
        {
            cachePath = context.getExternalCacheDir().getPath();
        }else 
        {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);  
    }

    /**
     * 当前文件夹的可用空间大小
     * @param path
     * @return
     */
    private  long getUsableSpace(File path)
    {
        return path.getUsableSpace();
        
    }

    /**
     * 内存缓存的添加
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key,Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中取数据
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key)
    {
        return mMemoryCache.get(key);
    }

    /**
     * 通过key的编码后的读取内存缓存
     * @param url
     * @return
     */
    private Bitmap loadBitmapFromMemCache(String url)
    {
        String key =hashKeyFromUrl(url);
        Bitmap bitmap = getBitmapFromMemCache(key);
        return bitmap;
        
    }

    /**
     * 从网络中读出流，把流在磁盘缓存中变为bitmap，并返回出去
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromHttp(String url,int reqWidth,int reqHeight) throws IOException {
        //校验这个方法，必须在子线程中执行
        if (Looper.myLooper()==Looper.getMainLooper())
        {
            throw new RuntimeException("不能在主线程进行网络访问");
        }

        //磁盘不可用的话，就返回null
        if (mDiskLruCache == null) {
            return null;
        }
        
        //以网络访问的url为key,先进行md5转码
        String key = hashKeyFromUrl(url);
        //打开磁盘缓存的编辑器
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor!=null)
        {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            //下载文件成功，就储存，否则放弃
            if (downloadUrlToStream(url,outputStream))
            {
                editor.commit();
                
            }else 
            {
                editor.abort();
            }
            //这句话不能忘记了
            mDiskLruCache.flush();
        }

        return loadBitmapFromDiskCache(url,reqWidth,reqHeight);
        
    }

    /**
     * 将网络流放入到磁盘缓存的写流中去
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) 
    {
        //最基本的网络流
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
            
        }  catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "downloadBitmap failed." + e);
        }finally {
            //关闭资源
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            MyUtils.close(out);
            MyUtils.close(in);
        }

        return false;
    }

    /**
     * 从磁盘缓存中取出
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) throws IOException {
        //还是建议放到子线程去使用
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread, it's not recommended!建议在子线程中去加载");
        } 
        
        if (mDiskLruCache==null)
        {
            return null;
        }
        
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(url);
        //得到磁盘的文件流
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null)
        {
            //得到文件流
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            //得到文件描述符
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            //工具类对bitmap进行处理
            bitmap = mImageResizer.decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
            //处理过的bitmap储存在内存缓存中
            if (bitmap!=null)
            {
                addBitmapToMemoryCache(key, bitmap);
            }
            
        }
        return bitmap;
    }

    /**
     * 对url进行md5编码
     * @param url
     * @return
     */
    private String hashKeyFromUrl(String url)
    {
        String cacheKey = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            //转码
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
            //转码失败后就以url的hashCode为值
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    /**
     * 对字节码进行转码
     * @param bytes
     * @return
     */
    private String bytesToHexString(byte[] bytes) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 异步返回的数据包装出去
     */
    private static class LoaderResult {
        ImageView imageView;
        String uri;
        Bitmap bitmap;

        public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
