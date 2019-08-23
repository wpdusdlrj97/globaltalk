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

public class StreamingAdapter extends RecyclerView.Adapter<StreamingAdapter.CustomViewHolder> {

    private ArrayList<StreamingData> sList = null;
    private Activity context = null;


    public StreamingAdapter(Activity context, ArrayList<StreamingData> list) {
        this.context = context;
        this.sList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView streaming_thumbnail;
        protected TextView streamer;
        protected TextView streaming_roomname;



        public CustomViewHolder(View view) {
            super(view);
            this.streaming_thumbnail = (ImageView) view.findViewById(R.id.streaming_thumbnail);
            this.streamer = (TextView) view.findViewById(R.id.streamer);
            this.streaming_roomname = (TextView) view.findViewById(R.id.streaming_roomname);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.streaming_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {



        Glide.with(context)
                .load(sList.get(position).getstreaming_thumbnail())
                .placeholder(R.drawable.streaming)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.streaming_thumbnail);




        int idx_name = sList.get(position).getstream_name().indexOf("@");

        String streamer_name = sList.get(position).getstream_name().substring(0, idx_name);


        viewholder.streamer.setText(streamer_name);



        viewholder.streaming_roomname.setText(sList.get(position).getroom_name());

    }

    @Override
    public int getItemCount() {
        return (null != sList ? sList.size() : 0);
    }

}
