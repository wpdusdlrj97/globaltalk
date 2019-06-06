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

        protected ImageView board_list_image4;
        protected ImageView board_list_image5;
        protected ImageView board_list_image6;
        protected ImageView board_list_image7;
        protected ImageView board_list_image8;





        public CustomViewHolder(View view) {
            super(view);
            this.board_list_image0 = (ImageView) view.findViewById(R.id.board_list_image0);
            this.board_list_image1 = (ImageView) view.findViewById(R.id.board_list_image1);
            this.board_list_image2 = (ImageView) view.findViewById(R.id.board_list_image2);
            this.board_list_image3 = (ImageView) view.findViewById(R.id.board_list_image3);
            this.board_list_image4 = (ImageView) view.findViewById(R.id.board_list_image4);
            this.board_list_image5 = (ImageView) view.findViewById(R.id.board_list_image5);
            this.board_list_image6 = (ImageView) view.findViewById(R.id.board_list_image6);
            this.board_list_image7 = (ImageView) view.findViewById(R.id.board_list_image7);
            this.board_list_image8 = (ImageView) view.findViewById(R.id.board_list_image8);


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


        //0
        if(bList.get(position).getimg0().equals("null")){
            viewholder.board_list_image0.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg0())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image0);
        }

        //1
        if(bList.get(position).getimg1().equals("null")){
            viewholder.board_list_image1.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg1())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image1);
        }

        //2
        if(bList.get(position).getimg2().equals("null")){
            viewholder.board_list_image2.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg2())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image2);
        }

        //3
        if(bList.get(position).getimg3().equals("null")){
            viewholder.board_list_image3.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg3())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image3);
        }

        // 4
        if(bList.get(position).getimg4().equals("null")){
            viewholder.board_list_image4.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg4())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image4);
        }

        //5
        if(bList.get(position).getimg5().equals("null")){
            viewholder.board_list_image5.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg5())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image5);
        }

        //6
        if(bList.get(position).getimg6().equals("null")){
            viewholder.board_list_image6.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg6())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image6);
        }


        //7
        if(bList.get(position).getimg7().equals("null")){
            viewholder.board_list_image7.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg7())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image7);
        }

        //8
        if(bList.get(position).getimg8().equals("null")){
            viewholder.board_list_image8.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(bList.get(position).getimg8())
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image8);
        }











    }

    @Override
    public int getItemCount() {
        return (null != bList ? bList.size() : 0);
    }

}
