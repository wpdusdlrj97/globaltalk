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

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.CustomViewHolder> {

    private ArrayList<InviteData> ivList = null;
    private Activity context = null;


    public InviteAdapter(Activity context, ArrayList<InviteData> list) {
        this.context = context;
        this.ivList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView id;
        protected TextView name;



        public CustomViewHolder(View view) {
            super(view);
            this.id = (ImageView) view.findViewById(R.id.invite_list_id);
            this.name = (TextView) view.findViewById(R.id.invite_list_name);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invite_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        Glide.with(context)
                .load(ivList.get(position).getMember_image())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.id);

        viewholder.name.setText(ivList.get(position).getMember_name());

    }

    @Override
    public int getItemCount() {
        return (null != ivList ? ivList.size() : 0);
    }

}
