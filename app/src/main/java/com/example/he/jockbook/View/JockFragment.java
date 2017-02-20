package com.example.he.jockbook.View;

import android.os.Bundle;
import android.os.Handler;
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

import com.example.he.jockbook.Bean.JockItem;
import com.example.he.jockbook.R;
import com.example.he.jockbook.Utility.SharedPreferrenceHelper;
import com.example.he.jockbook.myApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示笑话
 * Created by he on 2017/2/19.
 */

public class JockFragment extends Fragment {
    private RecyclerView mRecycler;
    private static final String url = "http://japi.juhe.cn/joke/content/list.from?key=aea3530fe10ee7947528d76711ee7796&page=2&pagesize=10&sort=asc&time=1418745237";

    private JockAdapter adapter;
    private List<JockItem> mList = new ArrayList<>();
    private SwipeRefreshLayout mRefresh;

    private static final int OK = 1;
    private static final int REFRESH_OK=2;

    private static final String TAG="JockFragment";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OK:
                    adapter.notifyDataSetChanged();
                    break;
                case REFRESH_OK:
                    mList.set(0,new JockItem("xxx","ss"));
                    adapter.notifyDataSetChanged();
                    mRefresh.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        init();
        
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_layout_1, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRefresh= (SwipeRefreshLayout) view.findViewById(R.id.jock_refresh);
        mRefresh.setColorSchemeResources(R.color.colorPrimary);

        Log.d(TAG, "onCreateView: ");
        
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        init();
        adapter = new JockAdapter(mList);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(adapter);

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
    private void init() {
        final int size = SharedPreferrenceHelper.getJockSize(myApplication.getContext());

//        /**
//         * 从网络中获取数据
//         */
//        HttpUtil.sendOkHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Looper.prepare();
//                Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//
//                if (size != -1) {
//                    //读取数据
//                    if (size != -1) {
//                        for (int i = 0; i < size; i++) {
//                            String updateTime = SharedPreferrenceHelper.getJockUpdateTime(myApplication.getContext(), "" + i);
//                            String content = SharedPreferrenceHelper.getJockContent(myApplication.getContext(), "" + i);
//                            mList.add(new JockItem(content, updateTime));
//                        }
//
//                    }
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Utility.handleJock(response.body().string());
//            }
//        });
//
//        //读取数据
//        if (size != -1) {
//            for (int i = 0; i < size; i++) {
//                String updateTime = SharedPreferrenceHelper.getJockUpdateTime(myApplication.getContext(), "" + i);
//                String content = SharedPreferrenceHelper.getJockContent(myApplication.getContext(), "" + i);
//                mList.add(new JockItem(content, updateTime));
//
////                handler.sendEmptyMessage(OK);
//            }
//        }

        mList.add(new JockItem("111", "sssssssssss"));
        mList.add(new JockItem("111", "sssssssssss"));
        mList.add(new JockItem("111", "sssssssssss"));
        mList.add(new JockItem("111", "sssssssssss"));


    }

    /**
     * 更新消息数据
     */
    private void refreshJocks(){
            handler.sendEmptyMessageDelayed(REFRESH_OK,1000);
    }



}
