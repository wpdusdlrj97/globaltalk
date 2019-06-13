package com.example.globaltalk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TTSActivity extends AppCompatActivity {

    private NaverTTSTask mNaverTTSTask;
    String[] mTextString;
    EditText etText;
    Button btTTS, btReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

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
                    mNaverTTSTask = new NaverTTSTask();
                    mNaverTTSTask.execute(mTextString);
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


    private class NaverTTSTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            APIExamTTS.main(mTextString);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //방금 받은 파일명의 mp3가 있으면 플레이 시키자. 맞나 여기서 하는거?
            //아닌가 파일을 만들고 바로 실행되게 해야 하나? AsyncTask 백그라운드 작업중에...?

        }
    }
}
