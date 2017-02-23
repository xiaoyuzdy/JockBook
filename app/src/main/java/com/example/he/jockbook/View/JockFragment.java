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

import com.example.he.jockbook.Bean.JockData;
import com.example.he.jockbook.Bean.JockItem;
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

/**
 * 显示笑话
 * Created by he on 2017/2/19.
 */

public class JockFragment extends Fragment {
    private RecyclerView mRecycler;
    public static String URL = UrlConstants.NEWS_JOCK_URL;
    public static boolean mRandom = false;


    private JockAdapter mAdapter;
    private List<JockItem> mList = new ArrayList<>();
    public SwipeRefreshLayout mRefresh;

    private static final int JOCK_OK = 1;

    private static final String TAG = "JockFragment";


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JOCK_OK:
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
        View view = inflater.inflate(R.layout.pager_layout_1, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.jock_refresh);
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        Log.d(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        initByHttp(URL);

        initByHttp(UrlConstants.NEWS_JOCK_URL);

        mAdapter = new JockAdapter(mList);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdapter);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshJocks();
            }
        });
    }


    /**
     * 初始化数据
     */
    private void initByHttp(String url) {
        /**
         * 从网络中获取数据
         */
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<JockData> dataList = Utility.handleJock(response.body().string());
                mList.clear();
                Log.d(TAG, "onResponse: " + dataList.size());
                for (JockData data : dataList) {
                    mList.add(new JockItem(data.content, data.updatetime));
                    Log.d(TAG, "onResponse: " + data.content);
                }
                handler.sendEmptyMessageDelayed(JOCK_OK, 1000);
            }
        });

//        mList.add(new JockItem("111", "sssssssssss"));
//        mList.add(new JockItem("111", "sssssssssss"));
//        mList.add(new JockItem("111", "sssssssssss"));
//        mList.add(new JockItem("111", "sssssssssss"));

    }

    /**
     * 更新消息数据
     */
    public void refreshJocks() {
        if (mRandom){
            initByHttp(UrlConstants.getJockRandomUrl());
        }
        else {
            initByHttp(UrlConstants.NEWS_JOCK_URL);
        }
        handler.sendEmptyMessageDelayed(JOCK_OK, 1000);
    }


}
