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
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.CustomViewHolder> {

    private ArrayList<BoardData> bList = null;
    private Activity context = null;


    public BoardAdapter(Activity context, ArrayList<BoardData> list) {
        this.context = context;
        this.bList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView board_list_name;
        protected TextView board_list_time;
        protected TextView board_list_text;


        protected ImageView board_list_image0;
        protected ImageView board_list_image1;
        protected ImageView board_list_image2;
        protected ImageView board_list_image3;


        public CustomViewHolder(View view) {
            super(view);
            this.board_list_image0 = (ImageView) view.findViewById(R.id.board_list_image0);
            this.board_list_image1 = (ImageView) view.findViewById(R.id.board_list_image1);
            this.board_list_image2 = (ImageView) view.findViewById(R.id.board_list_image2);
            this.board_list_image3 = (ImageView) view.findViewById(R.id.board_list_image3);

            this.board_list_name = (TextView) view.findViewById(R.id.board_list_name);
            this.board_list_time = (TextView) view.findViewById(R.id.board_list_time);
            this.board_list_text = (TextView) view.findViewById(R.id.board_list_text);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.board_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {



        viewholder.board_list_name.setText(bList.get(position).getwriter());
        viewholder.board_list_time.setText(bList.get(position).getwdate());
        viewholder.board_list_text.setText(bList.get(position).getcontent());


        Glide.with(context)
                .load(bList.get(position).getimg0())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.board_list_image0);

        Glide.with(context)
                .load(bList.get(position).getimg1())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.board_list_image1);

        Glide.with(context)
                .load(bList.get(position).getimg2())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.board_list_image2);

        Glide.with(context)
                .load(bList.get(position).getimg3())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.board_list_image3);


    }

    @Override
    public int getItemCount() {
        return (null != bList ? bList.size() : 0);
    }

}
