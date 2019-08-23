package com.example.globaltalk;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InStreamingAdapter extends RecyclerView.Adapter<InStreamingAdapter.CustomViewHolder> {

    private ArrayList<InStreamingData> isList = null;
    private Activity context = null;


    String sourceHolder;
    String targetHolder;

    String sourceLang;
    String targetLang;


    int translate=0;

    String clientId = "";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "";//애플리케이션 클라이언트 시크릿값";


    public InStreamingAdapter(Activity context, ArrayList<InStreamingData> list) {
        this.context = context;
        this.isList = list;
    }




    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView streamer_mTextView;
        protected TextView streamer_mTextView_translate;
        protected ImageView streamer_chat_translate;
        protected ImageView stream_after_translate;
        protected TextView streamer_profile_name;



        public CustomViewHolder(View view) {
            super(view);
            this.streamer_mTextView = (TextView) view.findViewById(R.id.streamer_mTextView);
            this.streamer_mTextView_translate = (TextView) view.findViewById(R.id.streamer_mTextView_translate);
            this.streamer_chat_translate = (ImageView) view.findViewById(R.id.streamer_chat_translate);
            this.stream_after_translate = (ImageView) view.findViewById(R.id.stream_after_translate);
            this.streamer_profile_name = (TextView) view.findViewById(R.id.streamer_profile_name);





        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.streamer_chat_item, null);
        //CustomViewHolder viewHolder = new CustomViewHolder(view);


        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {


        //번역 전 -> 학습 언어
        sourceHolder=isList.get(position).getlearn_login();
        //번역 후 -> 가르칠 언어
        targetHolder=isList.get(position).getteach_login();


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



        if(isList.get(position).getStream_Name().equals(isList.get(position).getchat_email())){ // 방장일 경우
            viewholder.streamer_mTextView_translate.setVisibility(View.GONE);
            viewholder.stream_after_translate.setVisibility(View.GONE);



            viewholder.streamer_chat_translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 번역 텍스트란 visible
                    viewholder.streamer_mTextView_translate.setVisibility(View.VISIBLE);
                    viewholder.stream_after_translate.setVisibility(View.VISIBLE);


                    //TextHolder = bList.get(position).getcontent();
                    //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                    //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                    //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                    TranslateTask translateTask = new TranslateTask();
                    String sText = isList.get(position).getchat_content();

                    //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                    // 번역을 누르기 전
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
                            viewholder.streamer_mTextView_translate.setText(items.getTranslatedText());


                            //폴더로 바꿔주기
                            Glide.with(context)
                                    .load(R.drawable.folder_white)
                                    .into(viewholder.streamer_chat_translate);


                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        translate=translate+1;
                    }else{
                        viewholder.streamer_mTextView_translate.setVisibility(View.GONE);
                        viewholder.stream_after_translate.setVisibility(View.GONE);

                        Glide.with(context)
                                .load(R.drawable.translate_white)
                                .into(viewholder.streamer_chat_translate);
                        translate=translate-1;
                    }




                }
            });


            String strColor = "#00FF00";


            viewholder.streamer_profile_name.setText(isList.get(position).getchat_profile_name());

            viewholder.streamer_profile_name.setTextColor(Color.parseColor(strColor));

            viewholder.streamer_mTextView.setText(isList.get(position).getchat_content());
            Log.d("이메일 다를 때","리턴 2");

            //텍스트뷰.setTextColor(Color.parseColor(strColor));

            viewholder.streamer_mTextView.setTextColor(Color.parseColor(strColor));


        }else{

            viewholder.streamer_mTextView_translate.setVisibility(View.GONE);
            viewholder.stream_after_translate.setVisibility(View.GONE);


            viewholder.streamer_chat_translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 번역 텍스트란 visible
                    viewholder.streamer_mTextView_translate.setVisibility(View.VISIBLE);
                    viewholder.stream_after_translate.setVisibility(View.VISIBLE);


                    //TextHolder = bList.get(position).getcontent();
                    //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                    //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                    //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                    TranslateTask translateTask = new TranslateTask();
                    String sText = isList.get(position).getchat_content();

                    //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                    // 번역을 누르기 전
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
                            viewholder.streamer_mTextView_translate.setText(items.getTranslatedText());


                            //폴더로 바꿔주기
                            Glide.with(context)
                                    .load(R.drawable.folder_white)
                                    .into(viewholder.streamer_chat_translate);


                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        translate=translate+1;
                    }else{
                        viewholder.streamer_mTextView_translate.setVisibility(View.GONE);
                        viewholder.stream_after_translate.setVisibility(View.GONE);

                        Glide.with(context)
                                .load(R.drawable.translate_white)
                                .into(viewholder.streamer_chat_translate);
                        translate=translate-1;
                    }




                }
            });



            viewholder.streamer_profile_name.setText(isList.get(position).getchat_profile_name());



            viewholder.streamer_mTextView.setText(isList.get(position).getchat_content());
            Log.d("이메일 다를 때","리턴 2");

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
        return (null != isList ? isList.size() : 0);
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








}
