package com.example.globaltalk;

import android.app.Activity;
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

import java.util.ArrayList;

public class InChatAdapter extends RecyclerView.Adapter<InChatAdapter.CustomViewHolder> {

    private ArrayList<InChatData> icList = null;
    private Activity context = null;


    public InChatAdapter(Activity context, ArrayList<InChatData> list) {
        this.context = context;
        this.icList = list;
    }



    @Override
    public int getItemViewType(int position) {


        if(icList.get(position).getchat_email().equals("notice@naver.com")){//공지일 경우 return 3
            return 3;
        }//채팅에서 온 이메일이랑 나의 이메일이랑 같으면 1번 뷰에
        else if (icList.get(position).getchat_email().equals(icList.get(position).getmy_email())){
            return 1;

        } else { // 다르면 2번 뷰에 뿌려준다
            return 2;

        }
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextView;
        protected ImageView chat_profile_image;
        protected TextView chat_profile_name;
        protected TextView chat_date;



        public CustomViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.mTextView);
            this.chat_profile_image = (ImageView) view.findViewById(R.id.chat_profile_image);
            this.chat_profile_name = (TextView) view.findViewById(R.id.chat_profile_name);
            this.chat_date = (TextView) view.findViewById(R.id.chat_date);



        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_chat_item, null);
        //CustomViewHolder viewHolder = new CustomViewHolder(view);


        View view;
        if(viewType ==1){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_chat_item, null);
        } else if(viewType ==2){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_chat_item, null);
        }else{
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_chat_item, null);
        }

        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        if(icList.get(position).getchat_email().equals("notice@naver.com")){//공지일 경우 return 3
            viewholder.mTextView.setText(icList.get(position).getchat_content());
            Log.d("공지일 경우","리턴 3");

        } else if (icList.get(position).getchat_email().equals(icList.get(position).getmy_email())){ //채팅에서 온 이메일이랑 나의 이메일이랑 같으면 1번 뷰에
            viewholder.mTextView.setText(icList.get(position).getchat_content());
            Log.d("이메일 같을 때","리턴 1");

            viewholder.chat_date.setText(icList.get(position).getchat_wdate());

        } else { // 다르면 2번 뷰에 뿌려준다

            Glide.with(context)
                    .load(icList.get(position).getchat_profile_image())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.chat_profile_image);

            viewholder.chat_profile_name.setText(icList.get(position).getchat_profile_name());

            viewholder.chat_date.setText(icList.get(position).getchat_wdate());

            viewholder.mTextView.setText(icList.get(position).getchat_content());
            Log.d("이메일 다를 때","리턴 2");
        }



    }

    @Override
    public int getItemCount() {
        return (null != icList ? icList.size() : 0);
    }

}
