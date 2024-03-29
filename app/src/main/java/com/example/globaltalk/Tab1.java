package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static Tab1 mContext;

    private static String TAG = "phptest";

    private ArrayList<BoardData> bArrayList;
    private BoardAdapter bAdapter;
    private RecyclerView bRecyclerView;

    private SwipeRefreshLayout swipeRefresh;


    private String bJsonString;

    private String EmailHolder;
    private String NameHolder;
    private String TeachHolder;
    private String LearnHolder;
    private String ImageHolder;

    public static final String UserEmail = "";
    public static final String UserName = "name";


    String profile_image;
    String profile_teach;
    String profile_learn;

    String wdate;
    String content;
    String writer;
    String email;


    String img0;
    String img1;
    String img2;
    String img3;
    String img4;
    String img5;
    String img6;
    String img7;
    String img8;

    String board_id;
    String heart_count;
    String heart_people;
    int comment_count;

    int page_no;



    ImageView write_button;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("fragment 생명주기", "onCreateView");
        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
            Log.d("이메일 받아오기",EmailHolder);
            NameHolder = getArguments().getString("NameHolder");
            Log.d("이름 받아오기",NameHolder);
            TeachHolder = getArguments().getString("TeachHolder");
            Log.d("가르칠 언어",TeachHolder );
            LearnHolder = getArguments().getString("LearnHolder");
            Log.d("배울언어 받아오기",LearnHolder);
            ImageHolder = getArguments().getString("ImageHolder");
            Log.d("프로필사진 받아오기",ImageHolder);


        }

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View rootView = inflater.inflate(R.layout.tab1, container, false);


        mContext=this;

        swipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);


        bArrayList = new ArrayList<>();

        bRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView_board_list);
        bRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));

        bAdapter = new BoardAdapter(getActivity(), bArrayList);
        bRecyclerView.setAdapter(bAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.




        write_button = (ImageView) rootView.findViewById(R.id.write_button);


        return rootView;


    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d("fragment 생명주기", "resume");


        //리사이클러뷰 초기화후, 재정렬
        bArrayList.clear();
        bAdapter.notifyDataSetChanged();

        page_no=0;

        GetData task = new GetData();
        task.execute(String.valueOf(page_no));



        bRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView bRecyclerView, int dx, int dy) {
                super.onScrolled(bRecyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) bRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                int itemTotalCount = bRecyclerView.getAdapter().getItemCount() - 1;


                // 스크롤 최하단 감지 시에 반응
                if (lastVisibleItemPosition == itemTotalCount) {
                    //Toast.makeText(getContext(), "Last Position", Toast.LENGTH_SHORT).show();
                    //bAdapter.notifyDataSetChanged();

                    page_no=page_no+6;
                    GetData task = new GetData();
                    task.execute(String.valueOf(page_no));
                    Log.d("페이지 번호", String.valueOf(page_no));

                }
            }
        });


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


    @Override
    public void onRefresh() {
        Log.d("MainActivity_", "onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //리사이클러뷰 초기화후, 재정렬
                bArrayList.clear();
                bAdapter.notifyDataSetChanged();

                page_no=0;

                GetData task = new GetData();
                task.execute(String.valueOf(page_no));

                swipeRefresh.setRefreshing(false);

            }
        }, 2000);
    }

    /*
    //다른 액티비티나 프래그먼트 함수 호출(게시물 작성 반영해주기) -> board_write에서 작성한 것 반영해주기
    public void onCreate() {

        //리사이클러뷰 초기화후, 재정렬
        bArrayList.clear();
        bAdapter.notifyDataSetChanged();

        page_no=0;

        GetData task = new GetData();
        task.execute(String.valueOf(page_no));



        bRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView bRecyclerView, int dx, int dy) {
                super.onScrolled(bRecyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) bRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                int itemTotalCount = bRecyclerView.getAdapter().getItemCount() - 1;


                // 스크롤 최하단 감지 시에 반응
                if (lastVisibleItemPosition == itemTotalCount) {
                    //Toast.makeText(getContext(), "Last Position", Toast.LENGTH_SHORT).show();
                    //bAdapter.notifyDataSetChanged();

                    page_no=page_no+6;
                    GetData task = new GetData();
                    task.execute(String.valueOf(page_no));
                    Log.d("페이지 번호", String.valueOf(page_no));

                }
            }
        });

    }
    */


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(), "데이터 로드 중", null, true, true);
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

            String page_no = params[0];
            //String page_size = params[1];

            //String serverURL = params[0];
            //String postParameters = params[1];

            String serverURL = "http://54.180.122.247/global_communication/board_list.php";
            String postParameters = "page_no=" + page_no;


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

        String TAG_IMAGE ="profile_image";
        String TAG_TEACH ="profile_teach";
        String TAG_LEARN ="profile_learn";



        String TAG_BOARDID ="board_id";

        String TAG_HEARTCOUNT ="heart_count";
        String TAG_HEARTPEOPLE ="heart_people";
        String TAG_COMMENTCOUNT ="comment_count";




            try {
                JSONObject jsonObject = new JSONObject(bJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);


                    board_id = item.getString(TAG_BOARDID);
                    Log.d("게시물 번호", board_id);

                    heart_count = item.getString(TAG_HEARTCOUNT);
                    Log.d("하트 개수", heart_count);

                    heart_people = item.getString(TAG_HEARTPEOPLE);
                    Log.d("하트 누른 사람", heart_people);

                    comment_count = Integer.parseInt(item.getString(TAG_COMMENTCOUNT));
                    Log.d("댓글 개수", String.valueOf(comment_count));



                    profile_image = item.getString(TAG_IMAGE);
                    Log.d("프로필 이미지", profile_image);

                    profile_teach = item.getString(TAG_TEACH);
                    Log.d("프로필 가르칠 언어", profile_teach);

                    profile_learn = item.getString(TAG_LEARN);
                    Log.d("프로필 학습 언어", profile_learn);



                    writer = item.getString(TAG_WRITER);
                    Log.d("제이슨", writer);

                    email = item.getString(TAG_EMAIL);
                    Log.d("제이슨", email);


                    wdate = item.getString(TAG_WDATE);
                    Log.d("제이슨", wdate);
                    content = item.getString(TAG_CONTENT);
                    Log.d("제이슨", content);

                    img0 = item.getString(TAG_IMG0);
                    Log.d("제이슨0", img0);
                    img1 = item.getString(TAG_IMG1);
                    Log.d("제이슨1", img1);
                    img2 = item.getString(TAG_IMG2);
                    Log.d("제이슨2", img2);


                    img3 = item.getString(TAG_IMG3);
                    Log.d("제이슨3", img3);
                    img4 = item.getString(TAG_IMG4);
                    Log.d("제이슨4", img4);
                    img5 = item.getString(TAG_IMG5);
                    Log.d("제이슨5", img5);

                    img6 = item.getString(TAG_IMG6);
                    Log.d("제이슨6", img6);
                    img7 = item.getString(TAG_IMG7);
                    Log.d("제이슨7", img7);
                    img8 = item.getString(TAG_IMG8);
                    Log.d("제이슨8", img8);




                    //가져온 이메일 값을 넣어서 프로필 이미지 가져오기
                    //String email = item.getString(TAG_EMAIL);


                    BoardData boardData = new BoardData();


                    boardData.setlogin_email(EmailHolder);
                    boardData.setlogin_name(NameHolder);
                    boardData.setlogin_teach(TeachHolder);
                    boardData.setlogin_learn(LearnHolder);
                    boardData.setlogin_image(ImageHolder);

                    boardData.setemail(email);


                    boardData.setboard_id(board_id);
                    Log.d("보드번호", board_id);

                    boardData.setheart_count(heart_count);
                    Log.d("하트 개수", heart_count);
                    boardData.setheart_people(heart_people);
                    Log.d("하트 누른 사람", heart_people);
                    boardData.setheart_boolean(0);
                    boardData.setcomment_count(comment_count);



                    boardData.setprofile_image(profile_image);
                    boardData.setprofile_teach(profile_teach);
                    boardData.setprofile_learn(profile_learn);



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

                    //bAdapter.addAll(itemList);

                }


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }



    }




}