package com.example.globaltalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Follower_list_Activity extends AppCompatActivity {

    private static String TAG = "phptest";

    String EmailHolder;

    private String mJsonString;

    private ArrayList<FollowerData> fArrayList;
    private FollowerAdapter fAdapter;
    private RecyclerView fRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list_);

        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra("Email");
        Log.d("이메일 받기", EmailHolder);


        //mTextViewResult = (TextView) rootView.findViewById(R.id.textView_main_result);
        fRecyclerView = (RecyclerView) findViewById(R.id.listView_follower_list);
        fRecyclerView.setLayoutManager(new LinearLayoutManager(this));





        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        fArrayList = new ArrayList<>();

        fAdapter = new FollowerAdapter(this, fArrayList);
        fRecyclerView.setAdapter(fAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        fRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));



    }


    @Override
    public void onResume() {
        super.onResume();

        fArrayList.clear();
        fAdapter.notifyDataSetChanged();




        //데이터 받아오기
        GetData task = new GetData();
        task.execute(EmailHolder);
    }



    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            //String serverURL = params[0];
            //String postParameters = params[1];

            String searchKeyword1 = params[0];

            String serverURL = "http://54.180.122.247/global_communication/follower_list.php";
            String postParameters = "follower_email=" + searchKeyword1 ;


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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="webnautes";

        String TAG_EMAIL ="email";
        String TAG_IMAGE ="image";
        String TAG_NAME = "name";
        String TAG_TEACH ="teach";
        String TAG_LEARN ="learn";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String email = item.getString(TAG_EMAIL);
                String image = item.getString(TAG_IMAGE);
                String name = item.getString(TAG_NAME);
                String teach = item.getString(TAG_TEACH);
                String learn = item.getString(TAG_LEARN);

                FollowerData followerData = new FollowerData();

                followerData.setMember_email(email);
                followerData.setMember_image(image);
                followerData.setMember_name(name);
                followerData.setMember_teach(teach);
                followerData.setMember_learn(learn);

                fArrayList.add(followerData);
                fAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }








}
