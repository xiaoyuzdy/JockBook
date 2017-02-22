package com.example.he.jockbook.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.he.jockbook.Bean.ImageData;
import com.example.he.jockbook.Bean.ImageItem;
import com.example.he.jockbook.Constant.UrlConstants;
import com.example.he.jockbook.MainActivity;
import com.example.he.jockbook.R;
import com.example.he.jockbook.Utility.HttpUtil;
import com.example.he.jockbook.Utility.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**显示趣图
 * Created by he on 2017/2/20.
 */

public class ImageFragment  extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static  String URL= UrlConstants.NEWS_IMAGE_URL;
    private static final String TAG="ImageFragment";
    private static final int IMAGE_OK=2;
    public static boolean mRandom=false;


    private RecyclerView mRecycler;
    private ImageAdapter mAdapter;
    public SwipeRefreshLayout mRefresh;
    private List<ImageItem> mList=new ArrayList<>();

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IMAGE_OK :
                    mAdapter.notifyDataSetChanged();
                    mRefresh.setRefreshing(false);
                    mRecycler.smoothScrollToPosition(0);
                    MainActivity.canPress=true;
                break;
                default:
                    break;
            }
        }
    };




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.pager_layout_2,container,false);
        mRecycler= (RecyclerView) view.findViewById(R.id.image_recycle_view);
        mRefresh= (SwipeRefreshLayout) view.findViewById(R.id.image_refresh);
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(URL);
        mAdapter=new ImageAdapter(mList);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),1);
        mRefresh.setOnRefreshListener(this);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdapter);
    }


    /**
     * 初始化数据
     */
    private void init(String url){
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(),"服务器连接失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<ImageData> dataList=  Utility.handleImage(response.body().string());
                mList.clear();
                for(ImageData t:dataList){
                    mList.add(new ImageItem(t.url,t.content,t.updatetime));
                }
                mHandler.sendEmptyMessageDelayed(IMAGE_OK,1000);
            }
        });


//        mList.add(new ImageItem("http://img.juhe.cn/joke/201412/19/B0C3ABBEBBE0A6EA5B8FE04E27215FBC.gif","看看","1112222"));
//        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));
//        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));
//        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));

    }

    @Override
    public void onRefresh() {
        refreshImage();
    }


    /**
     * 刷新
     */
    private void refreshImage(){
        if (mRandom)
            init(UrlConstants.getImageRandomUrl());
        else {
            Log.d(TAG, "refreshImage: "+URL);
            init(URL);
        }
        mHandler.sendEmptyMessageDelayed(IMAGE_OK, 1000);
    }
}
