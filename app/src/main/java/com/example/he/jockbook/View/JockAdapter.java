package com.example.he.jockbook.View;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.he.jockbook.Bean.JockItem;
import com.example.he.jockbook.R;

import java.util.List;

/**
 * Created by he on 2017/2/19.
 */

public class JockAdapter extends RecyclerView.Adapter<JockAdapter.ViewHolder> {

    public JockAdapter(List<JockItem> mJock) {
        this.mJock = mJock;
    }

    private Context mContext;
    private List<JockItem> mJock;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.jock_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JockItem item = mJock.get(position);
        holder.updateTime.setText(item.getUpdateTime());
        holder.content.setText(item.getContent());

    }

    @Override
    public int getItemCount() {
        return mJock.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView updateTime;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            updateTime = (TextView) itemView.findViewById(R.id.jock_updateTime);
            content = (TextView) itemView.findViewById(R.id.jock_content);
        }
    }


}
