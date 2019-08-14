package com.example.globaltalk;

import android.app.Activity;
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
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InChatAdapter extends RecyclerView.Adapter<InChatAdapter.CustomViewHolder> {

    private ArrayList<InChatData> icList = null;
    private Activity context = null;

    String sourceLang="en";
    String targetLang="ko";


    int translate=0;

    String clientId = "";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "";//애플리케이션 클라이언트 시크릿값";


    public InChatAdapter(Activity context, ArrayList<InChatData> list) {
        this.context = context;
        this.icList = list;
    }



    @Override
    public int getItemViewType(int position) {


        if(icList.get(position).getchat_email().equals("notice@naver.com")){ //공지일 경우 return 3
            return 3;
        }//채팅에서 온 이메일이랑 나의 이메일이랑 같으면 1번 뷰에
        else if (icList.get(position).getchat_email().equals(icList.get(position).getmy_email())){
            Log.d("내가 채팅을 했을 경우",icList.get(position).getchat_content());
            Log.d("내가 채팅을 했을 경우 포지션값", String.valueOf(position));

            //만약 채팅내용이 이미지일 경우
            if(icList.get(position).getchat_content().contains("content://media/external") || icList.get(position).getchat_content().contains("http://54.180.122.247/")){
                Log.d("나의 이미지일 경우","리턴 4");
                return 4;

            }else{ //만약 채팅내용이 텍스트일 경우

                return 1;
            }


        } else { // 다르면 2번 뷰에 뿌려준다


            //만약 채팅내용이 이미지일 경우
            if(icList.get(position).getchat_content().contains("content://media/external") || icList.get(position).getchat_content().contains("http://54.180.122.247/")){
                Log.d("상대방의 이미지일 경우","리턴 5");
                return 5;

            }else{ //만약 채팅내용이 텍스트일 경우

                return 2;
            }


        }







    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTextView;
        protected TextView mTextView_translate;
        protected ImageView chat_translate;
        protected ImageView Chat_ImageView;
        protected ImageView chat_profile_image;
        protected TextView chat_profile_name;
        protected TextView chat_date;



        public CustomViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.mTextView);
            this.mTextView_translate = (TextView) view.findViewById(R.id.mTextView_translate);
            this.chat_translate = (ImageView) view.findViewById(R.id.chat_translate);
            this.Chat_ImageView = (ImageView) view.findViewById(R.id.Chat_ImageView);
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
        } else if(viewType ==3){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_chat_item, null);
        } else if(viewType ==4){
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_image_chat_item, null);
        } else{
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_image_chat_item, null);
        }



        CustomViewHolder viewHolder = new CustomViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {



        if(icList.get(position).getchat_email().equals("notice@naver.com")){//공지일 경우 return 3

            //icList.get(position).getchat_content()=icList.get(position).getchat_content().replaceAll("@naver.com","");

            viewholder.mTextView.setText(icList.get(position).getchat_content().replaceAll("@naver.com",""));
            Log.d("공지일 경우","리턴 3");

        } else if (icList.get(position).getchat_email().equals(icList.get(position).getmy_email())){ //채팅에서 온 이메일이랑 나의 이메일이랑 같으면 1번 뷰에



            //만약 채팅내용이 이미지일 경우
            if(icList.get(position).getchat_content().contains("content://media/external") || icList.get(position).getchat_content().contains("http://54.180.122.247/")){


                //받아온 이미지가 content:로 시작할 경우 (상대경로)
                if(icList.get(position).getchat_content().contains("content://media/external")){

                    //스트링으로 받아왔기 때문에 uri로 파싱해줘야 한다
                    Glide.with(context)
                            .load(Uri.parse(icList.get(position).getchat_content()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(viewholder.Chat_ImageView);

                    Log.d("이메일 같을 때 이미지","리턴 1");

                    viewholder.chat_date.setText(icList.get(position).getchat_wdate());



                }else{  //받아온 이미지가 http://로 시작할 경우 (절대경로)


                    //스트링으로 받아왔기 때문에 uri로 파싱해줘야 한다
                    Glide.with(context)
                            .load(icList.get(position).getchat_content())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(viewholder.Chat_ImageView);

                    Log.d("이메일 같을 때 이미지","리턴 1");

                    viewholder.chat_date.setText(icList.get(position).getchat_wdate());

                }






            }else{ //만약 채팅내용이 텍스트일 경우

                viewholder.mTextView.setText(icList.get(position).getchat_content());
                Log.d("이메일 같을 때","리턴 1");

                viewholder.chat_date.setText(icList.get(position).getchat_wdate());


            }







        } else { // 다르면 2번 뷰에 뿌려준다



            //만약 채팅내용이 이미지일 경우
            if(icList.get(position).getchat_content().contains("content://media/external") || icList.get(position).getchat_content().contains("http://54.180.122.247/")){

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


                //스트링으로 받아왔기 때문에 uri로 파싱해줘야 한다


                Glide.with(context)
                        .load(icList.get(position).getchat_content())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(viewholder.Chat_ImageView);
                Log.d("이메일 다를 때 이미지","리턴 2");



            }else{ //만약 채팅내용이 텍스트일 경우

                //viewholder.chat_translate.setVisibility(View.GONE);

                viewholder.chat_translate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 번역 텍스트란 visible
                        //viewholder.board_list_transtext.setVisibility(View.VISIBLE);

                        //TextHolder = bList.get(position).getcontent();
                        //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                        //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                        //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                        TranslateTask translateTask = new TranslateTask();
                        String sText = icList.get(position).getchat_content();

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
                                viewholder.mTextView_translate.setText(items.getTranslatedText());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            translate=translate+1;
                        }else{
                            //viewholder.board_list_transtext.setVisibility(View.GONE);
                            translate=translate-1;
                        }




                    }
                });





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
        return (null != icList ? icList.size() : 0);
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
