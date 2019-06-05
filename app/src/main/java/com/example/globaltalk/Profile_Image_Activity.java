package com.example.globaltalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

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

public class Profile_Image_Activity extends AppCompatActivity {

    private static String TAG = "phpquerytest";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL ="email";
    private static final String TAG_IMAGE ="image";
    private static final String TAG_AGE ="age";
    private static final String TAG_SEX ="sex";
    private static final String TAG_TEACH ="teach";
    private static final String TAG_LEARN ="learn";

    String Profile_image = "Profile_image" ;
    String Profile_email = "Profile_email" ;
    String Profile_password = "Profile_password" ;
    String Profile_name = "Profile_name" ;
    String Profile_age = "Profile_age" ;
    String Profile_sex = "Profile_sex" ;
    String Profile_teach = "Profile_teach" ;
    String Profile_learn = "Profile_learn" ;


    private ImageView big_pf_image;

    String mJsonString;

    String EmailHolder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__image_);

        big_pf_image = (ImageView)findViewById(R.id.big_pf_image);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra(UserLoginActivity.UserEmail);

        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/mypage.php", "");


    }


    class GetData extends AsyncTask<String, Void, String> {



        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Profile_Image_Activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                //pf_name.setText(errorString);
            }
            else {

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
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


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String image = item.getString(TAG_IMAGE);



                Glide.with(this)
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .fitCenter()
                        .into(big_pf_image);





                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
