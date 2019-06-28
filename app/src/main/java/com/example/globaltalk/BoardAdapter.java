package com.example.globaltalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.CustomViewHolder> {


    private ArrayList<BoardData> bList = null;
    private Activity context = null;

    String clientId = "";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "";//애플리케이션 클라이언트 시크릿값";

    String sourceHolder;
    String targetHolder;

    String sourceLang;
    String targetLang;


    ProgressDialog progressDialog;
    //String HttpURL = "http://54.180.122.247/global_communication/board_delete.php";
    HttpParse httpParse = new HttpParse();
    HashMap<String, String> hashMap = new HashMap<>();
    String finalResult;

    //boolean heart;
    int translate=0;


    private NaverDetectionTask mNaverDetectionTask;
    private NaverTTSTask mNaverTTSTask;

    //TTS 대상
    String[] mTextString;
    static String speaker;


    String loginemail;



    public BoardAdapter(Activity context, ArrayList<BoardData> list) {
        this.context = context;
        this.bList = list;
    }


    /*
    //아이템 클릭시 실행 함수
    private ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view,int position);
    }
    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
    */


    class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView board_list_pfimage;

        protected TextView board_list_name;
        protected TextView board_list_time;
        protected TextView board_list_text;


        protected TextView board_list_teach;
        protected TextView board_list_learn;


        protected ImageView board_list_image0;
        protected ImageView board_list_image1;
        protected ImageView board_list_image2;
        protected ImageView board_list_image3;

        protected ImageView board_list_image4;
        protected ImageView board_list_image5;
        protected ImageView board_list_image6;
        protected ImageView board_list_image7;
        protected ImageView board_list_image8;



        protected ImageView board_write_heart;

        protected TextView board_heart_count;

        protected ImageView board_write_translate;

        protected ImageView board_write_more;

        protected ImageView board_write_sound;





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


            this.board_list_pfimage = (ImageView) view.findViewById(R.id.board_list_pfimage);

            this.board_list_teach = (TextView) view.findViewById(R.id.board_list_teach);
            this.board_list_learn = (TextView) view.findViewById(R.id.board_list_learn);

            this.board_list_name = (TextView) view.findViewById(R.id.board_list_name);
            this.board_list_time = (TextView) view.findViewById(R.id.board_list_time);
            this.board_list_text = (TextView) view.findViewById(R.id.board_list_text);


            //하트
            this.board_write_heart = (ImageView) view.findViewById(R.id.board_write_heart);

            //하트 개수
            this.board_heart_count = (TextView) view.findViewById(R.id.board_heart_count);

            //번역
            this.board_write_translate = (ImageView) view.findViewById(R.id.board_write_translate);

            //번역
            this.board_write_sound = (ImageView) view.findViewById(R.id.board_write_sound);


            //더보기란
            this.board_write_more = (ImageView) view.findViewById(R.id.board_write_more);


        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.board_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, final int position) {


        loginemail=bList.get(position).getlogin_email();
        //Log.d("하트-이메일 대조"+position,loginemail);
        final int[] heart_button = {bList.get(position).getheart_boolean()};

        final int[] heart_count = {Integer.parseInt(bList.get(position).getheart_count())};

        if((bList.get(position).getheart_people()).contains(loginemail)){
            heart_button[0] =1;
            Log.d("하트버튼 1로"+position, String.valueOf(heart_button[0]));
        }else{
            heart_button[0] =0;
            Log.d("하트버튼 0로"+position, String.valueOf(heart_button[0]));
        }


        // 하트 클릭 시
        final int[] finalHeart_button = {heart_button[0]};
        viewholder.board_write_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finalHeart_button[0] ==1){//하트를 클릭한 기록이 있을 경우, 하트 해제
                    Glide.with(context)
                            .load(R.drawable.heart)
                            .into(viewholder.board_write_heart);
                    //하트 해제
                    finalHeart_button[0] =0;
                    //하트 수 -1 후에 viewholder에 set해주기
                    heart_count[0] = heart_count[0] -1;
                    viewholder.board_heart_count.setText(String.valueOf(heart_count[0]));

                    //서버,mysql에서 삭제
                    UserHateFunction(bList.get(position).getboard_id(),bList.get(position).getlogin_email());

                }else{
                    Glide.with(context)
                            .load(R.drawable.redheart)
                            .into(viewholder.board_write_heart);
                    //하트 추가
                    finalHeart_button[0] =1;

                    //하트 수 +1 후에 viewholder에 set해주기
                    heart_count[0] = heart_count[0] +1;
                    viewholder.board_heart_count.setText(String.valueOf(heart_count[0]));

                    //서버,mysql에서 추가
                    UserLikeFunction(bList.get(position).getboard_id(),bList.get(position).getlogin_email());

                }

                //Toast.makeText(context, String.valueOf(finalHeart_button), Toast.LENGTH_LONG).show();

            }
        });




        // TTS
        viewholder.board_write_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, String.valueOf(position), Toast.LENGTH_LONG).show();
                String mText;
                mText = viewholder.board_list_text.getText().toString();
                mTextString = new String[]{mText};

                //AsyncTask 실행
                mNaverDetectionTask = new NaverDetectionTask();
                mNaverDetectionTask.execute(mTextString);

            }
        });




        // 번역 버튼
        viewholder.board_write_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TextHolder = bList.get(position).getcontent();
                //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                TranslateTask translateTask = new TranslateTask();
                String sText = bList.get(position).getcontent();

                //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                // 하트 클릭이 안되어 있을 때
                if(translate==0){
                    try {
                        //데이터를 translateAsynctask로부터 가져온다
                        String translate = translateTask.execute(sText).get();

                        //JSON데이터를 자바객체로 변환해야 한다.
                        //Gson을 사용할 것이다.
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObj = parser.parse(translate)
                                //원하는 데이터 까지 찾아 들어간다.
                                .getAsJsonObject().get("message")
                                .getAsJsonObject().get("result");
                        //안드로이드 객체에 담기
                        TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);

                        // 모든 처리 이후에 문자열 set
                        viewholder.board_list_text.setText(items.getTranslatedText());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    translate=translate+1;
                }else{
                    viewholder.board_list_text.setText(sText);
                    translate=translate-1;
                }









            }
        });





        // 더보기란 클릭했을 때
        viewholder.board_write_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //접속한 사람과 게시물을 쓴 사람을 구분하기


                String writeremail;

                loginemail=bList.get(position).getlogin_email();
                writeremail=bList.get(position).getemail();

                //같다면 수정화면으로 넘어가기
                if(loginemail.equals(writeremail)) {

                    final List<String> ListItems = new ArrayList<>();
                    ListItems.add("수정하기");
                    ListItems.add("삭제하기");
                    final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {

                            //수정하기
                            if(pos==0){

                                Intent intent = new Intent(context, Board_editActivity.class);

                                // Board_ID 넘기기 (서버에서 수정할 시 board_id 필요)
                                intent.putExtra("board_id", bList.get(position).getboard_id());
                                Log.d("보드 번호 수정",bList.get(position).getboard_id());

                                //글
                                intent.putExtra("content", bList.get(position).getcontent());
                                Log.d("보드 내용 수정",bList.get(position).getcontent());

                                //이미지 0~9
                                intent.putExtra("img0", bList.get(position).getimg0());
                                Log.d("보드 이미지 수정0",bList.get(position).getimg0());

                                intent.putExtra("img1", bList.get(position).getimg1());
                                Log.d("보드 이미지 수정1",bList.get(position).getimg1());

                                intent.putExtra("img2", bList.get(position).getimg2());
                                Log.d("보드 이미지 수정2",bList.get(position).getimg2());

                                intent.putExtra("img3", bList.get(position).getimg3());
                                Log.d("보드 이미지 수정3",bList.get(position).getimg3());

                                intent.putExtra("img4", bList.get(position).getimg4());
                                Log.d("보드 이미지 수정4",bList.get(position).getimg4());

                                intent.putExtra("img5", bList.get(position).getimg5());
                                Log.d("보드 이미지 수정5",bList.get(position).getimg5());

                                intent.putExtra("img6", bList.get(position).getimg6());
                                Log.d("보드 이미지 수정6",bList.get(position).getimg6());

                                intent.putExtra("img7", bList.get(position).getimg7());
                                Log.d("보드 이미지 수정7",bList.get(position).getimg7());

                                intent.putExtra("img8", bList.get(position).getimg8());
                                Log.d("보드 이미지 수정8",bList.get(position).getimg8());

                                context.startActivityForResult(intent, 1001);

                                Toast.makeText(context, bList.get(position).getboard_id(), Toast.LENGTH_LONG).show();

                            }else{

                                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                                alert_confirm.setMessage("해당 게시물을 정말 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DeleteFunction(bList.get(position).getboard_id());
                                                Log.d("포지션",bList.get(position).getboard_id());


                                                // 배열에서 삭제 및 데이터 갱신
                                                bList.remove(viewholder.getAdapterPosition());
                                                notifyItemRemoved(viewholder.getAdapterPosition());
                                                notifyItemRangeChanged(viewholder.getAdapterPosition(), bList.size());



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

                        }
                    });

                    builder.show();


                }else{
                    Toast.makeText(context, "본인의 게시물만 수정가능합니다", Toast.LENGTH_LONG).show();
                }


            }
        });



        Glide.with(context)
                .load(bList.get(position).getprofile_image())
                //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.gray)
                //.skipMemoryCache(true)
                .thumbnail(0.1f)
                .fitCenter()
                .into(viewholder.board_list_pfimage);
        Log.d("보드 프로필이미지", bList.get(position).getprofile_image());


        viewholder.board_list_teach.setText(bList.get(position).getprofile_teach());
        Log.d("보드 가르칠 언어", bList.get(position).getprofile_teach());

        viewholder.board_list_learn.setText(bList.get(position).getprofile_learn());
        Log.d("보드 학습언어", bList.get(position).getprofile_learn());

        //번역 전 -> 학습 언어
        sourceHolder=bList.get(position).getlogin_learn();
        //번역 후 -> 가르칠 언어
        targetHolder=bList.get(position).getLogin_teach();


        //영어 일때
        if(sourceHolder.equals("English")){
            sourceLang="en";
            //speaker="clara";

        }

        //한국어 일때
        if(sourceHolder.equals("Korean")){
            sourceLang="ko";
            //speaker="mijin";
        }

        //일어 일때
        if(sourceHolder.equals("Japanese")){
            sourceLang="ja";
            //speaker="yuri";
        }

        //중국어 일때
        if(sourceHolder.equals("Chinese")){
            sourceLang="zh-CN";
            //speaker="meimei";
        }


        //영어 일때
        if(targetHolder.equals("English")){
            targetLang="en";

        }

        //한국어 일때
        if(targetHolder.equals("Korean")){
            targetLang="ko";

        }

        //일어 일때
        if(targetHolder.equals("Japanese")){
            targetLang="ja";

        }

        //중국어 일때
        if(targetHolder.equals("Chinese")){
            targetLang="zh-CN";

        }





        viewholder.board_list_name.setText(bList.get(position).getwriter());
        Log.d("보드 네임", bList.get(position).getwriter());

        viewholder.board_list_time.setText(bList.get(position).getwdate());
        Log.d("보드 날짜", bList.get(position).getwdate());

        viewholder.board_list_text.setText(bList.get(position).getcontent());
        Log.d("보드 내용", bList.get(position).getcontent());


        if((bList.get(position).getheart_people()).contains(loginemail)){
            Glide.with(context)
                    .load(R.drawable.redheart)
                    .into(viewholder.board_write_heart);
        }else{
            Glide.with(context)
                    .load(R.drawable.heart)
                    .into(viewholder.board_write_heart);
        }


        viewholder.board_heart_count.setText(bList.get(position).getheart_count());
        //Log.d("보드 내용", bList.get(position).getcontent());


        //0
        if(bList.get(position).getimg0().equals("null")){
            viewholder.board_list_image0.setVisibility(View.GONE);
            Log.d("g 보드 이미지0", bList.get(position).getimg0());
        }else{
            viewholder.board_list_image0.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg0())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image0);

            Log.d("v 보드 이미지0", bList.get(position).getimg0());
        }

        //1
        if(bList.get(position).getimg1().equals("null")){
            viewholder.board_list_image1.setVisibility(View.GONE);
            Log.d("g 보드 이미지1", bList.get(position).getimg1());
        }else{
            viewholder.board_list_image1.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg1())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image1);
            Log.d("v 보드 이미지1", bList.get(position).getimg1());
        }

        //2
        if(bList.get(position).getimg2().equals("null")){
            viewholder.board_list_image2.setVisibility(View.GONE);
            Log.d("g 보드 이미지2", bList.get(position).getimg2());
        }else{
            viewholder.board_list_image2.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg2())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image2);
            Log.d("v 보드 이미지2", bList.get(position).getimg2());
        }

        //3
        if(bList.get(position).getimg3().equals("null")){
            viewholder.board_list_image3.setVisibility(View.GONE);
            Log.d("g 보드 이미지3", bList.get(position).getimg3());
        }else{
            viewholder.board_list_image3.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg3())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image3);
            Log.d("v 보드 이미지3", bList.get(position).getimg3());
        }

        // 4
        if(bList.get(position).getimg4().equals("null")){
            viewholder.board_list_image4.setVisibility(View.GONE);
            Log.d("g 보드 이미지4", bList.get(position).getimg4());
        }else{
            viewholder.board_list_image4.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg4())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image4);
            Log.d("v 보드 이미지4", bList.get(position).getimg4());
        }

        //5
        if(bList.get(position).getimg5().equals("null")){
            viewholder.board_list_image5.setVisibility(View.GONE);
            Log.d("g 보드 이미지5", bList.get(position).getimg5());
        }else{
            viewholder.board_list_image5.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg5())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image5);
            Log.d("v 보드 이미지5", bList.get(position).getimg5());
        }

        //6
        if(bList.get(position).getimg6().equals("null")){
            viewholder.board_list_image6.setVisibility(View.GONE);
            Log.d("g 보드 이미지6", bList.get(position).getimg6());
        }else{
            viewholder.board_list_image6.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg6())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image6);
            Log.d("v 보드 이미지6", bList.get(position).getimg6());
        }


        //7
        if(bList.get(position).getimg7().equals("null")){
            viewholder.board_list_image7.setVisibility(View.GONE);
            Log.d("g 보드 이미지7", bList.get(position).getimg7());
        }else{
            viewholder.board_list_image7.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg7())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image7);
            Log.d("v 보드 이미지7", bList.get(position).getimg7());
        }

        //8
        if(bList.get(position).getimg8().equals("null")){
            viewholder.board_list_image8.setVisibility(View.GONE);
            Log.d("g 보드 이미지8", bList.get(position).getimg8());
        }else{
            viewholder.board_list_image8.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(bList.get(position).getimg8())
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.gray)
                    //.skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(viewholder.board_list_image8);
            Log.d("v 보드 이미지8", bList.get(position).getimg8());
        }











    }

    //자바용 그릇
    private class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }

    @Override
    public int getItemCount() {
        return (null != bList ? bList.size() : 0);
    }


    //게시물 좋아요 함수
    public void UserLikeFunction(final String board_id, final String liker) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 누르셨습니다' 토스트
                Toast.makeText(context, httpResponseMsg, Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                hashMap.put("liker", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_like_plus.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id, liker);
    }

    //게시물 좋아요 취소 함수
    public void UserHateFunction(final String board_id, final String liker) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 취소하셨습니다' 토스트
                Toast.makeText(context, httpResponseMsg, Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                hashMap.put("liker", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_like_minus.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id, liker);
    }





    //게시물 삭제 함수
    public void DeleteFunction(final String board_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(context, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("삭제 성공")) {

                    Toast.makeText(context, httpResponseMsg, Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(context, "삭제 실패", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_delete.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id);
    }



    class TranslateTask extends AsyncTask<String,Void,String> {

        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.

        // 한국어(ko)-영어(en), 한국어(ko)-일본어(ja), 한국어(ko)-중국어 간체(zh-CN), 한국어(ko)-중국어 번체(zh-TW),
        // 중국어 간체(zh-CN) - 일본어(ja), 중국어 번체(zh-TW) - 일본어(ja), 영어(en)-일본어(ja), 영어(en)-중국어 간체(zh-CN), 영어(en)-중국어 번체(zh-TW)


        @Override
        protected String doInBackground(String... strings) {

            String sourceText = strings[0];


            try {
                //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                return response.toString();

            } catch (Exception e) {
                System.out.println(e);
                return null;
            }


        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //최종 결과 처리부
            //Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.



        }



    }



    //언어감지 먼저
    private class NaverDetectionTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            //APIExamTTS.main(mTextString);

            APIExamDetectLangs.main(mTextString);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //언어를 감지한 후 speaker 설정완료했으면 TTS로 넘어가기
            //AsyncTask 실행
            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);

            //tv_title.setText(result);

            //방금 받은 파일명의 mp3가 있으면 플레이 시키자. 맞나 여기서 하는거?
            //아닌가 파일을 만들고 바로 실행되게 해야 하나? AsyncTask 백그라운드 작업중에...?

        }
    }


    private class NaverTTSTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            APIExamTTS.main(mTextString);

            //APIExamDetectLangs.main(mTextString);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            speaker="";

            //tv_title.setText(result);

            //방금 받은 파일명의 mp3가 있으면 플레이 시키자. 맞나 여기서 하는거?
            //아닌가 파일을 만들고 바로 실행되게 해야 하나? AsyncTask 백그라운드 작업중에...?

        }
    }




    // 텍스트 읽기 API
    public static class APIExamTTS {

        private static String TAG = "APIExamTTS";



        public static void main(String[] args) {
            String clientId = "";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "";//애플리케이션 클라이언트 시크릿값";
            try {
                String text = URLEncoder.encode(args[0], "UTF-8"); // 13자
                String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                // post request
                String postParams = "speaker="+speaker+"&speed=0&text=" + text;
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());///여기서 에러 난다?
                Log.d(TAG, String.valueOf(wr));
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    InputStream is = con.getInputStream();
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    //폴더를 만들어 줘야 겠다. 없으면 새로 생성하도록 해야 한다. 일단 Naver폴더에 저장하도록 하자.
                    File dir = new File(Environment.getExternalStorageDirectory()+"/", "Naver");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    // 랜덤한 이름으로 mp3 파일 생성
                    //String tempname = Long.valueOf(new Date().getTime()).toString();
                    String tempname = "naverttstemp"; //하나의 파일명으로 덮어쓰기 하자.
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Naver/" + tempname + ".mp3");
                    f.createNewFile();
                    OutputStream outputStream = new FileOutputStream(f);
                    while ((read =is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    is.close();

                    //여기서 바로 재생하도록 하자. mp3파일 재생 어떻게 하지? 구글링!
                    String Path_to_file = Environment.getExternalStorageDirectory()+ File.separator+"Naver/"+tempname+".mp3";
                    MediaPlayer audioPlay = new MediaPlayer();
                    audioPlay.setDataSource(Path_to_file);
                    audioPlay.prepare();//이걸 해줘야 하는군. 없으면 에러난다.
                    audioPlay.start();
                    //재생하고 나서 파일을 지워줘야 하나? 이거참 고민이네... if문으로 분기 시켜야 하나?
                    //아니면 유니크한 파일로 만들지 말고 하나의 파일명으로 저장하게 할수도 있을듯...



                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    // 언어감지
    public static class APIExamDetectLangs {

        private static String TAG = "APIExamDetectLangs";

        public static void main(String[] args) {
            String clientId = "";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "";//애플리케이션 클라이언트 시크릿값";
            try {
                String query = URLEncoder.encode(args[0], "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/detectLangs";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "query=" + query;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                Log.d(TAG, String.valueOf(wr));
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());

                //Json 파싱해서 언어만 가져온 후 speaker 대입하기
                try
                {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String language = jsonObject.getString("langCode");


                    if(language.equals("ko")){
                        //tv_title.setText("Korean");
                        speaker="mijin";
                    }
                    if(language.equals("en")){
                        //tv_title.setText("English");
                        speaker="clara";
                    }
                    if(language.equals("ja")){
                        //tv_title.setText("Japan");
                        speaker="yuri";
                    }
                    if(language.equals("zh-CN")){
                        //tv_title.setText("Chinese");
                        speaker="meimei";
                    }



                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }




            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }




}