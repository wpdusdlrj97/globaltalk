package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Board_writeActivity extends AppCompatActivity {

    Bitmap bitmap;
    ProgressDialog progressDialog ;

    boolean check = true;


    //뒤로가기, 업로드 버튼
    ImageView board_write_back,board_write_upload;

    // 입력사항 -> 이메일, 비밀번호, 비밀번호 확인, 이름, 나이
    EditText board_write_text;

    //추가할 콘텐츠 (이미지, 카메라, 영상, 음성녹음, 번역)
    ImageView board_write_image,board_write_camera,board_write_video,board_write_mic,board_write_translate;


    //업로드할 이미지 1~10
    ImageView board_write_image1;

    String text_content;
    String board_email = "board_email" ;
    String board_text = "board_text" ;
    String board_image1="board_image1";

    //텍스트만 올릴경우
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    public static final String UserEmail = "";
    String finalResult;



    String BoardUploadPath ="http://54.180.122.247/test/register.php" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        // 뒤로가기, 업로드 버튼
        board_write_back = (ImageView)findViewById(R.id.board_write_back);
        board_write_upload = (ImageView)findViewById(R.id.board_write_upload);

        // 추가 콘텐츠
        board_write_image = (ImageView)findViewById(R.id.board_write_image);
        board_write_camera = (ImageView)findViewById(R.id.board_write_camera);
        board_write_video = (ImageView)findViewById(R.id.board_write_video);
        board_write_mic = (ImageView)findViewById(R.id.board_write_mic);
        board_write_translate = (ImageView)findViewById(R.id.board_write_translate);

        //게시글 내용
        board_write_text = (EditText)findViewById(R.id.board_write_text);


        //추가된 사진
        board_write_image1  = (ImageView)findViewById(R.id.board_write_image1);


        //뒤로가기 버튼
        board_write_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //업로드 버튼
        board_write_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //입력된 내용
                text_content = board_write_text.getText().toString();

                //아무 내용도 입력되지 않았을 때
                if(TextUtils.isEmpty(text_content))
                {
                    Toast.makeText(Board_writeActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    //사진을 등록하지 않았을 때
                    if(bitmap == null) {

                        TextUploadFunction("soccer@naver.com",text_content);

                    } else { //사진을 등록했을 때

                        ImageUploadToServerFunction();
                    }

                }




            }
        });

        //프로필 사진 선택
        board_write_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

            }
        });





    }

    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                board_write_image1.setImageBitmap(bitmap);


            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    //로그인 함수
    public void TextUploadFunction(final String email, final String text) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Board_writeActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("업로드 완료")) {

                    finish();

                    //Intent intent = new Intent(Board_writeActivity.this, BoardActivity.class);

                    //로그인 함수 시 괄호 안에 넣었던 변수 email
                    //intent.putExtra(UserEmail, email);
                    //startActivity(intent);


                } else {

                    Toast.makeText(Board_writeActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("email", params[0]);

                hashMap.put("text", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/test/UserLogin.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(email, text);
    }






    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(Board_writeActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(Board_writeActivity.this,string1,Toast.LENGTH_LONG).show();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (string1.equalsIgnoreCase("업로드 완료")) {

                    finish();

                    //Intent intent = new Intent(Board_writeActivity.this, BoardActivity.class);

                    //startActivity(intent);

                }


                // Setting image as transparent after done uploading.
                // register_image.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(board_email, "egg@naver.com");

                HashMapParams.put(board_text, text_content);

                HashMapParams.put(board_image1, ConvertImage);


                String FinalData = imageProcessClass.ImageHttpRequest(BoardUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }





}
