package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

//Implementing the interface OnTabSelectedListener to our MainActivity
//This interface would help in swiping views
public class MainActivity extends AppCompatActivity {

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

    //로그인 시 받아온 이메일 값
    String EmailHolder;



    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;

    Bundle bundle;

    String mJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adding toolbar to the activity
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra(UserLoginActivity.UserEmail);

        GetData91 task91 = new GetData91();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task91.execute("http://54.180.122.247/global_communication/mypage.php", "");


    }



    class GetData91 extends AsyncTask<String, Void, String> {

        //ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog = ProgressDialog.show(MainActivity.this,
            //        "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //progressDialog.dismiss();
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


                String IDHolder= item.getString(TAG_ID);
                String NameHolder = item.getString(TAG_NAME);


                String ImageHolder = item.getString(TAG_IMAGE);
                String AgeHolder = item.getString(TAG_AGE);
                String SexHolder = item.getString(TAG_SEX);
                String TeachHolder= item.getString(TAG_TEACH);
                String LearnHolder = item.getString(TAG_LEARN);



                //번들로 PagerAdapter에 보내주기
                Bundle bundle = new Bundle();
                bundle.putString("EmailHolder", EmailHolder);
                bundle.putString("TeachHolder", TeachHolder);
                Log.d("티칭홀더",TeachHolder);
                bundle.putString("LearnHolder", LearnHolder);
                Log.d("러닝홀더",LearnHolder);
                bundle.putString("NameHolder", NameHolder);
                Log.d("이름홀더",NameHolder);
                bundle.putString("ImageHolder", ImageHolder);
                Log.d("사진홀더",ImageHolder);



                //밑에 있는 식에서 번들과 함께 보내기
                //Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(),bundle);



                //Initializing the tablayout
                tabLayout = (TabLayout) findViewById(R.id.tabLayout);

                //Adding the tabs using addTab() method
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector1));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector2));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector3));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector4));
                tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector5));
                //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_selector6));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                //Initializing viewPager
                viewPager = (ViewPager) findViewById(R.id.pager);

                //Creating our pager adapter
                Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount(),bundle);


                //Adding adapter to pager
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });




            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


}
