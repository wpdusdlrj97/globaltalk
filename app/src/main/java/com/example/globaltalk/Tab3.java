package com.example.globaltalk;

import android.app.ProgressDialog;
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

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab3 extends Fragment {


    private static String TAG = "phptest";
    private String mJsonString;

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


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        return rootView;


    }


    @Override
    public void onResume() {
        super.onResume();

        cArrayList.clear();
        cAdapter.notifyDataSetChanged();


        //내 이름을 포함한 채팅방 리스트 가져오기
        //단 해당 사람이 방을 나갈 경우
        //chat_key 테이블의 exit_person에 표시되며 나간사람은 대화리스트를 받아올수 없다
        GetData task = new GetData();
        task.execute(EmailHolder);


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

                    intent.putExtra("friend_name", chatlistData.getMember_name());

                    startActivity(intent);

                    Log.d("채팅-싱글", String.valueOf(intent));


                } else { //대화가 1:n 채팅이라면 chat19로 이동하라

                    Intent intent9 = new Intent(getActivity(), Chat_Multi.class);

                    intent9.putExtra("room_id", chatlistData.getchatroom_no());

                    intent9.putExtra("user_list", chatlistData.getuserlist());

                    intent9.putExtra("myemail", EmailHolder);

                    startActivity(intent9);
                    Log.d("채팅-멀티", String.valueOf(intent9));
                }

            }
        });


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
                String chatuser_email = item.getString("chatuser_email");
                Log.d("채팅", chatuser_email);
                String chatuser_image = item.getString("chatuser_image");
                Log.d("채팅", chatuser_image);
                String chatuser_name = item.getString("chatuser_name");
                Log.d("채팅", chatuser_name);
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

                    chatlistData.setchatroom_no(chatroom_no);
                    chatlistData.setchatroom_image(chatuser_image);
                    chatlistData.setuserlist(userlist);
                    chatlistData.setMember_name(chatuser_name);
                    chatlistData.setMember_email(chatuser_email);
                    chatlistData.setMyemail(myemail);
                    chatlistData.setExit_person(exit_person);

                    //userlist를 ,로 쪼개 배열에 담는다 -> 여기서 -1 해주기
                    String[] userlist_array = userlist.split(",");

                    chatlistData.setcount(String.valueOf(userlist_array.length-1));
                    chatlistData.setmessage(last_message);
                    chatlistData.setdate(last_time);
                    chatlistData.setchatroom_type(chatroom_type);


                    cArrayList.add(chatlistData);
                    cAdapter.notifyDataSetChanged();
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
