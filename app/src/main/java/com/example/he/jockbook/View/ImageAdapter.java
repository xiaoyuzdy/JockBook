package com.example.he.jockbook.View;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.he.jockbook.Bean.ImageItem;
import com.example.he.jockbook.R;
import com.example.he.jockbook.Utility.imageloader.loader.ImageLoader;

import java.util.List;

/**
 * Created by he on 2017/2/20.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<ImageItem> mList;
    private ImageLoader loader;
    private int width;

    public ImageAdapter(List<ImageItem> mList) {
        this.mList = mList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageItem item=mList.get(position);
        holder.updateTime.setText(item.getmUpdateTime());
        loader=ImageLoader.build(mContext);
//        width= MyUtils.getScreenMetrics(mContext).widthPixels;
//        loader.bindBitmap(item.getmImageUrl(),holder.content,width,600);
        Glide.with(mContext).load(item.getmImageUrl()).into(holder.content);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView updateTime;
        ImageView content;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            updateTime = (TextView) itemView.findViewById(R.id.image_updateTime);
            content = (ImageView) itemView.findViewById(R.id.image_content);
        }
    }
}
