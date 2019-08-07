package com.example.globaltalk;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.CustomViewHolder> {

    private ArrayList<InviteData> ivList = null;
    private Activity context = null;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public InviteAdapter(Activity context, ArrayList<InviteData> list) {
        this.context = context;
        this.ivList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView id;
        protected TextView name;
        protected CheckBox checkbox;
        protected View line;

        public CustomViewHolder(View view, OnItemClickListener listener) {
            super(view);
            this.id = (ImageView) view.findViewById(R.id.invite_list_id);
            this.name = (TextView) view.findViewById(R.id.invite_list_name);
            this.checkbox = (CheckBox) view.findViewById(R.id.invite_checkbox);
            this.line = (View) view.findViewById(R.id.invite_list_line);

            //체크박스를 클릭할 시에 해당 이메일 토스트
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //체크될때만 반응하기
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invite_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        String invite_email = ivList.get(position).getMember_email();
        Log.d("이메일",invite_email);
        String chat_list = ivList.get(position).getInviteList();
        Log.d("이메일 비교 채팅",chat_list);

        if(ivList.get(position).getInviteList().contains(invite_email)){ // 이미 채팅을 하고 있을 경우 X

            viewholder.id .setVisibility(View.GONE);
            viewholder.name.setVisibility(View.GONE);
            viewholder.checkbox.setVisibility(View.GONE);
            viewholder.line.setVisibility(View.GONE);


        }else{ // 채팅방에 없는 팔로우리스트일 경우 불러온다

            Glide.with(context)
                    .load(ivList.get(position).getMember_image())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.id);

            viewholder.name.setText(ivList.get(position).getMember_name());
        }




    }

    @Override
    public int getItemCount() {
        return (null != ivList ? ivList.size() : 0);
    }

}
