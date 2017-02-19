package com.example.he.jockbook.View;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.he.jockbook.Bean.JockItem;
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
 * Created by he on 2017/2/19.
 */

public class JockFragment extends Fragment {
    private RecyclerView mRecycler;
    private TextView mTime;
    private TextView mContent;
    private static final String url = "http://japi.juhe.cn/joke/content/list.from?key=aea3530fe10ee7947528d76711ee7796&page=2&pagesize=10&sort=asc&time=1418745237";

    private JockAdapter adapter;
    private List<JockItem> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_layout_1, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycle_view);
//        mTime = (TextView) view.findViewById(R.id.jock_updateTime);
//        mContent = (TextView) view.findViewById(R.id.jock_content);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        adapter = new JockAdapter(mList);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(adapter);
    }

    private void init() {
        /**
         * 从网络中获取数据
         */
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Utility.handleJock(response.body().string());
            }
        });



        mList.add(new JockItem("222", "sssssssssssssssssssss"));
        mList.add(new JockItem("222", "sssssssssssssssssssss"));
        mList.add(new JockItem("222", "sssssssssssssssssssss"));
        mList.add(new JockItem("222", "sssssssssssssssssssss"));
        mList.add(new JockItem("222", "sssssssssssssssssssss"));


    }


}
