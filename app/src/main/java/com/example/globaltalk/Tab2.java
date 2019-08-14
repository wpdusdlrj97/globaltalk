package com.example.globaltalk;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab2 extends Fragment {

    Button btTranslate;
    EditText etSource;
    TextView tvResult;

    static String register_source;
    static String register_target;



    String sourceLang;
    String targetLang;


    Spinner spinner_source;
    Spinner spinner_target;



    Button image_translate;
    Button button_tts;

    String[] mTextString;
    String[] rTextString;

    private NaverTTSTask mNaverTTSTask;

    static String speaker;


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes


        View rootView = inflater.inflate(R.layout.tab2, container, false);

        etSource = (EditText) rootView.findViewById(R.id.et_source);
        tvResult = (TextView) rootView.findViewById(R.id.tv_result_image);
        btTranslate = (Button) rootView.findViewById(R.id.bt_translate);

        //번역 대상
        spinner_source =rootView.findViewById(R.id.spinner_source);

        //번역 결과
        spinner_target = rootView.findViewById(R.id.spinner_target);

        //번역 버튼
        image_translate = (Button) rootView.findViewById(R.id.image_translate);

        //TTS 화면 버튼
        button_tts = (Button) rootView.findViewById(R.id.button_tts);



        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume 호출 됨",Toast.LENGTH_LONG).show();


        etSource.setText("");
        tvResult.setText("");

        //번역 실행버튼 클릭이벤트
        btTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //소스에 입력된 내용이 있는지 체크하고 넘어가자.
                if(etSource.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    etSource.requestFocus();
                    return;
                }

                if(register_source.equals(register_target)){
                    Toast.makeText(getActivity(), "같은 언어로 번역할 수 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                //실행버튼을 클릭하면 AsyncTask를 이용 요청하고 결과를 반환받아서 화면에 표시하도록 해보자.
                //NaverTranslateTask asyncTask = new NaverTranslateTask();
                //String sText = etSource.getText().toString();
                //asyncTask.execute(sText);

                TranslateTask translateTask = new TranslateTask();
                String sText = etSource.getText().toString();
                translateTask.execute(sText);
            }
        });


        /*
        //번역대상 TTS
        source_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사용자가 입력한 텍스트를 이 배열변수에 담는다.
                String mText;
                if (etSource.getText().length() > 0) { //한글자 이상 1
                    mText = etSource.getText().toString();
                    mTextString = new String[]{mText};

                    //AsyncTask 실행
                    mNaverTTSTask = new NaverTTSTask();
                    mNaverTTSTask.execute(mTextString);
                    Log.d("TTS 실행",mText);
                } else {
                    Toast.makeText(getActivity(), "텍스트를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }




            }
        });
        */


        /*
        //번역대상 TTS
        target_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 입력한 텍스트를 이 배열변수에 담는다.
                String rText;
                if (tvResult.getText().length() > 0) { //한글자 이상 1
                    rText = tvResult.getText().toString();
                    rTextString = new String[]{rText};

                    //AsyncTask 실행
                    mNaverTTSTask = new NaverTTSTask();
                    mNaverTTSTask.execute(rTextString);
                    Log.d("TTS 실행",rText);
                } else {
                    Toast.makeText(getActivity(), "텍스트를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }



            }
        });
        */




        spinner_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                register_source = (String) parent.getItemAtPosition(position);

                //영어 일때
                if(register_source.equals("English")){
                    sourceLang="en";
                    speaker="clara";

                }

                //한국어 일때
                if(register_source.equals("Korean")){
                    sourceLang="ko";
                    speaker="mijin";
                }

                //한국어 일때
                if(register_source.equals("Japanese")){
                    sourceLang="ja";
                    speaker="yuri";
                }

                //한국어 일때
                if(register_source.equals("Chinese")){
                    sourceLang="zh-CN";
                    speaker="meimei";
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_target.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                register_target = (String) parent.getItemAtPosition(position);

                //영어 일때
                if(register_target.equals("English")){
                    targetLang="en";

                }

                //한국어 일때
                if(register_target.equals("Korean")){
                    targetLang="ko";

                }

                //한국어 일때
                if(register_target.equals("Japanese")){
                    targetLang="ja";

                }

                //한국어 일때
                if(register_target.equals("Chinese")){
                    targetLang="zh-CN";

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







        image_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ImageTranslateActivity.class);

                //intent.putExtra(UserEmail, email);
                startActivity(intent);
            }
        });



        button_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), TTSActivity.class);

                //intent.putExtra(UserEmail, EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent);
            }
        });


    }

    //번역 대상 TTS
    private class NaverTTSTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            APIExamTTS.main(mTextString);

            //APIExamDetectLangs.main(mTextString);

            //영어 일때
            if(register_source.equals("English")){
                speaker="clara";

            }

            //한국어 일때
            if(register_source.equals("Korean")){
                speaker="mijin";
            }

            //한국어 일때
            if(register_source.equals("Japanese")){
                speaker="yuri";
            }

            //한국어 일때
            if(register_source.equals("Chinese")){
                speaker="meimei";
            }


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

    // 번역 대상 텍스트 읽기 API
    public static class APIExamTTS {

        private static String TAG = "APIExamTTS";


        public static void main(String[] args) {

            try {
                String text = URLEncoder.encode(args[0], "UTF-8"); // 13자
                String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                // post request
                String postParams = "speaker=clara&speed=0&text=" + text;
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

            //JSON데이터를 자바객체로 변환해야 한다.
            //Gson을 사용할 것이다.


            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s)
                    //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            //Log.d("result", items.getTranslatedText());
            //번역결과를 텍스트뷰에 넣는다.
            tvResult.setText(items.getTranslatedText());

        }


        //자바용 그릇
        private class TranslatedItem {
            String translatedText;

            public String getTranslatedText() {
                return translatedText;
            }
        }


    }







}
