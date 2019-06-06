package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab4 extends Fragment {

    private static String IP_ADDRESS = "IP주소";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    //private TextView mTextViewResult;

    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private EditText mEditTextSearchKeyword;
    private String mJsonString;

    private String EmailHolder;
    private String TeachHolder;
    private String LearnHolder;

    public static final String FriendEmail = "";


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
            TeachHolder = getArguments().getString("TeachHolder");
            LearnHolder = getArguments().getString("LearnHolder");

        }
        Log.d("이메일 받아오기",EmailHolder);
        Log.d("가르칠언어 받아오기",TeachHolder);
        Log.d("배울언어 받아오기",LearnHolder);





        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View rootView = inflater.inflate(R.layout.tab4, container, false);


        //mTextViewResult = (TextView) rootView.findViewById(R.id.textView_main_result);
        mRecyclerView = (RecyclerView)  rootView.findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());



        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(getActivity(), mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));




        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        mArrayList.clear();
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                PersonalData personalData = mArrayList.get(position);
                Toast.makeText(getActivity(), personalData.getMember_email(), Toast.LENGTH_LONG).show();


                Intent intent = new Intent(getActivity(), Friends_Profile_Activity.class);

                intent.putExtra(FriendEmail, personalData.getMember_email());

                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        //데이터 받아오기
        GetData task = new GetData();
        task.execute(TeachHolder,LearnHolder);
    }


    // RecyclerView 클릭 이벤트
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
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
            String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/friends_list.php";
            String postParameters = "teach=" + searchKeyword1 + "&learn=" + searchKeyword2;


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

                PersonalData personalData = new PersonalData();

                personalData.setMember_email(email);
                personalData.setMember_image(image);
                personalData.setMember_name(name);
                personalData.setMember_teach(teach);
                personalData.setMember_learn(learn);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }







}