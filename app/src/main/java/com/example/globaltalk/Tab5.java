package com.example.globaltalk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab5 extends Fragment {




    private static String TAG = "phpquerytest";

    private static final String TAG_JSON = "webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_AGE = "age";
    private static final String TAG_SEX = "sex";
    private static final String TAG_TEACH = "teach";
    private static final String TAG_LEARN = "learn";
    private static final String TAG_CONTENT = "content";

    private static final String TAG_IMAGE1 ="image1";
    private static final String TAG_IMAGE2 ="image2";
    private static final String TAG_IMAGE3 ="image3";

    private static final String TAG_FOLLOWER = "follower";
    private static final String TAG_FOLLOWING = "following";
    private static final String TAG_FOLLOWER_COUNT = "follower_count";
    private static final String TAG_FOLLOWING_COUNT = "following_count";



    String Profile_image1 = "Profile_image1" ;
    String Profile_image2 = "Profile_image2" ;
    String Profile_image3 = "Profile_image3" ;

    String Profile_email = "Profile_email" ;
    String Profile_image = "Profile_image" ;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private Boolean isCamera = false;
    Bitmap bitmap;
    ProgressDialog progressDialog ;
    boolean check = true;

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;



    //프로필 사진
    private ImageView mypage_image;

    //프로필 수정 버튼
    private ImageView edit_profile_button;

    //개인 방송 버튼
    private ImageView start_streaming_button;

    //이름
    private TextView mypage_name;
    //나이
    private TextView mypage_age;
    //성별
    private TextView pf_sex;
    //이메일
    private TextView pf_email;
    //가르칠 언어
    private TextView mypage_teach;
    //배울 언어
    private TextView mypage_learn;
    //자기소개
    private TextView mypage_intro;

    private TextView mypage_following_number;

    private TextView mypage_follower_number;


    String mJsonString;
    private String EmailHolder;
    public static final String UserEmail = "";

    //Button button_logout1;
    //Button button_streaming;


    private ImageView pf_img1;
    private ImageView pf_img2;
    private ImageView pf_img3;


    //다이얼로그 창에 입력하는 방 제목
    String room_name;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
        }
        Log.d("이메일 받아오기",EmailHolder);




        View rootView = inflater.inflate(R.layout.tab5, container, false);

        mypage_image = (ImageView) rootView.findViewById(R.id.mypage_image);

        edit_profile_button = rootView.findViewById(R.id.edit_profile_button);

        mypage_name = (TextView) rootView.findViewById(R.id.mypage_name);
        mypage_age = (TextView) rootView.findViewById(R.id.mypage_age);
        //pf_sex = (TextView)rootView.findViewById(R.id.pf_sex);
        //pf_email = (TextView)rootView.findViewById(R.id.pf_email);
        mypage_teach = (TextView) rootView.findViewById(R.id.mypage_teach);
        mypage_learn = (TextView) rootView.findViewById(R.id.mypage_learn);

        mypage_following_number = (TextView) rootView.findViewById(R.id.mypage_following_number);
        mypage_follower_number = (TextView) rootView.findViewById(R.id.mypage_follower_number);


        mypage_intro = (TextView) rootView.findViewById(R.id.mypage_intro);

        pf_img1 = (ImageView)rootView.findViewById(R.id.pf_img1);
        pf_img2 = (ImageView)rootView.findViewById(R.id.pf_img2);
        pf_img3 = (ImageView)rootView.findViewById(R.id.pf_img3);

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes


        //개인 방송하기 버튼 클릭 시
        start_streaming_button = (ImageView) rootView.findViewById(R.id.start_streaming_button);



        return rootView;


    }



    //생명주기 활용 필요

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume 호출 됨",Toast.LENGTH_LONG).show();

        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/mypage.php", "");


        // 프로필 수정 버튼 클릭 시
        edit_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent5 = new Intent(getActivity(), MypageActivity.class);

                intent5.putExtra(UserEmail, EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent5);
            }
        });



        // 개인 방송하기 클릭 시
        start_streaming_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 방 제목을 입력하는 다이얼로그 창
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                ad.setTitle("방 제목");       // 제목 설정
                ad.setMessage("방송 제목을 설정해주세요");   // 내용 설정

                // EditText 삽입하기
                final EditText et = new EditText(getActivity());
                ad.setView(et);


                // 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        room_name = et.getText().toString();
                        Toast.makeText(getContext(), "방 제목 : " +room_name + " 스트리밍 시작", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();     //닫기


                        //확인 버튼을 누르면 서버로 가서 방을 생성하고 방번호를 받아온다
                        //이때 DB에 저장할 것 (방장 이메일, 방제목, Stream_Name, 시청자 목록, 시청자 수)
                        //받아온 방번호와 함께 방번호(for 채팅), 방제목, 방장이름(@빼고), StreamName(이메일)를 인텐트로 Streaming_Activity에 넘긴다
                        //방송 시작 버튼을 누르면 방이 만들어진다

                        Streaming_Key_Function();





                        //서버 측




                        // Event
                    }
                });


                // 취소 버튼 설정
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getContext(), "취소하셨습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });


                //창띄우기
                ad.show();


                // 서버 - DB에 방만들기
                //서버에 보낼 정보 -> 이메일, 본인이 설정한 방제목



            }
        });



        mypage_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Profile_Image_Activity.class);

                intent.putExtra(UserEmail, EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent);

            }
        });

    }









    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(),
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {

                mypage_name.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/mypage.php";
            String postParameters = "email=" + EmailHolder;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                //String email = item.getString(TAG_EMAIL);

                String image = item.getString(TAG_IMAGE);
                String age = item.getString(TAG_AGE);
                //String sex = item.getString(TAG_SEX);
                String teach = item.getString(TAG_TEACH);
                String learn = item.getString(TAG_LEARN);

                String content = item.getString(TAG_CONTENT);

                String image1 = item.getString(TAG_IMAGE1);
                String image2 = item.getString(TAG_IMAGE2);
                String image3 = item.getString(TAG_IMAGE3);


                String f_follower = item.getString(TAG_FOLLOWER);
                String f_following = item.getString(TAG_FOLLOWING);
                int f_follower_count = item.getInt(TAG_FOLLOWER_COUNT);
                int f_following_count = item.getInt(TAG_FOLLOWING_COUNT);


                Glide.with(this)
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(mypage_image);

                Glide.with(this)
                        .load(image1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(pf_img1);

                Glide.with(this)
                        .load(image2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(pf_img2);

                Glide.with(this)
                        .load(image3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(pf_img3);

                //pf_email.setText(email);
                mypage_name.setText(name);
                mypage_age.setText(age);
                //pf_sex.setText(sex);
                mypage_teach.setText(teach);
                mypage_learn.setText(learn);

                mypage_following_number.setText(String.valueOf(f_following_count));
                mypage_follower_number.setText(String.valueOf(f_follower_count));

                if(content.equals("null")){
                    mypage_intro.setText("");
                }else{
                    mypage_intro.setText(content);
                }






                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }






    // 스트리밍 시작 전 DB값에 빈방 추가
    // 빈방을 추가하기 전에 이미 있는 방인지 확인한다
    public void Streaming_Key_Function() {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(getActivity(), chatResponseMsg, Toast.LENGTH_LONG).show();


                Log.d("스트리밍 방 번호 가져오기",chatResponseMsg);



                //인텐트로 던지기
                Intent intent = new Intent(getActivity(), Streaming_Activity.class);

                intent.putExtra("streaming_no", chatResponseMsg );
                intent.putExtra("room_name", room_name );
                intent.putExtra("StreamName", EmailHolder );




                int idx = EmailHolder.indexOf("@");
                String NameHolder = EmailHolder.substring(0, idx);

                //intent.putExtra("Streamer", NameHolder);



                //필요한 정보들을 넘기고 방송을 시작했을 때 -> DB를 통해 방 만들기
                startActivity(intent);



            }

            @Override
            protected String doInBackground(String... params) {


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/streaming_key.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute();
    }





}