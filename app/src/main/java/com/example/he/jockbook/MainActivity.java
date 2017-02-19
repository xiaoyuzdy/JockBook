package com.example.he.jockbook;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.he.jockbook.Utility.HideStatusBar;
import com.example.he.jockbook.Utility.HttpUtil;
import com.example.he.jockbook.Utility.SharedPreferrenceHelper;
import com.example.he.jockbook.Utility.imageloader.loader.ImageLoader;
import com.example.he.jockbook.Utility.imageloader.loader.ImageResizer;
import com.example.he.jockbook.Utility.imageloader.utils.MyUtils;
import com.example.he.jockbook.View.JockFragment;
import com.example.he.jockbook.View.MyViewPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.baselitch.widget.bannerholder.BannerClickListenenr;
import top.baselitch.widget.bannerholder.BannerHolderView;
import top.baselitch.widget.bannerholder.HolderAttr;

public class MainActivity extends AppCompatActivity {

    private static final String url = "http://japi.juhe.cn/joke/content/list.from?key=aea3530fe10ee7947528d76711ee7796&page=2&pagesize=10&sort=asc&time=1418745237";
    private static final String TAG = "MainActivity";
    //获取每日一图地址的URL
    private static final String BING_URL = "http://guolin.tech/api/bing_pic";
    private static final int OK = 1;


    private Bitmap mBitmap;
    private BannerHolderView mHolder;
    private ImageLoader mImageLoader;
    private int mWidth;
    private int mHeight;
    private List<Bitmap> mList = new ArrayList<>();
    private ProgressBar mBar;

    private ViewPager mViewPager;
    private PagerTabStrip mPagerTab;
    private List<Fragment> mView;
    private List<String> mTitle;



    private Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==OK){
                initHolder();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HideStatusBar.hide(this);
//        HttpUtil.sendOkHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Looper.prepare();
//                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Utility.handleJock(response.body().string());
//            }
//        });


        mHolder = (BannerHolderView) findViewById(R.id.banner_holder);
        mBar= (ProgressBar) findViewById(R.id.pb);
        HolderAttr.Builder builder = mHolder.getHolerAttr();//获取Holder配置参数构建对象
        builder.setSwitchDuration(900)//设置切换Banner的持续时间
                .setAutoLooper(true)//开启自动轮播
                .setLooperTime(2000)//设置轮播间隔时间
                .setBannerClickListenenr(new BannerClickListenenr() {//Banner图片点击事件
                    @Override
                    public void onBannerClick(int p) {
                        //p: 页面索引
                    }
                });

        mHolder.setHolerAttr(builder);
        mWidth = MyUtils.getScreenMetrics(this).widthPixels;
        mHeight = (int) MyUtils.dp2px(this, (float) 120);
        mImageLoader = ImageLoader.build(this);

        final String url = SharedPreferrenceHelper.getBingPicUrl(this);
        if (url != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBitmap = mImageLoader.loadBitmap(url, mWidth, mHeight);
                }
            }).start();
        } else {
            loadImage(BING_URL);
        }
        h.sendEmptyMessageDelayed(OK,2000);
        //ViewPage初始化
        initView();



    }


    /**
     * 加载图片，先获取Bing每天一图的地址再根据地址从网上获取图片
     *
     * @param url
     */
    private void loadImage(String url) {
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + "获取每日一图地址失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String urlImage = response.body().string();
                SharedPreferrenceHelper.saveBingPicUrl(MainActivity.this, urlImage);
                mBitmap = mImageLoader.loadBitmap(urlImage, mWidth, mHeight);
            }
        });
    }

    /**
     * 初始化广告栏
     */
    private void initHolder() {
        ImageResizer i = new ImageResizer();
        Bitmap b = i.decodeSampledBitmapFromResoures(getResources(), R.drawable.holder_default_bj, mWidth, mHeight);
        Bitmap c = i.decodeSampledBitmapFromResoures(getResources(), R.drawable.holder_default_bj2, mWidth, mHeight);
        Bitmap d = i.decodeSampledBitmapFromResoures(getResources(), R.drawable.image_default, mWidth, mHeight);
        mList.add(b);
        mList.add(c);
        if(mBitmap!=null){
            mList.add(mBitmap);
        }else{
            mList.add(d);
        }
        mBar.setVisibility(View.GONE);
        //设置图片集合
        mHolder.setHolderBitmaps(mList);
        Log.d(TAG, "initHolder: ");
    }


    /**
     * ViewPager 初始化
     */
    private void  initView(){
        mViewPager= (ViewPager) findViewById(R.id.view_pager);
        mPagerTab= (PagerTabStrip) findViewById(R.id.page_tab);
        mPagerTab.setTabIndicatorColor(Color.RED);
        mPagerTab.setDrawFullUnderline(false);
        mPagerTab.setTextSpacing(0);

        mView=new ArrayList<>();
        mTitle=new ArrayList<>();

        mView.add(new JockFragment());
        mView.add(new JockFragment());
        mView.add(new JockFragment());

        mTitle.add("笑话");
        mTitle.add("趣图");
        mTitle.add("其他");

        MyViewPagerAdapter adapter=new MyViewPagerAdapter(getSupportFragmentManager(),mView,mTitle);

        mViewPager.setAdapter(adapter);

    }


//    @Override
//    protected void onResume() {
//        super.onResume();
////            initHolder();
//    }
}
