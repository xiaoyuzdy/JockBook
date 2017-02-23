package com.example.he.jockbook;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.he.jockbook.Constant.UrlConstants;
import com.example.he.jockbook.Utility.HideStatusBar;
import com.example.he.jockbook.Utility.HttpUtil;
import com.example.he.jockbook.Utility.SharedPreferrenceHelper;
import com.example.he.jockbook.View.ImageFragment;
import com.example.he.jockbook.View.JockFragment;
import com.example.he.jockbook.View.MyViewPagerAdapter;
import com.example.he.jockbook.View.OtherFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.baselitch.widget.bannerholder.BannerClickListenenr;
import top.baselitch.widget.bannerholder.BannerHolderView;
import top.baselitch.widget.bannerholder.HolderAttr;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //获取每日一图地址的URL
    private static final String BING_URL = "http://guolin.tech/api/bing_pic";
    private static final int BANNER_HOLDER_OK = 0;

    //用于防治用户频繁按底部导航栏导致的Crash
    public static boolean canPress = true;


    private Bitmap mBitmap;
    private BannerHolderView mHolder;
    //    private ImageLoader mImageLoader;
//    private int mWidth;
//    private int mHeight;
    private List<Bitmap> mList = new ArrayList<>();
    private ProgressBar mBar;
    private BottomNavigationBar mButtonBar;


    private ViewPager mViewPager;
    private MyViewPagerAdapter mAdapter;
    private PagerTabStrip mPagerTab;
    private List<Fragment> mView;
    private List<String> mTitle;


    //Fragment
    private JockFragment mJock;
    private ImageFragment mImage;
    private OtherFragment mOther;


    //线程池需要的一些参数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "" + mCount.getAndIncrement());
        }
    };
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), sThreadFactory);


    public Handler h = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BANNER_HOLDER_OK:
                    mBar.setVisibility(View.GONE);
                    mHolder.setHolderBitmaps(mList);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HideStatusBar.hide(this);
        Log.d(TAG, "线程  " + Thread.currentThread());
        mHolder = (BannerHolderView) findViewById(R.id.banner_holder);
        mBar = (ProgressBar) findViewById(R.id.pb);
        mButtonBar = (BottomNavigationBar) findViewById(R.id.navigation_bar);

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
//        mWidth = MyUtils.getScreenMetrics(this).widthPixels;
//        mHeight = (int) MyUtils.dp2px(this, (float) 120);
//        mImageLoader = ImageLoader.build(this);

        //ViewPage初始化
        initView();
        //顶部广告栏初始化
        initHolder();
        //底部导航栏初始化
        initBottomBar();

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
                Log.d(TAG, "线程  " + Thread.currentThread());
                final String urlImage = response.body().string();
                SharedPreferrenceHelper.saveBingPicUrl(MainActivity.this, urlImage);
                try {
                    Bitmap b = Glide.with(MainActivity.this).load(R.drawable.holder_default_bj).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    Bitmap c = Glide.with(MainActivity.this).load(R.drawable.holder_default_bj2).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    Bitmap d = Glide.with(MainActivity.this).load(R.drawable.image_default).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    mBitmap = Glide.with(MainActivity.this).load(urlImage).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    mList.add(b);
                    mList.add(c);
                    if (mBitmap != null)
                        mList.add(mBitmap);
                    else {
                        mList.add(d);
                    }
                    h.sendEmptyMessageDelayed(BANNER_HOLDER_OK, 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化广告栏
     */
    private void initHolder() {
        final String url = SharedPreferrenceHelper.getBingPicUrl(this);
        if (url != null) {
            Runnable loadBitmapFromLocal = new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap b = Glide.with(MainActivity.this).load(R.drawable.holder_default_bj).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        Bitmap c = Glide.with(MainActivity.this).load(R.drawable.holder_default_bj2).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        Bitmap d = Glide.with(MainActivity.this).load(R.drawable.image_default).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        mBitmap = Glide.with(MainActivity.this).load(url).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        mList.add(b);
                        mList.add(c);
                        if (mBitmap != null)
                            mList.add(mBitmap);
                        else {
                            mList.add(d);
                        }
                        h.sendEmptyMessageDelayed(BANNER_HOLDER_OK, 1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            };
            THREAD_POOL_EXECUTOR.execute(loadBitmapFromLocal);
            Log.d(TAG, "initHolder: ");
        } else {
            loadImage(BING_URL);
        }
    }


    /**
     * ViewPager 初始化
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerTab = (PagerTabStrip) findViewById(R.id.page_tab);
        mPagerTab.setTabIndicatorColor(Color.RED);
        mPagerTab.setDrawFullUnderline(false);
        mPagerTab.setTextSpacing(0);

        mView = new ArrayList<>();
        mTitle = new ArrayList<>();

        mJock = new JockFragment();
        mImage = new ImageFragment();
        mOther = new OtherFragment();

        mView.add(mJock);
        mView.add(mImage);
        mView.add(mOther);

        mTitle.add("笑话");
        mTitle.add("趣图");
        mTitle.add("其他");

        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mView, mTitle);
        mViewPager.setAdapter(mAdapter);

    }


    /**
     * 初始化底部导航栏
     */
    private void initBottomBar() {
        //添加选项
        mButtonBar.addItem(new BottomNavigationItem(R.drawable.news_unselect, "最新"))
                .addItem(new BottomNavigationItem(R.drawable.random_unselect, "随机"))
                .addItem(new BottomNavigationItem(R.drawable.me_unselect, "我"))
                .initialise();

        //设置点击事件
        mButtonBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            //未选中-->选中
            @Override
            public void onTabSelected(int position) {

                if (canPress) {
                    canPress = false;
                    switch (position) {
                        //最新
                        case 0:
                            //笑话
                            if (getViewPagerItem() == 0) {
                                //显示刷新状态并刷新数据
                                mJock.mRefresh.setRefreshing(true);
                                mJock.mRandom = false;
                                mJock.URL = UrlConstants.NEWS_JOCK_URL;
                                mJock.refreshJocks();
                            }
                            //趣图
                            else if (getViewPagerItem() == 1) {
                                mImage.mRefresh.setRefreshing(true);
                                mImage.mRandom = false;
                                mImage.URL = UrlConstants.NEWS_IMAGE_URL;
                                mImage.onRefresh();

                            }
                            //其他
                            else if (getViewPagerItem() == 2) {

                            }
                            break;

                        //随机
                        case 1:
                            //笑话
                            if (getViewPagerItem() == 0) {
                                //显示刷新状态并刷新数据
                                mJock.mRefresh.setRefreshing(true);
                                mJock.mRandom = true;
                                mJock.refreshJocks();
                            }
                            //趣图
                            else if (getViewPagerItem() == 1) {
                                mImage.mRefresh.setRefreshing(true);
                                mImage.mRandom = true;
                                mImage.onRefresh();
                            }
                            //其他
                            else if (getViewPagerItem() == 2) {

                            }
                            break;
                        //我
                        case 2:
                            mViewPager.setCurrentItem(2);
                            canPress = true;
                            break;

                        default:
                            break;
                    }
                }
//                canPress=false;
            }

            //选中-->未选中
            @Override
            public void onTabUnselected(int position) {

            }

            //选中-->选中
            @Override
            public void onTabReselected(int position) {

                if (canPress) {
                    canPress = false;
                    switch (position) {
                        //最新
                        case 0:
                            //笑话
                            if (getViewPagerItem() == 0) {
                                //显示刷新状态并刷新数据
                                mJock.mRefresh.setRefreshing(true);
                                mJock.mRandom = false;
//                                mJock.URL = UrlConstants.NEWS_JOCK_URL;
                                mJock.refreshJocks();
                            }
                            //趣图
                            else if (getViewPagerItem() == 1) {
                                mImage.mRefresh.setRefreshing(true);
                                mImage.mRandom = false;
                                mImage.URL = UrlConstants.NEWS_IMAGE_URL;
                                mImage.onRefresh();
                            }
                            //其他
                            else if (getViewPagerItem() == 2) {

                            }
                            break;

                        //随机
                        case 1:
                            //笑话
                            if (getViewPagerItem() == 0) {
                                //显示刷新状态并刷新数据
                                mJock.mRefresh.setRefreshing(true);
                                mJock.mRandom = true;
                                mJock.refreshJocks();
                            }
                            //趣图
                            else if (getViewPagerItem() == 1) {
                                mImage.mRefresh.setRefreshing(true);
                                mImage.mRandom = true;
                                mImage.onRefresh();
                            }
                            //其他
                            else if (getViewPagerItem() == 2) {

                            }
                            break;
                        //我
                        case 2:
                            mViewPager.setCurrentItem(2);
                            canPress = true;
                            break;

                        default:
                            break;
                    }
                }
//                canPress = false;
            }
        });


    }


    /**
     * 获取当前ViewPager中Fragment的index
     *
     * @return
     */
    private int getViewPagerItem() {
        return mViewPager.getCurrentItem();
    }


}
