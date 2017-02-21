package com.example.he.jockbook.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.he.jockbook.Bean.ImageItem;
import com.example.he.jockbook.R;

import java.util.ArrayList;
import java.util.List;

/**显示趣图
 * Created by he on 2017/2/20.
 */

public class ImageFragment  extends Fragment{
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<ImageItem> mList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.pager_layout_2,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.image_recycle_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        mAdapter=new ImageAdapter(mList);
        GridLayoutManager manager=new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void init(){

        mList.add(new ImageItem("http://img.juhe.cn/joke/201412/19/B0C3ABBEBBE0A6EA5B8FE04E27215FBC.gif","看看","1112222"));
        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));
        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));
        mList.add(new ImageItem("http://cn.bing.com/az/hprichbg/rb/YorkshireWinter_ZH-CN9258658675_1920x1080.jpg","看看","1112222"));

    }

}
