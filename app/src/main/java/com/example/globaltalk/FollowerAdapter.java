package com.example.globaltalk;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.CustomViewHolder> {

    private ArrayList<FollowerData> mList = null;
    private Activity context = null;


    public FollowerAdapter(Activity context, ArrayList<FollowerData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView id;
        protected TextView name;
        protected TextView teach;
        protected TextView learn;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (ImageView) view.findViewById(R.id.follower_list_id);
            this.name = (TextView) view.findViewById(R.id.follower_list_name);
            this.teach = (TextView) view.findViewById(R.id.follower_list_teach);
            this.learn = (TextView) view.findViewById(R.id.follower_list_learn);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.follower_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        Glide.with(context)
                .load(mList.get(position).getMember_image())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.id);

        viewholder.name.setText(mList.get(position).getMember_name());
        viewholder.teach.setText(mList.get(position).getMember_teach());
        viewholder.learn.setText(mList.get(position).getMember_learn());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
