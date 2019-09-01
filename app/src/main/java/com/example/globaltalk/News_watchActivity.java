package com.example.globaltalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.concurrent.ExecutionException;

public class News_watchActivity extends AppCompatActivity {


    String EmailHolder;
    private String TeachHolder;
    private String LearnHolder;
    String news_thumbnail;
    String news_title;
    String news_content;


    ImageView watch_news_back;
    Button watch_news_all_translate;
    ImageView watch_news_thumbnail;

    TextView watch_news_title;
    TextView watch_news_content;

    //번역
    String sourceHolder;
    String targetHolder;

    String sourceLang;
    String targetLang;


    int translate = 0;

    String clientId = "";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "";//애플리케이션 클라이언트 시크릿값";


    EditText get_translate;
    ImageView watch_news_part_translate;
    ImageView watch_news_part_sound;
    ImageView watch_news_part_reset;


    //TTS

    private NaverDetectionTask mNaverDetectionTask;
    private NaverTTSTask mNaverTTSTask;

    //TTS 대상
    String[] mTextString;
    static String speaker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_watch);


        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra("EmailHolder");
        Log.d("이메일 받기", EmailHolder);

        news_thumbnail = intent.getStringExtra("news_thumbnail");
        Log.d("뉴스 썸네일 받기", news_thumbnail);

        news_title = intent.getStringExtra("news_title");
        Log.d("뉴스 타이틀 받기", news_title);


        news_content = intent.getStringExtra("news_content");
        Log.d("뉴스 원문 받기", news_content);

        TeachHolder = intent.getStringExtra("TeachHolder");
        Log.d("Teach 받기", TeachHolder);

        LearnHolder = intent.getStringExtra("LearnHolder");
        Log.d("Learn 받기", LearnHolder);


        watch_news_back = findViewById(R.id.watch_news_back);
        watch_news_all_translate = findViewById(R.id.watch_news_all_translate);

        watch_news_title = findViewById(R.id.watch_news_title);
        watch_news_thumbnail = findViewById(R.id.watch_news_thumbnail);
        watch_news_content = findViewById(R.id.watch_news_content);

        get_translate = findViewById(R.id.get_translate);
        watch_news_part_translate = findViewById(R.id.watch_news_part_translate);
        watch_news_part_sound = findViewById(R.id.watch_news_part_sound);
        watch_news_part_reset = findViewById(R.id.watch_news_part_reset);

        //번역 전 -> 학습 언어
        sourceHolder = LearnHolder;
        //번역 후 -> 가르칠 언어
        targetHolder = TeachHolder;


        //영어 일때
        if (sourceHolder.equals("English")) {
            sourceLang = "en";
            //speaker="clara";

        }

        //한국어 일때
        if (sourceHolder.equals("Korean")) {
            sourceLang = "ko";
            //speaker="mijin";
        }

        //일어 일때
        if (sourceHolder.equals("Japanese")) {
            sourceLang = "ja";
            //speaker="yuri";
        }

        //중국어 일때
        if (sourceHolder.equals("Chinese")) {
            sourceLang = "zh-CN";
            //speaker="meimei";
        }


        //영어 일때
        if (targetHolder.equals("English")) {
            targetLang = "en";

        }

        //한국어 일때
        if (targetHolder.equals("Korean")) {
            targetLang = "ko";

        }

        //일어 일때
        if (targetHolder.equals("Japanese")) {
            targetLang = "ja";

        }

        //중국어 일때
        if (targetHolder.equals("Chinese")) {
            targetLang = "zh-CN";

        }


    }


    @Override
    public void onResume() {

        super.onResume();


        watch_news_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        watch_news_title.setText(news_title);

        watch_news_content.setText(news_content);

        Glide.with(this)
                .load(news_thumbnail)
                .placeholder(R.drawable.news)
                .thumbnail(0.9f)
                .fitCenter()
                .into(watch_news_thumbnail);

        watch_news_all_translate.setText("전체번역");




        watch_news_all_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //TextHolder = bList.get(position).getcontent();
                //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                TranslateTask translateTask = new TranslateTask();
                TranslateTask9 translateTask9 = new TranslateTask9();

                String sTitle = news_title;
                String sText = news_content;


                //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                // 번역을 누르기 전
                if (translate == 0) {
                    try {
                        //데이터를 translateAsynctask로부터 가져온다
                        String translate_title = translateTask.execute(sTitle).get();
                        String translate = translateTask9.execute(sText).get();


                        //뉴스 제목
                        //JSON데이터를 자바객체로 변환해야 한다.
                        //Gson을 사용할 것이다.
                        Gson gson_title = new GsonBuilder().create();
                        JsonParser parser_title = new JsonParser();
                        JsonElement rootObj_title = parser_title.parse(translate_title)
                                //원하는 데이터 까지 찾아 들어간다.
                                .getAsJsonObject().get("message")
                                .getAsJsonObject().get("result");
                        //안드로이드 객체에 담기
                        TranslatedItem items_title = gson_title.fromJson(rootObj_title.toString(), TranslatedItem.class);


                        //뉴스 본문
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
                        watch_news_title.setText(items_title.getTranslatedText());
                        watch_news_content.setText(items.getTranslatedText());


                        watch_news_all_translate.setText("번역풀기");


                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    translate = translate + 1;
                } else {
                    watch_news_title.setText(news_title);
                    watch_news_content.setText(news_content);

                    watch_news_all_translate.setText("전체번역");

                    translate = translate - 1;
                }


            }
        });



        //부분 번역 리셋
        watch_news_part_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_translate.setText("");
                get_translate.setHint("번역할 텍스트를 입력하세요.");
            }
        });



        //부분 번역
        watch_news_part_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (get_translate.getText().length() > 0) { //한글자 이상 1

                    TranslateTask translateTask = new TranslateTask();
                    String sText_part = get_translate.getText().toString();

                    //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                    try {
                        //데이터를 translateAsynctask로부터 가져온다
                        String translate = translateTask.execute(sText_part).get();

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
                        //viewholder.streamer_mTextView_translate.setText(items.getTranslatedText());


                        AlertDialog.Builder builder = new AlertDialog.Builder(News_watchActivity.this);

                        builder.setMessage(sText_part+"\n\n"+"                                       ▼"+"\n\n"+items.getTranslatedText())
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // 네 클릭
                                            }
                                        });

                        AlertDialog alert = builder.create();
                        // 대화창 클릭시 뒷 배경 어두워지는 것 막기
                        //alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        // 대화창 제목 설정

                        alert.setTitle("[번역완료]    "+LearnHolder + "=>"+ TeachHolder);
                        // 대화창 아이콘 설정
                        alert.setIcon(R.drawable.translate_black);
                        // 대화창 배경 색  설정



                        alert.show();


                        //builder.setTitle("[번역완료]    "+LearnHolder + "=>"+ TeachHolder).setMessage(items.getTranslatedText());
                        //AlertDialog alertDialog = builder.create();
                        //alertDialog.show();




                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(News_watchActivity.this, "텍스트를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }



            }
        });



        watch_news_part_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 입력한 텍스트를 이 배열변수에 담는다.
                String mText;
                if (get_translate.getText().length() > 0) { //한글자 이상 1
                    mText = get_translate.getText().toString();
                    mTextString = new String[]{mText};

                    //AsyncTask 실행
                    mNaverDetectionTask = new NaverDetectionTask();
                    mNaverDetectionTask.execute(mTextString);
                    Log.d("TTS 실행",mText);
                } else {
                    Toast.makeText(News_watchActivity.this, "텍스트를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });



    }




    //자바용 그릇
    private class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }


    class TranslateTask extends AsyncTask<String, Void, String> {

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
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
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

    class TranslateTask9 extends AsyncTask<String, Void, String> {

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
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
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
