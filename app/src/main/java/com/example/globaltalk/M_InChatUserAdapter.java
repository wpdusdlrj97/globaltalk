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

import java.util.ArrayList;

public class M_InChatUserAdapter extends RecyclerView.Adapter<M_InChatUserAdapter.CustomViewHolder> {

    private ArrayList<M_InChatUserData> m_icuser_List = null;
    private Activity context = null;


    public M_InChatUserAdapter(Activity context, ArrayList<M_InChatUserData> list) {
        this.context = context;
        this.m_icuser_List = list;
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;




        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.m_chat_user_list_image);
            this.name = (TextView) view.findViewById(R.id.m_chat_user_list_name);



        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.m_inchat_user_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        Glide.with(context)
                .load(m_icuser_List.get(position).getchat_profile_image())
                //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.gray)
                //.skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.image);

        viewholder.name.setText(m_icuser_List.get(position).getchat_profile_name());

    }

    @Override
    public int getItemCount() {
        return (null != m_icuser_List ? m_icuser_List.size() : 0);
    }

}
