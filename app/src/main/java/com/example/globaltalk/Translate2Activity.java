package com.example.globaltalk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class Translate2Activity extends AppCompatActivity {

    Button btTranslate;
    EditText etSource;
    TextView tvResult;


    String clientId = "cv81DQOKSlPw4lX1hyEe";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "s3Rf0jQDf0";//애플리케이션 클라이언트 시크릿값";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);


        etSource = (EditText) findViewById(R.id.et_source);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btTranslate = (Button) findViewById(R.id.bt_translate);

        //번역 실행버튼 클릭이벤트
        btTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //소스에 입력된 내용이 있는지 체크하고 넘어가자.
                if(etSource.getText().toString().length() == 0) {
                    Toast.makeText(Translate2Activity.this, "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    etSource.requestFocus();
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



    }

    class TranslateTask extends AsyncTask<String,Void,String>{

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
                String postParams = "source=ko&target=en&text=" + text;
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
