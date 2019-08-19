package com.example.globaltalk;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {

    private ArrayList<CommentData> cList = null;
    private Activity context = null;

    HttpParse httpParse = new HttpParse();
    HashMap<String, String> hashMap = new HashMap<>();
    String finalResult;


    public CommentAdapter(Activity context, ArrayList<CommentData> list) {
        this.context = context;
        this.cList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;
        protected TextView content;
        protected TextView time;
        protected TextView delete;



        public CustomViewHolder(View view) {
            super(view);
            this.image = (ImageView) view.findViewById(R.id.board_comment_image);
            this.name = (TextView) view.findViewById(R.id.board_comment_name);
            this.content = (TextView) view.findViewById(R.id.board_comment_content);
            this.time = (TextView) view.findViewById(R.id.board_comment_time);
            this.delete = (TextView) view.findViewById(R.id.board_comment_delete);


        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {



        Glide.with(context)
                .load(cList.get(position).getMember_image())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.image);

        viewholder.name.setText(cList.get(position).getMember_name());
        viewholder.content.setText(cList.get(position).getMember_content());
        viewholder.time.setText(cList.get(position).getMember_time());


        //로그인한 사람과 댓글작성자가 같을 경우
        if(cList.get(position).getLogin_email().equals(cList.get(position).getMember_email())){
            viewholder.delete.setText("삭제");


            viewholder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                    alert_confirm.setMessage("해당 게시물을 정말 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    Log.d("포지션 댓글",cList.get(position).getComment_id());
                                    Log.d("포지션 보드번호",cList.get(position).getBoard_id());

                                    DeleteFunction(cList.get(position).getComment_id(),cList.get(position).getBoard_id());

                                    // 배열에서 삭제 및 데이터 갱신
                                    cList.remove(viewholder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    /*notifyItemRemoved(viewholder.getAdapterPosition());
                                    notifyItemRangeChanged(viewholder.getAdapterPosition(), cList.size());*/

                                    //댓글 개수 줄어들게 해주기, 다른 액티비티 메소드 사용
                                    ((Board_detailActivity)Board_detailActivity.mcontext).Delete_comment();



                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(context, "취소하셨습니다", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();



                }
            });




        }else{
            viewholder.delete.setVisibility(View.GONE);



        }



    }

    @Override
    public int getItemCount() {
        return (null != cList ? cList.size() : 0);
    }


    //게시물 삭제 함수
    public void DeleteFunction(final String comment_id, final String board_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);



            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("comment_id", params[0]);

                hashMap.put("board_id", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/comment_delete.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(comment_id,board_id);
    }

}

