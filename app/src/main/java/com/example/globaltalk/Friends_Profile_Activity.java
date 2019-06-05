package com.example.globaltalk;

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

public class Friends_Profile_Activity extends AppCompatActivity {

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


    private static final String TAG_IMAGE1 ="image1";
    private static final String TAG_IMAGE2 ="image2";
    private static final String TAG_IMAGE3 ="image3";

    String Profile_image1 = "Profile_image1" ;
    String Profile_image2 = "Profile_image2" ;
    String Profile_image3 = "Profile_image3" ;

    ProgressDialog progressDialog ;
    String mJsonString;

    String EmailHolder;

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

    public static final String UserEmail = "";

    private ImageView friend_img1;
    private ImageView friend_img2;
    private ImageView friend_img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends__profile_);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra(Tab4.FriendEmail);
        Log.d("이메일 받아오기",EmailHolder);

        friend_image = (ImageView) findViewById(R.id.friend_image);

        friend_name = (TextView) findViewById(R.id.friend_name);
        friend_age = (TextView) findViewById(R.id.friend_age);
        //pf_sex = (TextView)rootView.findViewById(R.id.pf_sex);
        //pf_email = (TextView)rootView.findViewById(R.id.pf_email);
        friend_teach = (TextView) findViewById(R.id.friend_teach);
        friend_learn = (TextView) findViewById(R.id.friend_learn);

        friend_img1 = (ImageView)findViewById(R.id.friend_img1);
        friend_img2 = (ImageView)findViewById(R.id.friend_img2);
        friend_img3 = (ImageView)findViewById(R.id.friend_img3);

        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/friendpage.php", "");

        friend_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Friends_Profile_Activity.this, Profile_Image_Activity.class);

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

                String image1 = item.getString(TAG_IMAGE1);
                String image2 = item.getString(TAG_IMAGE2);
                String image3 = item.getString(TAG_IMAGE3);


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


                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
