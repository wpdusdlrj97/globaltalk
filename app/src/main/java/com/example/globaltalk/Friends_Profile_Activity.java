package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Friends_Profile_Activity extends AppCompatActivity {


    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

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

    private static final String TAG_FOLLOWER = "follower";
    private static final String TAG_FOLLOWING = "following";
    private static final String TAG_FOLLOWER_COUNT = "follower_count";
    private static final String TAG_FOLLOWING_COUNT = "following_count";


    private static final String TAG_IMAGE1 = "image1";
    private static final String TAG_IMAGE2 = "image2";
    private static final String TAG_IMAGE3 = "image3";

    //String Profile_image1 = "Profile_image1";
    //String Profile_image2 = "Profile_image2";
    //String Profile_image3 = "Profile_image3";

    String f_id;
    String f_name;
    //String email = item.getString(TAG_EMAIL);

    String f_image;
    String f_age;
    //String sex = item.getString(TAG_SEX);
    String f_teach;
    String f_learn;

    String f_content;

    String f_follower;
    String f_following;
    int f_follower_count;
    int f_following_count;


    String f_image1;
    String f_image2;
    String f_image3;



    String mJsonString;

    String FriendEmailHolder;
    String LoginEmailHolder;

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

    private TextView friend_following_count;
    private TextView friend_follower_count;

    public static final String UserEmail = "";

    private ImageView friend_img1;
    private ImageView friend_img2;
    private ImageView friend_img3;

    private ImageView friends_message;
    private ImageView friends_follow;
    private TextView friends_follow_text;


    int Follow_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends__profile_);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();

        //친구의 이메일
        LoginEmailHolder = intent.getStringExtra("Login_email");
        Log.d("로그인 이메일 받아오기", LoginEmailHolder);

        //친구의 이메일
        FriendEmailHolder = intent.getStringExtra("Friend_email");
        Log.d("친구 이메일 받아오기", FriendEmailHolder);


        friend_image = (ImageView) findViewById(R.id.friend_image);

        friend_name = (TextView) findViewById(R.id.friend_name);
        friend_age = (TextView) findViewById(R.id.friend_age);

        friends_intro = (TextView) findViewById(R.id.friends_intro);


        //pf_sex = (TextView)rootView.findViewById(R.id.pf_sex);
        //pf_email = (TextView)rootView.findViewById(R.id.pf_email);
        friend_teach = (TextView) findViewById(R.id.friend_teach);
        friend_learn = (TextView) findViewById(R.id.friend_learn);

        friend_learn = (TextView) findViewById(R.id.friend_learn);

        friend_following_count = (TextView) findViewById(R.id.friend_following_count);
        friend_follower_count = (TextView) findViewById(R.id.friend_follower_count);


        friend_img1 = (ImageView) findViewById(R.id.friend_img1);
        friend_img2 = (ImageView) findViewById(R.id.friend_img2);
        friend_img3 = (ImageView) findViewById(R.id.friend_img3);


        friends_message = (ImageView) findViewById(R.id.friends_message);

        friends_follow = (ImageView) findViewById(R.id.friends_follow);
        friends_follow_text = (TextView) findViewById(R.id.friends_follow_text);



    }


    @Override
    public void onResume() {
        super.onResume();

        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/friendpage.php", "");


        //프로필이미지 확대해보기
        friend_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Friends_Profile_Activity.this, Profile_Image_Activity.class);

                intent.putExtra(UserEmail, FriendEmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent);

            }
        });


        //메시지 보내기 클릭
        friends_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //채팅 액티비티로 이동

                //이미 있는 채팅방인지는 서버에서 확인
                //서버로 값 보내기 - 나의이메일+상대방이메일

                //기존 방이 있는지 확인 (나의 이메일과 상대방 이메일로) -> 있다면 방번호가져와서 인텐트로 넘기기

                ChatKey_Function(LoginEmailHolder, FriendEmailHolder);

            }
       });


        friends_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //해당 회원의 팔로워 리스트/수 증가, 로그인유저의 팔로잉 리스트/수 증가

                //서버

                //클라이언트


                //이미 팔로우를 했을 경우 한번 더 누르면 팔로우해제
                if (Follow_button==1) { // 친구의 팔로잉 목록에 해당로그인한 사람의 이메일이 포함됐을 경우

                    //Toast.makeText(Friends_Profile_Activity.this, "이미 팔로우하셨습니다", Toast.LENGTH_LONG).show();
                    Glide.with(Friends_Profile_Activity.this)
                            .load(R.drawable.follow)
                            .into(friends_follow);
                    friends_follow_text.setText("팔로우");

                    //팔로우 버튼 해제
                    Follow_button=Follow_button-1;
                    //클라이언트 딴 친구의 팔로잉 -1
                    f_follower_count=f_follower_count-1;
                    friend_follower_count.setText(String.valueOf(f_follower_count));

                    //서버    해당 회원의 팔로워 리스트/수 감소, 로그인유저의 팔로잉 리스트/수 증가
                    Follow_Undo_Function(LoginEmailHolder, FriendEmailHolder);


                } else {//아직 팔로우를 안했을 경우 누르면 팔로우

                    //Toast.makeText(Friends_Profile_Activity.this, "팔로우하시겠습니까", Toast.LENGTH_LONG).show();
                    Glide.with(Friends_Profile_Activity.this)
                            .load(R.drawable.check)
                            .into(friends_follow);
                    friends_follow_text.setText("팔로잉 중");

                    //팔로우 버튼 등록
                    Follow_button=Follow_button+1;
                    //클라이언트 딴 친구의 팔로잉 +1
                    f_follower_count=f_follower_count+1;
                    friend_follower_count.setText(String.valueOf(f_follower_count));


                    //서버    해당 회원의 팔로워 리스트/수 증가, 로그인유저의 팔로잉 리스트/수 증가
                    Follow_Do_Function(LoginEmailHolder, FriendEmailHolder);



                }



            }
        });


        //나를 팔로우 한사람 목록    following목록에 내가 포함된 리스트     select * from global where following목록 like %,나의이메일%
        friend_follower_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent5 = new Intent(Friends_Profile_Activity.this, Follower_list_Activity.class);


                //이메일을 던지고 그에 해당하는 것들을 조건문으로 걸러낸 뒤에 recyclerview에 뿌려주기
                intent5.putExtra("Email", FriendEmailHolder);
                //intent.putExtra(UserEmail, email);
                startActivity(intent5);

            }
        });


        friend_following_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent19 = new Intent(Friends_Profile_Activity.this, Following_list_Activity.class);


                //이메일을 던지고 그에 해당하는 것들을 조건문으로 걸러낸 뒤에 recyclerview에 뿌려주기
                intent19.putExtra("Email", FriendEmailHolder);
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

            progressDialog = ProgressDialog.show(Friends_Profile_Activity.this,
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
            String postParameters = "email=" + FriendEmailHolder;


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

                f_id = item.getString(TAG_ID);
                f_name = item.getString(TAG_NAME);
                //String email = item.getString(TAG_EMAIL);

                f_image = item.getString(TAG_IMAGE);
                f_age = item.getString(TAG_AGE);
                //String sex = item.getString(TAG_SEX);
                f_teach = item.getString(TAG_TEACH);
                f_learn = item.getString(TAG_LEARN);

                f_content = item.getString(TAG_CONTENT);

                f_follower = item.getString(TAG_FOLLOWER);
                f_following = item.getString(TAG_FOLLOWING);
                f_follower_count = item.getInt(TAG_FOLLOWER_COUNT);
                f_following_count = item.getInt(TAG_FOLLOWING_COUNT);


                f_image1 = item.getString(TAG_IMAGE1);
                f_image2 = item.getString(TAG_IMAGE2);
                f_image3 = item.getString(TAG_IMAGE3);


                Glide.with(this)
                        .load(f_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_image);

                Glide.with(this)
                        .load(f_image1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img1);

                Glide.with(this)
                        .load(f_image2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img2);

                Glide.with(this)
                        .load(f_image3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(friend_img3);

                //pf_email.setText(email);
                friend_name.setText(f_name);
                friend_age.setText(f_age);
                //pf_sex.setText(sex);
                friend_teach.setText(f_teach);
                friend_learn.setText(f_learn);

                friend_follower_count.setText(String.valueOf(f_follower_count));
                friend_following_count.setText(String.valueOf(f_following_count));


                if (f_content.equals("null")) {
                    friends_intro.setText("");
                } else {
                    friends_intro.setText(f_content);
                }


                //팔로우 여부

                //이미 팔로우를 했을 경우 한번 더 누르면 팔로우해제
                if (f_follower.contains(LoginEmailHolder)) { // 친구의 팔로워 목록에 해당로그인한 사람의 이메일이 포함됐을 경우
                    Glide.with(this)
                            .load(R.drawable.check)
                            .into(friends_follow);
                    friends_follow_text.setText("팔로잉 중");
                    Follow_button=1;
                } else {//아직 팔로우를 안했을 경우 누르면 팔로우
                    Glide.with(this)
                            .load(R.drawable.follow)
                            .into(friends_follow);
                    friends_follow_text.setText("팔로우");
                    Follow_button=0;
                }


                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }



    //친구 팔로우 함수
    public void Follow_Do_Function(final String login_id, final String friend_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 누르셨습니다' 토스트
                Toast.makeText(Friends_Profile_Activity.this, httpResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("login_id", params[0]);

                hashMap.put("friend_id", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/follow_do.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(login_id, friend_id);
    }


    //친구 언팔로우 함수
    public void Follow_Undo_Function(final String login_id, final String friend_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 누르셨습니다' 토스트
                Toast.makeText(Friends_Profile_Activity.this, httpResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("login_id", params[0]);

                hashMap.put("friend_id", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/follow_undo.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(login_id, friend_id);
    }





    // 메시지 보내기 DB값에 빈방 추가
    // 빈방을 추가하기 전에 이미 있는 방인지 확인한다
    public void ChatKey_Function(final String login_id, final String friend_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Friends_Profile_Activity.this, chatResponseMsg, Toast.LENGTH_LONG).show();

                //인텐트로 던지기
                Intent intent = new Intent(Friends_Profile_Activity.this, Chat9.class);

                intent.putExtra("room_id", chatResponseMsg );
                intent.putExtra("myemail", LoginEmailHolder );
                intent.putExtra("friend_name", f_name );

                String user_list = ","+LoginEmailHolder+","+FriendEmailHolder;

                intent.putExtra("user_list", user_list);

                startActivity(intent);



            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("login_id", params[0]);

                hashMap.put("friend_id", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/chat_key.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(login_id,friend_id);
    }



}
