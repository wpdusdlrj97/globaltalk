package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
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



    public ChatListAdapter(Activity context, ArrayList<ChatListData> list) {
        this.context = context;
        this.cList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;


        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.chat_list_image);
            this.name = (TextView) view.findViewById(R.id.chat_list_name);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        Glide.with(context)
                .load(cList.get(position).getchatroom_image())
                //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.gray)
                //.skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.image);

        viewholder.name.setText(cList.get(position).getMember_name());



    }

    @Override
    public int getItemCount() {
        return (null != cList ? cList.size() : 0);
    }




}
