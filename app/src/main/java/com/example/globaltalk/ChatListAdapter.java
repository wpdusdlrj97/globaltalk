package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.CustomViewHolder> {


    private ArrayList<ChatListData> cList = null;
    private Activity context = null;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public ChatListAdapter(Activity context, ArrayList<ChatListData> list) {
        this.context = context;
        this.cList = list;
    }


    @Override
    public int getItemViewType(int position) {


        if(cList.get(position).getcount()<=2){ // 대화자가 2명일 경우 상대방 사진만
            return 1;
        }else if(cList.get(position).getcount()==3){ // 대화자가 3명일 경우 상대방 2명 사진만
            return 2;
        }else if(cList.get(position).getcount()==4){ // 대화자가 4명일 경우 상대방 3명 사진만
            return 3;
        }else{  // 대화자가 5명일 경우 상대방 4명 사진만
            return 4;
        }


    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image1;
        protected ImageView image2;
        protected ImageView image3;
        protected ImageView image4;

        protected TextView roomname;


        protected TextView count;
        protected TextView message;
        protected TextView date;
        protected View line;

        public CustomViewHolder(View view, OnItemClickListener listener) {
            super(view);
            this.image1 = (ImageView) view.findViewById(R.id.chat_list_image1);
            this.image2 = (ImageView) view.findViewById(R.id.chat_list_image2);
            this.image3 = (ImageView) view.findViewById(R.id.chat_list_image3);
            this.image4 = (ImageView) view.findViewById(R.id.chat_list_image4);

            this.roomname = (TextView) view.findViewById(R.id.chat_list_roomname);

            this.count = (TextView) view.findViewById(R.id.chat_list_count);
            this.message = (TextView) view.findViewById(R.id.chat_list_message);
            this.date = (TextView) view.findViewById(R.id.chat_list_date);
            this.line = (View) view.findViewById(R.id.chat_list_line);


            //체크박스를 클릭할 시에 해당 이메일 토스트
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);


                        }
                    }


                }
            });

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        //        View view;
        //        if(viewType ==1){
        //            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_chat_item, null);
        //        } else if(viewType ==2){
        //            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_chat_item, null);
        //        }else{
        //            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_chat_item, null);
        //        }
        //
        //        CustomViewHolder viewHolder = new CustomViewHolder(view);
        //
        //
        //        return viewHolder;

        View view;

        if(viewType ==1){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list1, null);
        } else if(viewType ==2){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list2, null);
        } else if(viewType ==3){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list3, null);
        } else {
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list4, null);
        }


        CustomViewHolder viewHolder = new CustomViewHolder(view, mListener);

        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        if(cList.get(position).getcount()<=2){ // 대화자가 2명일 경우 상대방 사진만

            //퇴장한 사람에 내 이메일이 포함되어 있을 경우
            if (cList.get(position).getExit_person().contains(cList.get(position).getMyemail())) {
                viewholder.image1.setVisibility(View.GONE);
                viewholder.roomname.setVisibility(View.GONE);
                viewholder.count.setVisibility(View.GONE);
                viewholder.message.setVisibility(View.GONE);
                viewholder.date.setVisibility(View.GONE);
                viewholder.line.setVisibility(View.GONE);


            } else {

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image1);

                viewholder.roomname.setText(cList.get(position).getroom_name());
                viewholder.count.setText(String.valueOf(cList.get(position).getcount()));
                viewholder.message.setText(cList.get(position).getmessage());
                viewholder.date.setText(cList.get(position).getdate());


            }

        }else if(cList.get(position).getcount()==3){ // 대화자가 3명일 경우 상대방 2명 사진만


            //퇴장한 사람에 내 이메일이 포함되어 있을 경우
            if (cList.get(position).getExit_person().contains(cList.get(position).getMyemail())) {
                viewholder.image1.setVisibility(View.GONE);
                viewholder.image2.setVisibility(View.GONE);
                viewholder.roomname.setVisibility(View.GONE);
                viewholder.count.setVisibility(View.GONE);
                viewholder.message.setVisibility(View.GONE);
                viewholder.date.setVisibility(View.GONE);
                viewholder.line.setVisibility(View.GONE);


            } else {

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image1);

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image2())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image2);




                viewholder.roomname.setText(cList.get(position).getroom_name());
                viewholder.count.setText(String.valueOf(cList.get(position).getcount()));
                viewholder.message.setText(cList.get(position).getmessage());
                viewholder.date.setText(cList.get(position).getdate());


            }



        }else if(cList.get(position).getcount()==4){ // 대화자가 4명일 경우 상대방 3명 사진만

            //퇴장한 사람에 내 이메일이 포함되어 있을 경우
            if (cList.get(position).getExit_person().contains(cList.get(position).getMyemail())) {
                viewholder.image1.setVisibility(View.GONE);
                viewholder.image2.setVisibility(View.GONE);
                viewholder.image3.setVisibility(View.GONE);
                viewholder.roomname.setVisibility(View.GONE);
                viewholder.count.setVisibility(View.GONE);
                viewholder.message.setVisibility(View.GONE);
                viewholder.date.setVisibility(View.GONE);
                viewholder.line.setVisibility(View.GONE);


            } else {

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image1);

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image2())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image2);


                Glide.with(context)
                        .load(cList.get(position).getchatroom_image3())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image3);



                viewholder.roomname.setText(cList.get(position).getroom_name());
                viewholder.count.setText(String.valueOf(cList.get(position).getcount()));
                viewholder.message.setText(cList.get(position).getmessage());
                viewholder.date.setText(cList.get(position).getdate());


            }


        }else{  // 대화자가 5명일 경우 상대방 4명 사진만

            //퇴장한 사람에 내 이메일이 포함되어 있을 경우
            if (cList.get(position).getExit_person().contains(cList.get(position).getMyemail())) {
                viewholder.image1.setVisibility(View.GONE);
                viewholder.image2.setVisibility(View.GONE);
                viewholder.image3.setVisibility(View.GONE);
                viewholder.image4.setVisibility(View.GONE);
                viewholder.roomname.setVisibility(View.GONE);
                viewholder.count.setVisibility(View.GONE);
                viewholder.message.setVisibility(View.GONE);
                viewholder.date.setVisibility(View.GONE);
                viewholder.line.setVisibility(View.GONE);


            } else {

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image1);

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image2())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image2);


                Glide.with(context)
                        .load(cList.get(position).getchatroom_image3())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image3);

                Glide.with(context)
                        .load(cList.get(position).getchatroom_image4())
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.image4);



                viewholder.roomname.setText(cList.get(position).getroom_name());
                viewholder.count.setText(String.valueOf(cList.get(position).getcount()));
                viewholder.message.setText(cList.get(position).getmessage());
                viewholder.date.setText(cList.get(position).getdate());


            }

        }




    }

    @Override
    public int getItemCount() {
        return (null != cList ? cList.size() : 0);
    }


}
