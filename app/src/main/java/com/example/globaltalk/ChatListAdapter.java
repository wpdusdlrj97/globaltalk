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

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;
        protected TextView count;
        protected TextView message;
        protected TextView date;
        protected View line;

        public CustomViewHolder(View view, OnItemClickListener listener) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.chat_list_image);
            this.name = (TextView) view.findViewById(R.id.chat_list_name);
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        //퇴장한 사람에 내 이메일이 포함되어 있을 경우
        if (cList.get(position).getExit_person().contains(cList.get(position).getMyemail())) {
            viewholder.image.setVisibility(View.GONE);
            viewholder.name.setVisibility(View.GONE);
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
                    .into(viewholder.image);

            viewholder.name.setText(cList.get(position).getMember_name());
            viewholder.count.setText(cList.get(position).getcount());
            viewholder.message.setText(cList.get(position).getmessage());
            viewholder.date.setText(cList.get(position).getdate());


        }


    }

    @Override
    public int getItemCount() {
        return (null != cList ? cList.size() : 0);
    }


}
