package com.assassin.appexited.imageLoader.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.assassin.appexited.R;
import com.assassin.appexited.imageLoader.loader.ImageLoader;
import com.assassin.appexited.imageLoader.ui.SquareImageView;
import com.assassin.appexited.imageLoader.utils.MyUtils;
import com.assassin.appexited.util.permission.CompatPermissionManager;
import com.assassin.appexited.util.permission.PermissionEnum;
import com.assassin.appexited.util.permission.PermissionsCallback;
import com.assassin.appexited.widget.powerfulRVAdapter.CommonAdapter;
import com.assassin.appexited.widget.powerfulRVAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试编写的三级缓存图片
 */
public class ImageLoaderActivity extends AppCompatActivity 
{

    private static final String TAG = "MainActivity";

    private List<String> mUrList = new ArrayList<String>();

    private int mImageWidth = 0;
    
    private RecyclerView rcvImageLoader;

    ImageLoader imageLoader;

    CommonAdapter<String> commonAdapter;


    private boolean mIsGridViewIdle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);
        initData();
        initView();
        imageLoader = ImageLoader.build(this);
        requestPermission();
        
    }

    private void initView() {
        rcvImageLoader = findViewById(R.id.rcv_image_loader);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        rcvImageLoader.setLayoutManager(gridLayoutManager);
        rcvImageLoader.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                return false;
            }
        });
        
        rcvImageLoader.addOnScrollListener(new RecyclerView.OnScrollListener() 
        {
            private boolean isUp = false;

            //当前RecyclerView显示出来的最后一个的item的position
            int lastPosition = -1;
            int firstPositon = -1;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) 
            {
                super.onScrollStateChanged(recyclerView, newState);
                
                if (newState== RecyclerView.SCROLL_STATE_IDLE)
                {
                    lastPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    //是否到顶部
                    firstPositon = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                    
                    //是否在底部
                    if (lastPosition==mUrList.size()-1 && isUp)
                    {
                        Toast.makeText(ImageLoaderActivity.this, "滑动到底部了", Toast.LENGTH_SHORT).show();
                        return;
                        
                    }

                    if (firstPositon==0 && !isUp)
                    {
                        Toast.makeText(ImageLoaderActivity.this, "滑动到顶部了", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    mIsGridViewIdle = true;
                    commonAdapter.notifyDataSetChanged();
                }else 
                {
                    mIsGridViewIdle = false;
                }
                
                
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: dy的值--" +dy);
                //开始滑动了
                if (dy >0)
                {
                    //向上滑动
                    isUp = true;
                }else 
                {
                    isUp = false;
                }
            }
        });
    }

    private void requestPermission()
    {
        CompatPermissionManager.INSTANCE.with(this)
                .tag(1000)
                .permission(PermissionEnum.READ_EXTERNAL_STORAGE,PermissionEnum.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionsCallback() {
                    @Override
                    public void onGranted(ArrayList<PermissionEnum> grantedList) 
                    {
                        initRecyclerView();
                    }

                    @Override
                    public void onDenied(ArrayList<PermissionEnum> deniedList) 
                    {

                    }
                }).checkAsk();
        
    }

    private void initRecyclerView() {
        commonAdapter=new CommonAdapter<String>(ImageLoaderActivity.this, R.layout.recyclerview__image_layout, mUrList) 
        {
            private Drawable mDefaultBitmapDrawable=getResources().getDrawable(R.mipmap.ic_launcher);

            @Override
            public void convert(ViewHolder holder, String url)
            {
                SquareImageView imageView = holder.getView(R.id.img_item_square);
                //使用imageLoder进行加载
                String tag = (String) imageView.getTag();
                if (!url.equals(tag))
                {
                    imageView.setImageDrawable(mDefaultBitmapDrawable);
                }
                if (mIsGridViewIdle) 
                {
                    imageView.setTag(url);
                    imageLoader.bindBitmap(url, imageView, mImageWidth, mImageWidth);
                }
                
                
            }
        };
        rcvImageLoader.setAdapter(commonAdapter);
    }

    private void initData() {
        String[] imageUrls = {
                "http://b.hiphotos.baidu.com/zhidao/pic/item/a6efce1b9d16fdfafee0cfb5b68f8c5495ee7bd8.jpg",
                "http://pic47.nipic.com/20140830/7487939_180041822000_2.jpg",
                "http://pic41.nipic.com/20140518/4135003_102912523000_2.jpg",
                "http://img2.imgtn.bdimg.com/it/u=1133260524,1171054226&fm=21&gp=0.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/3b87e950352ac65c0f1f6e9efff2b21192138ac0.jpg",
                "http://pic42.nipic.com/20140618/9448607_210533564001_2.jpg",
                "http://pic10.nipic.com/20101027/3578782_201643041706_2.jpg",
                "http://picview01.baomihua.com/photos/20120805/m_14_634797817549375000_37810757.jpg",
                "http://img2.3lian.com/2014/c7/51/d/26.jpg",
                "http://img3.3lian.com/2013/c1/34/d/93.jpg",
                "http://b.zol-img.com.cn/desk/bizhi/image/3/960x600/1375841395686.jpg",
                "http://picview01.baomihua.com/photos/20120917/m_14_634834710114218750_41852580.jpg",
                "http://cdn.duitang.com/uploads/item/201311/03/20131103171224_rr2aL.jpeg",
                "http://imgrt.pconline.com.cn/images/upload/upc/tx/wallpaper/1210/17/c1/spcgroup/14468225_1350443478079_1680x1050.jpg",
                "http://pic41.nipic.com/20140518/4135003_102025858000_2.jpg",
                "http://www.1tong.com/uploads/wallpaper/landscapes/200-4-730x456.jpg",
                "http://pic.58pic.com/58pic/13/00/22/32M58PICV6U.jpg",
                "http://picview01.baomihua.com/photos/20120629/m_14_634765948339062500_11778706.jpg",
                "http://h.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=429e7b1b92ef76c6d087f32fa826d1cc/7acb0a46f21fbe09cc206a2e69600c338744ad8a.jpg",
                "http://pica.nipic.com/2007-12-21/2007122115114908_2.jpg",
                "http://cdn.duitang.com/uploads/item/201405/13/20140513212305_XcKLG.jpeg",
                "http://photo.loveyd.com/uploads/allimg/080618/1110324.jpg",
                "http://img4.duitang.com/uploads/item/201404/17/20140417105820_GuEHe.thumb.700_0.jpeg",
                "http://cdn.duitang.com/uploads/item/201204/21/20120421155228_i52eX.thumb.600_0.jpeg",
                "http://img4.duitang.com/uploads/item/201404/17/20140417105856_LTayu.thumb.700_0.jpeg",
                "http://img04.tooopen.com/images/20130723/tooopen_20530699.jpg",
                "http://www.qjis.com/uploads/allimg/120612/1131352Y2-16.jpg",
                "http://pic.dbw.cn/0/01/33/59/1335968_847719.jpg",
                "http://a.hiphotos.baidu.com/image/pic/item/a8773912b31bb051a862339c337adab44bede0c4.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0feeea8a30f5e6034a85edf720f.jpg",
                "http://img0.pconline.com.cn/pconline/bizi/desktop/1412/ER2.jpg",
                "http://pic.58pic.com/58pic/11/25/04/91v58PIC6Xy.jpg",
                "http://img3.3lian.com/2013/c2/32/d/101.jpg",
                "http://pic25.nipic.com/20121210/7447430_172514301000_2.jpg",
                "http://img02.tooopen.com/images/20140320/sy_57121781945.jpg",
                "http://www.renyugang.cn/emlog/content/plugins/kl_album/upload/201004/852706aad6df6cd839f1211c358f2812201004120651068641.jpg"
        };
        for (String url : imageUrls) {
            mUrList.add(url);
        }
        int screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
        int space = (int)MyUtils.dp2px(this, 20f);
       // mImageWidth = (screenWidth - space) / 3;
        mImageWidth = screenWidth / 3;
        /*mIsWifi = MyUtils.isWifi(this);
        if (mIsWifi) {
            mCanGetBitmapFromNetWork = true;
        }*/
    }
}
