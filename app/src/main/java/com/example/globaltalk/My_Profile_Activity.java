package com.example.globaltalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class My_Profile_Activity extends AppCompatActivity {

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

    ProgressDialog progressDialog ;
    String mJsonString;

    String EmailHolder;

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;



    //프로필 사진
    private ImageView friend_image;

    //이름
    private TextView friend_name;
    //나이
    private TextView friend_age;

    //가르칠 언어
    private TextView friend_teach;
    //배울 언어
    private TextView friend_learn;

    private TextView friends_intro;

    private TextView friend_follower_number;
    private TextView friend_following_number;

    public static final String UserEmail = "";

    private ImageView friend_img1;
    private ImageView friend_img2;
    private ImageView friend_img3;

    //private Button button_change;

    String f_follower;
    String f_following;

    //프로필 수정 버튼
    private ImageView edit_profile_button1;

    //개인 방송 버튼
    private ImageView start_streaming_button1;

    //다이얼로그 창에 입력하는 방 제목
    String room_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();
        //친구의 이메일
        EmailHolder = intent.getStringExtra("My_email");
        Log.d("이메일 받아오기",EmailHolder);

        friend_image = (ImageView) findViewById(R.id.friend_image);

        friend_name = (TextView) findViewById(R.id.friend_name);
        friend_age = (TextView) findViewById(R.id.friend_age);

        friends_intro = (TextView) findViewById(R.id.friends_intro);

        edit_profile_button1 = findViewById(R.id.edit_profile_button1);
        start_streaming_button1 = findViewById(R.id.start_streaming_button1);



        //pf_sex = (TextView)rootView.findViewById(R.id.pf_sex);
        //pf_email = (TextView)rootView.findViewById(R.id.pf_email);
        friend_teach = (TextView) findViewById(R.id.friend_teach);
        friend_learn = (TextView) findViewById(R.id.friend_learn);

        friend_img1 = (ImageView)findViewById(R.id.friend_img1);
        friend_img2 = (ImageView)findViewById(R.id.friend_img2);
        friend_img3 = (ImageView)findViewById(R.id.friend_img3);


        friend_following_number = (TextView) findViewById(R.id.friend_following_number);
        friend_follower_number = (TextView) findViewById(R.id.friend_follower_number);



    }



    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume 호출 됨",Toast.LENGTH_LONG).show();

        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/friendpage.php", "");

        friend_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(My_Profile_Activity.this, Profile_Image_Activity.class);

                intent.putExtra(UserEmail, EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent);

            }
        });


        edit_profile_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent5 = new Intent(My_Profile_Activity.this, MypageActivity.class);

                intent5.putExtra(UserEmail, EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent5);
            }
        });




        //스트리밍 버튼
        start_streaming_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 방 제목을 입력하는 다이얼로그 창
                AlertDialog.Builder ad = new AlertDialog.Builder(My_Profile_Activity.this);

                ad.setTitle("방 제목");       // 제목 설정
                ad.setMessage("방송 제목을 설정해주세요");   // 내용 설정

                // EditText 삽입하기
                final EditText et = new EditText(My_Profile_Activity.this);
                ad.setView(et);


                // 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        room_name = et.getText().toString();
                        Toast.makeText(My_Profile_Activity.this, "방 제목 : " +room_name + " 스트리밍 시작", Toast.LENGTH_SHORT).show();

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

                        Toast.makeText(My_Profile_Activity.this, "취소하셨습니다", Toast.LENGTH_SHORT).show();
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






        //나를 팔로우 한사람 목록    following목록에 내가 포함된 리스트     select * from global where following목록 like %,나의이메일%
        friend_follower_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent5 = new Intent(My_Profile_Activity.this, Follower_list_Activity.class);


                //이메일을 던지고 그에 해당하는 것들을 조건문으로 걸러낸 뒤에 recyclerview에 뿌려주기
                intent5.putExtra("Email", EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent5);

            }
        });


        friend_following_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent19 = new Intent(My_Profile_Activity.this, Following_list_Activity.class);


                //이메일을 던지고 그에 해당하는 것들을 조건문으로 걸러낸 뒤에 recyclerview에 뿌려주기
                intent19.putExtra("Email", EmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent19);

            }
        });






    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(My_Profile_Activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {

                friend_name.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/friendpage.php";
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

                f_follower = item.getString(TAG_FOLLOWER);
                f_following = item.getString(TAG_FOLLOWING);
                int f_follower_count = item.getInt(TAG_FOLLOWER_COUNT);
                int f_following_count = item.getInt(TAG_FOLLOWING_COUNT);





                Glide.with(this)
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_image);

                Glide.with(this)
                        .load(image1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img1);

                Glide.with(this)
                        .load(image2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img2);

                Glide.with(this)
                        .load(image3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img3);

                //pf_email.setText(email);
                friend_name.setText(name);
                friend_age.setText(age);
                //pf_sex.setText(sex);
                friend_teach.setText(teach);
                friend_learn.setText(learn);

                friend_follower_number.setText(String.valueOf(f_follower_count));
                friend_following_number.setText(String.valueOf(f_following_count));

                if(content.equals("null")){
                    friends_intro.setText("");
                }else{
                    friends_intro.setText(content);
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
                Toast.makeText(My_Profile_Activity.this, chatResponseMsg, Toast.LENGTH_LONG).show();

                //인텐트로 던지기
                Intent intent = new Intent(My_Profile_Activity.this, Streaming_Activity.class);

                intent.putExtra("streaming_no", chatResponseMsg );
                intent.putExtra("room_name", room_name );
                intent.putExtra("StreamName", EmailHolder );


                int idx = EmailHolder.indexOf("@");
                String NameHolder = EmailHolder.substring(0, idx);

                intent.putExtra("Streamer", NameHolder);



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
