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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CustomViewHolder> {

    private ArrayList<NewsData> nList = null;
    private Activity context = null;


    public NewsAdapter(Activity context, ArrayList<NewsData> list) {
        this.context = context;
        this.nList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView news_thumbnail;
        protected TextView news_title;
        protected TextView news_subtitle;
        protected TextView news_writer;



        public CustomViewHolder(View view) {
            super(view);
            this.news_thumbnail = (ImageView) view.findViewById(R.id.news_thumbnail);
            this.news_title = (TextView) view.findViewById(R.id.news_title);
            this.news_subtitle = (TextView) view.findViewById(R.id.news_subtitle);
            this.news_writer = (TextView) view.findViewById(R.id.news_writer);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {



        Glide.with(context)
                .load(nList.get(position).getNews_thumbnail())
                .placeholder(R.drawable.news)
                .thumbnail(0.4f)
                .fitCenter()
                .into(viewholder.news_thumbnail);




        viewholder.news_title.setText(nList.get(position).getNews_title());
        viewholder.news_subtitle.setText(nList.get(position).getNews_subtitle());
        viewholder.news_writer.setText(nList.get(position).getNews_writer());


    }

    @Override
    public int getItemCount() {
        return (null != nList ? nList.size() : 0);
    }

}
