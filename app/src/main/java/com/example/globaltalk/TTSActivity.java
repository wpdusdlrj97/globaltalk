package com.example.globaltalk;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class TTSActivity extends AppCompatActivity {

    //Speaker - 국적 별로 4명이기 때문에 언어감지 필요
    //네이버 언어감지로 언어를 감지한 후 -> speaker를 다르게 설정
    //
    //NaverLanguageDetection -> NaverTTS

    private NaverDetectionTask mNaverDetectionTask;
    private NaverTTSTask mNaverTTSTask;

    static TextView tv_title;
    String[] mTextString;
    EditText etText;
    Button btTTS, btReset;

    static String speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        tv_title=findViewById(R.id.tv_title);
        etText = (EditText) findViewById(R.id.et_text);
        btTTS = (Button) findViewById(R.id.bt_tts);
        btReset = (Button) findViewById(R.id.bt_reset);

        //버튼 클릭이벤트 - 클릭하면 에디터뷰에 있는 글자를 가져와서 네이버에 보낸다. MP3로 바꿔 달라고 ...
        btTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사용자가 입력한 텍스트를 이 배열변수에 담는다.
                String mText;
                if (etText.getText().length() > 0) { //한글자 이상 1
                    mText = etText.getText().toString();
                    mTextString = new String[]{mText};

                    //AsyncTask 실행
                    mNaverDetectionTask = new NaverDetectionTask();
                    mNaverDetectionTask.execute(mTextString);
                } else {
                    Toast.makeText(TTSActivity.this, "텍스트를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //리셋버튼
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etText.setText("");
                etText.setHint("텍스트를 입력하세요.");
            }
        });
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
                    tv_title.setText(language);

                    if(language.equals("ko")){

                        speaker="mijin";
                    }
                    if(language.equals("en")){

                        speaker="clara";
                    }
                    if(language.equals("ja")){

                        speaker="yuri";
                    }
                    if(language.equals("zh-CN")){

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
