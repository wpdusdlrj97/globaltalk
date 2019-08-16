package com.example.globaltalk;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment



public class Tab3 extends Fragment {


    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

    private static String TAG = "phptest";
    private String mJsonString;
    private String mJsonString9;

    private String EmailHolder;
    private String NameHolder;
    private String TeachHolder;
    private String LearnHolder;
    private String ImageHolder;

    private ArrayList<ChatListData> cArrayList;
    private ChatListAdapter cAdapter;
    private RecyclerView cRecyclerView;


    Socket socket;
    //LinkedList<SocketClient> threadList;
    Context context;
    String name;
    String place;
    WordsThread mythread;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //실시간 비교 쓰레드 가동
            compareThread();
        }
    };



    class WordsThread extends Thread {

        private boolean isRunning;

        public void run() {

            while(isRunning) {

                //cArrayList.clear();
                compareThread();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
            }



        }

        public void setRunningState(boolean state) {
            isRunning = state;
        }


    }






    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("생명주기", "onCreate");

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
            Log.d("이메일 받아오기", EmailHolder);
            NameHolder = getArguments().getString("NameHolder");
            Log.d("이름 받아오기", NameHolder);
            TeachHolder = getArguments().getString("TeachHolder");
            Log.d("가르칠 언어", TeachHolder);
            LearnHolder = getArguments().getString("LearnHolder");
            Log.d("배울언어 받아오기", LearnHolder);
            ImageHolder = getArguments().getString("ImageHolder");
            Log.d("프로필사진 받아오기", ImageHolder);


        }

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View rootView = inflater.inflate(R.layout.tab3, container, false);

        context = getActivity();

        cRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView_chat_list);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        cArrayList = new ArrayList<>();

        cAdapter = new ChatListAdapter(getActivity(), cArrayList);


        cRecyclerView.setAdapter(cAdapter);






        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        //cRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));





        //Log.d("쓰레드 실행 onCreate", String.valueOf(mythread));



        return rootView;


    }




    @Override
    public void onStart() {
        super.onStart();
        Log.d("생명주기", "onStart");

        //mythread = new WordsThread();

        //mythread.setRunningState(true);

        //mythread.setDaemon(true);

        //mythread.start();

    }

    /*
    // DB의 채팅방 개수와 현재 TAB3에 반영된 채팅방 개수를 실시간으로 3초마다 비교한다다
    Thread myThread = new Thread(new Runnable() {
        public void run() {
            while (true) {
                try {
                    handler.sendMessage(handler.obtainMessage());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            }
        }
    });
    */









    @Override
    public void onResume() {
        super.onResume();
        Log.d("생명주기", "onResume");

        //cArrayList.clear();
        //cAdapter.notifyDataSetChanged();


        //내 이름을 포함한 채팅방 리스트 가져오기
        //단 해당 사람이 방을 나갈 경우
        //chat_key 테이블의 exit_person에 표시되며 나간사람은 대화리스트를 받아올수 없다
        //GetData task = new GetData();
        //task.execute(EmailHolder);

        mythread = new WordsThread();

        mythread.setRunningState(true);

        mythread.setDaemon(true);

        mythread.start();

        //Log.d("쓰레드 실행 onResume", String.valueOf(mythread));





        // 아이템을 클릭했을 때
        cAdapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                ChatListData chatlistData = cArrayList.get(position);

                Toast.makeText(getActivity(), chatlistData.getchatroom_no(), Toast.LENGTH_LONG).show();

                //getActivity().finish();

                if (chatlistData.getchatroom_type().equals("single_chat")) { //만약 대화가 1대1채팅이라면 chat9으로 이동하라

                    Intent intent = new Intent(getActivity(), Chat9.class);

                    intent.putExtra("room_id", chatlistData.getchatroom_no());

                    intent.putExtra("user_list", chatlistData.getuserlist());

                    intent.putExtra("myemail", EmailHolder);

                    intent.putExtra("friend_name", chatlistData.getroom_name());

                    //내가 가르칠 언어
                    intent.putExtra("TeachHolder", TeachHolder);
                    //내가 배울 언어
                    intent.putExtra("LearnHolder", LearnHolder);


                    startActivity(intent);

                    Log.d("채팅-싱글", String.valueOf(intent));


                } else { //대화가 1:n 채팅이라면 chat19로 이동하라

                    Intent intent9 = new Intent(getActivity(), Chat_Multi.class);

                    intent9.putExtra("room_id", chatlistData.getchatroom_no());

                    intent9.putExtra("user_list", chatlistData.getuserlist());

                    intent9.putExtra("myemail", EmailHolder);

                    //내가 가르칠 언어
                    intent9.putExtra("TeachHolder", TeachHolder);
                    //내가 배울 언어
                    intent9.putExtra("LearnHolder", LearnHolder);

                    startActivity(intent9);
                    Log.d("채팅-멀티", String.valueOf(intent9));
                }

            }
        });


    }



    @Override
    public void onPause() {
        super.onPause();
        Log.d("생명주기", "onPause");

        mythread.setRunningState(false);

        //Log.d("쓰레드 실행 onPause", String.valueOf(mythread));

        //mythread.start();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("생명주기", "onStop");

        boolean retry = true;

        mythread.setRunningState(false);

        while(retry){

            mythread.interrupt();
            retry=false;
        }


        //mythread.setRunningState(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("생명주기", "onDestroy");

        //mythread.setRunningState(false);

        //해당 프래그먼트가 완전히 종료될 때 죽이기
        //mythread.interrupt();
        //Log.d("쓰레드 실행 onDestroy", String.valueOf(mythread));

    }





    //DB 채팅방 개수 감지 쓰레드
    private void compareThread() {


        cArrayList.clear();
        //cAdapter.notifyDataSetChanged();

        GetData task = new GetData();
        task.execute(EmailHolder);

        /////////////////////////////////////////////////////////////////////

        // -> 방을 새로 만들 시



        //cArrayList.size();
        //Log.d("현재 채팅방 리스트 개수", String.valueOf(cArrayList.size()));
        //나의 이메일을 보내서 현재 나의 채팅방 개수의 변화를 감지한다

        //Compare_Function(EmailHolder);




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


            if (result == null) {

                // mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://54.180.122.247/global_communication/chatroom_list.php";
            String postParameters = "myemail=" + searchKeyword1;


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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult() {

        String TAG_JSON = "webnautes";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String chatroom_no = item.getString("chatroom_no");
                Log.d("채팅", chatroom_no);
                String userlist = item.getString("user_list");
                Log.d("채팅", userlist);


                String chatuser_email1 = item.getString("chatuser_email1");
                Log.d("채팅", chatuser_email1);
                String chatuser_image1 = item.getString("chatuser_image1");
                Log.d("채팅", chatuser_image1);
                String chatuser_name1 = item.getString("chatuser_name1");
                Log.d("채팅", chatuser_name1);

                String chatuser_email2 = item.getString("chatuser_email2");
                Log.d("채팅", chatuser_email2);
                String chatuser_image2 = item.getString("chatuser_image2");
                Log.d("채팅", chatuser_image2);
                String chatuser_name2 = item.getString("chatuser_name2");
                Log.d("채팅", chatuser_name2);

                String chatuser_email3 = item.getString("chatuser_email3");
                Log.d("채팅", chatuser_email3);
                String chatuser_image3 = item.getString("chatuser_image3");
                Log.d("채팅", chatuser_image3);
                String chatuser_name3 = item.getString("chatuser_name3");
                Log.d("채팅", chatuser_name3);


                String chatuser_email4 = item.getString("chatuser_email4");
                Log.d("채팅", chatuser_email4);
                String chatuser_image4 = item.getString("chatuser_image4");
                Log.d("채팅", chatuser_image4);
                String chatuser_name4 = item.getString("chatuser_name4");
                Log.d("채팅", chatuser_name4);



                String myemail = item.getString("myemail");
                Log.d("채팅", myemail);
                String exit_person = item.getString("exit_person");
                Log.d("채팅", exit_person);
                String last_message = item.getString("last_message");
                Log.d("채팅", last_message);
                String last_time = item.getString("last_time");
                Log.d("채팅", last_time);
                String chatroom_type = item.getString("chatroom_type");
                Log.d("채팅", chatroom_type);


                if (exit_person.contains(myemail)) { //퇴장한 사람 중에 내 이름이 들어있으면 put X

                } else {
                    ChatListData chatlistData = new ChatListData();

                    //userlist를 ,로 쪼개 배열에 담는다 -> 여기서 -1 해주기
                    String[] userlist_array = userlist.split(",");

                    if(userlist_array.length-1==2) { //1대1 대화일경우

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setroom_name(chatuser_name1);
                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);

                    }else if(userlist_array.length-1==3){ // 3명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);

                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);



                    }else if(userlist_array.length-1==4){ // 4명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);
                        chatlistData.setchatroom_image3(chatuser_image3);


                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2+","+chatuser_name3);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);


                    }else{ //5명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);
                        chatlistData.setchatroom_image3(chatuser_image3);
                        chatlistData.setchatroom_image4(chatuser_image4);


                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2+","+chatuser_name3+","+chatuser_name4);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);

                    }


                    cArrayList.add(chatlistData);
                    cAdapter.notifyDataSetChanged();

                    //cAdapter.notifyItemInserted(cArrayList.size());
                }


                //방의 프로필사진, 대화자 목록 데이터 받아오기
                //GetData9 task9 = new GetData9();
                //task9.execute(Chatlist[1], chatroom_no);

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }






    // 메시지 보내기 DB값에 빈방 추가
    // 빈방을 추가하기 전에 이미 있는 방인지 확인한다
    public void Compare_Function(final String login_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // DB에 반영된 개수
                //Toast.makeText(getActivity(), "DB의 채팅방 개수 : "+chatResponseMsg+" 현재 채팅방 리스트 : "+String.valueOf(cArrayList.size()), Toast.LENGTH_SHORT).show();
                Log.d("DB의 채팅방 개수 :",chatResponseMsg);
                Log.d("현재 채팅방 리스트 :",String.valueOf(cArrayList.size()));


                if(chatResponseMsg.equals(String.valueOf(cArrayList.size()))){ //현재 탭에 반영된 채팅방 개수와 DB의 개수가 같을 경우

                    Log.d("DB의 채팅방 개수와 반영된 채팅 개수 :","같다");

                    //바꿔줄 필요 X

                }else{ // 개수가 다르다면 가장 최근 채팅방을 Insert 해준다

                    Log.d("DB의 채팅방 개수와 반영된 채팅 개수 :","다르다");

                    //해당 이메일의 방 중에서 가장 최근 것을 추가해준다
                    GetData9 task9 = new GetData9();
                    task9.execute(EmailHolder);

                }



            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("login_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/compare_chatroom.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(login_id);
    }





    private class GetData9 extends AsyncTask<String, Void, String> {


        String errorString9 = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result9) {
            super.onPostExecute(result9);


            if (result9 == null) {

                // mTextViewResult.setText(errorString);
            } else {

                mJsonString9 = result9;
                showResult9();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://54.180.122.247/global_communication/chatroom_list_refresh.php";
            String postParameters = "myemail=" + searchKeyword1;


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

                Log.d(TAG, "GetData : Error ", e);
                errorString9 = e.toString();

                return null;
            }

        }
    }


    private void showResult9() {

        String TAG_JSON = "webnautes";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString9);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String chatroom_no = item.getString("chatroom_no");
                Log.d("채팅 리스트 갱신", chatroom_no);
                String userlist = item.getString("user_list");
                Log.d("채팅 리스트 갱신", userlist);


                String chatuser_email1 = item.getString("chatuser_email1");
                Log.d("채팅 리스트 갱신", chatuser_email1);
                String chatuser_image1 = item.getString("chatuser_image1");
                Log.d("채팅 리스트 갱신", chatuser_image1);
                String chatuser_name1 = item.getString("chatuser_name1");
                Log.d("채팅 리스트 갱신", chatuser_name1);

                String chatuser_email2 = item.getString("chatuser_email2");
                Log.d("채팅 리스트 갱신", chatuser_email2);
                String chatuser_image2 = item.getString("chatuser_image2");
                Log.d("채팅 리스트 갱신", chatuser_image2);
                String chatuser_name2 = item.getString("chatuser_name2");
                Log.d("채팅 리스트 갱신", chatuser_name2);

                String chatuser_email3 = item.getString("chatuser_email3");
                Log.d("채팅 리스트 갱신", chatuser_email3);
                String chatuser_image3 = item.getString("chatuser_image3");
                Log.d("채팅 리스트 갱신", chatuser_image3);
                String chatuser_name3 = item.getString("chatuser_name3");
                Log.d("채팅 리스트 갱신", chatuser_name3);


                String chatuser_email4 = item.getString("chatuser_email4");
                Log.d("채팅 리스트 갱신", chatuser_email4);
                String chatuser_image4 = item.getString("chatuser_image4");
                Log.d("채팅 리스트 갱신", chatuser_image4);
                String chatuser_name4 = item.getString("chatuser_name4");
                Log.d("채팅 리스트 갱신", chatuser_name4);



                String myemail = item.getString("myemail");
                Log.d("채팅 리스트 갱신", myemail);
                String exit_person = item.getString("exit_person");
                Log.d("채팅 리스트 갱신", exit_person);
                String last_message = item.getString("last_message");
                Log.d("채팅 리스트 갱신", last_message);
                String last_time = item.getString("last_time");
                Log.d("채팅 리스트 갱신", last_time);
                String chatroom_type = item.getString("chatroom_type");
                Log.d("채팅 리스트 갱신", chatroom_type);


                if (exit_person.contains(myemail)) { //퇴장한 사람 중에 내 이름이 들어있으면 put X

                } else {
                    ChatListData chatlistData = new ChatListData();

                    //userlist를 ,로 쪼개 배열에 담는다 -> 여기서 -1 해주기
                    String[] userlist_array = userlist.split(",");

                    if(userlist_array.length-1==2) { //1대1 대화일경우

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setroom_name(chatuser_name1);
                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);

                    }else if(userlist_array.length-1==3){ // 3명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);

                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);



                    }else if(userlist_array.length-1==4){ // 4명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);
                        chatlistData.setchatroom_image3(chatuser_image3);


                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2+","+chatuser_name3);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);


                    }else{ //5명

                        chatlistData.setchatroom_no(chatroom_no);
                        chatlistData.setuserlist(userlist);

                        chatlistData.setchatroom_image(chatuser_image1);
                        chatlistData.setchatroom_image2(chatuser_image2);
                        chatlistData.setchatroom_image3(chatuser_image3);
                        chatlistData.setchatroom_image4(chatuser_image4);


                        chatlistData.setroom_name(chatuser_name1+","+chatuser_name2+","+chatuser_name3+","+chatuser_name4);

                        chatlistData.setMember_email(chatuser_email1);

                        chatlistData.setMyemail(myemail);
                        chatlistData.setExit_person(exit_person);


                        chatlistData.setcount(userlist_array.length-1);
                        chatlistData.setmessage(last_message);
                        chatlistData.setdate(last_time);
                        chatlistData.setchatroom_type(chatroom_type);

                    }


                    //해당 뷰에 추가해준다
                    cArrayList.add(0,chatlistData);
                    //cAdapter.notifyDataSetChanged();
                    cAdapter.notifyItemInserted(0);
                }


                //방의 프로필사진, 대화자 목록 데이터 받아오기
                //GetData9 task9 = new GetData9();
                //task9.execute(Chatlist[1], chatroom_no);

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


}
