package com.example.globaltalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment {

    private static String TAG = "phptest";

    private ArrayList<BoardData> bArrayList;
    private BoardAdapter bAdapter;
    private RecyclerView bRecyclerView;

    private String bJsonString;

    private String EmailHolder;
    private String NameHolder;

    public static final String UserEmail = "";
    public static final String UserName = "name";


    ImageView write_button;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
            Log.d("이메일 받아오기",EmailHolder);
            NameHolder = getArguments().getString("NameHolder");
            Log.d("이름 받아오기",NameHolder);
        }

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View rootView = inflater.inflate(R.layout.tab1, container, false);

        bRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView_board_list);
        bRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bArrayList = new ArrayList<>();

        bAdapter = new BoardAdapter(getActivity(), bArrayList);
        bRecyclerView.setAdapter(bAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        bRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));




        write_button = (ImageView) rootView.findViewById(R.id.write_button);



        return rootView;


    }



    @Override
    public void onResume() {
        super.onResume();


        //리사이클러뷰 초기화후, 재정렬
        bArrayList.clear();
        bAdapter.notifyDataSetChanged();


        GetData task = new GetData();
        task.execute( "http://54.180.122.247/global_communication/board_list.php", "");

        // 게시물 쓰기 화면으로 이동
        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getActivity(), Board_writeActivity.class);
                intent1.putExtra(UserEmail, EmailHolder);
                intent1.putExtra(UserName, NameHolder);
                startActivity(intent1);

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

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                bJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            //String serverURL = params[0];
            //String postParameters = params[1];

            //String searchKeyword1 = params[0];
            //String searchKeyword2 = params[1];

            String serverURL = params[0];
            String postParameters = params[1];

            //String serverURL = "http://54.180.122.247/global_communication/board_list.php";
            //String postParameters = "email=" + searchKeyword1 + "&name=" + searchKeyword2;


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

        String TAG_WRITER ="writer";
        String TAG_WDATE ="wdate";
        String TAG_CONTENT = "content";

        String TAG_IMG0 ="img_file0";
        String TAG_IMG1 ="img_file1";
        String TAG_IMG2 ="img_file2";

        String TAG_IMG3 ="img_file3";
        String TAG_IMG4 ="img_file4";
        String TAG_IMG5 ="img_file5";

        String TAG_IMG6 ="img_file6";
        String TAG_IMG7 ="img_file7";
        String TAG_IMG8 ="img_file8";




        try {
            JSONObject jsonObject = new JSONObject(bJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String writer = item.getString(TAG_WRITER);
                Log.d("제이슨",writer);
                String wdate = item.getString(TAG_WDATE);
                Log.d("제이슨",wdate);
                String content = item.getString(TAG_CONTENT);
                Log.d("제이슨",content);

                String img0 = item.getString(TAG_IMG0);
                Log.d("제이슨0",img0);
                String img1 = item.getString(TAG_IMG1);
                Log.d("제이슨1",img1);
                String img2 = item.getString(TAG_IMG2);
                Log.d("제이슨2",img2);


                String img3 = item.getString(TAG_IMG3);
                Log.d("제이슨3",img3);
                String img4 = item.getString(TAG_IMG4);
                Log.d("제이슨4",img4);
                String img5 = item.getString(TAG_IMG5);
                Log.d("제이슨5",img5);

                String img6 = item.getString(TAG_IMG6);
                Log.d("제이슨6",img6);
                String img7 = item.getString(TAG_IMG7);
                Log.d("제이슨7",img7);
                String img8 = item.getString(TAG_IMG8);
                Log.d("제이슨8",img8);




                BoardData boardData = new  BoardData();

                boardData.setwriter(writer);
                boardData.setwdate(wdate);
                boardData.setcontent(content);

                boardData.setimg0(img0);
                boardData.setimg1(img1);
                boardData.setimg2(img2);

                boardData.setimg3(img3);
                boardData.setimg4(img4);
                boardData.setimg5(img5);

                boardData.setimg6(img6);
                boardData.setimg7(img7);
                boardData.setimg8(img8);

                bArrayList.add(boardData);
                bAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }




}