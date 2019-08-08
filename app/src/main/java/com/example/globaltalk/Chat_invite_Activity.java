package com.example.globaltalk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;

public class Chat_invite_Activity extends AppCompatActivity {

    Chat9 Chat9Activity = (Chat9)Chat9.Chat9Activity;



    private static String TAG = "phptest";

    String EmailHolder;
    String ChatListHolder;

    private String mJsonString;

    Button chat_invite_finish;

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

    List list_invite = new ArrayList();

    private ArrayList<InviteData> ivArrayList;
    private InviteAdapter ivAdapter;
    private RecyclerView ivRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_invite_);

        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra("Email");
        Log.d("이메일 받기", EmailHolder);

        ChatListHolder = intent.getStringExtra("ChatListHolder");
        Log.d("ChatListHolder 받기", ChatListHolder);

        //초대하기 완료 버튼
        chat_invite_finish=findViewById(R.id.chat_invite_finish);



        //mTextViewResult = (TextView) rootView.findViewById(R.id.textView_main_result);
        ivRecyclerView = (RecyclerView) findViewById(R.id.listView_invite_list);
        ivRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        ivArrayList = new ArrayList<>();

        ivAdapter = new InviteAdapter(this, ivArrayList);
        ivRecyclerView.setAdapter(ivAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        //ivRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));



    }


    @Override
    public void onResume() {
        super.onResume();

        ivArrayList.clear();
        ivAdapter.notifyDataSetChanged();

        //데이터 받아오기
        GetData task = new GetData();
        task.execute(EmailHolder);


        //인터페이스를 통한 클릭 리스너
        //해당 포지션을 가져와서 토스트로 이메일을 띄워준다
        //여기서 이럴게 아니라 해당 포지션의 checkbox가 체크되었는지 아닌지 여기서 판단한후
        //체크되었다면 추가, 해제되었다면 삭제를 시켜야한다
        ivAdapter.setOnItemClickListener(new InviteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(Chat_invite_Activity.this, ivArrayList.get(position).getMember_email(), Toast.LENGTH_LONG).show();
                //listA.add(ivArrayList.get(position).getMember_email());
                //Log.d("리스트A",String.valueOf(listA));

                if(ivArrayList.get(position).getinvite_checkbox()==0){ //체크가 안되어있을 경우

                    // 체크됨으로 바꿔준(1로 변경) 이후
                    ivArrayList.get(position).setinvite_checkbox(1);
                    //리스트에 담아준다
                    list_invite.add(ivArrayList.get(position).getMember_email());
                    Log.d("리스트 list_invite",String.valueOf(list_invite));

                }else{ //체크가 되어있을 경우

                    // 체크안됨으로 바꿔준(1로 변경) 이후
                    ivArrayList.get(position).setinvite_checkbox(0);
                    //리스트에서 제외한다
                    list_invite.remove(ivArrayList.get(position).getMember_email());
                    Log.d("리스트 list_invite",String.valueOf(list_invite));

                }

            }
        });



        //초대할 사람 클릭 후 "초대완료"버튼을 누를 시 --> 1명도 체크를 안했을 경우 (Chat9으로, 1명이라도 추가했을 시 Chat_multi로 이동)
        chat_invite_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list_invite.size()==0){//초대를 아무도 안했을 시

                    //그냥 종료하면 된다
                    Toast.makeText(Chat_invite_Activity.this, "아무도 초대하지 않았습니다", Toast.LENGTH_LONG).show();
                    finish();

                }else{// 누군가 클릭했을 시

                    //초대 리스트와 채팅리스트를 합쳐서 새로운 방을 생성 chat_multi로 이동하고 Chat9 대화방은 finish 시킨다

                    //방만 만들고 아무말도 안할 수 있으니
                    //방번호만 받아오고 채팅을 칠 때 모든 방이 만들어지게 한다
                    Multi_ChatKey_Function();

                    //Chat9 액티비티는 종료시키기
                    Chat9Activity.finish();
                    finish();


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

            String serverURL = "http://54.180.122.247/global_communication/invite_list.php";
            String postParameters = "email=" + searchKeyword1 ;


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




        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String email = item.getString(TAG_EMAIL);
                String image = item.getString(TAG_IMAGE);
                String name = item.getString(TAG_NAME);


                InviteData inviteData = new InviteData();


                inviteData.setMember_email(email);
                inviteData.setMember_image(image);
                inviteData.setMember_name(name);
                inviteData.setInviteList(ChatListHolder);
                inviteData.setinvite_checkbox(0);




                ivArrayList.add(inviteData);
                ivAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }




    // 메시지 보내기 DB값에 빈방 추가
    // 빈방을 추가하기 전에 이미 있는 방인지 확인한다
    public void Multi_ChatKey_Function() {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_invite_Activity.this, chatResponseMsg, Toast.LENGTH_LONG).show();

                //인텐트로 던지기
                Intent intent = new Intent(Chat_invite_Activity.this, Chat_Multi.class);

                intent.putExtra("room_id", chatResponseMsg );
                intent.putExtra("myemail", EmailHolder );


                //intent.putExtra("friend_email", FriendEmailHolder );

                //초대된 배열을 쪼갠뒤에 chat_list에 붙인다 ,a,b,c 형식으로

                //                    String chatuserlist = String.valueOf(Userlist);
                //                    chatuserlist = chatuserlist.replace("[", ",");
                //                    chatuserlist = chatuserlist.replace("]", "");
                //                    chatuserlist = chatuserlist.replace(" ", "");
                //                    Log.d("채팅유저리스트", chatuserlist);


                String chatuserlist = String.valueOf(list_invite);
                chatuserlist = chatuserlist.replace("[", ",");
                chatuserlist = chatuserlist.replace("]", "");
                chatuserlist = chatuserlist.replace(" ", "");

                String Total_Chat_userlist = ChatListHolder + chatuserlist;

                Log.d("토탈 유저리스트", Total_Chat_userlist);

                intent.putExtra("user_list", Total_Chat_userlist);

                String Invite_Chat_userlist = Total_Chat_userlist.replace(","+EmailHolder,"");

                intent.putExtra("invite_message", EmailHolder+"님이 "+ Invite_Chat_userlist+"을 초대하였습니다");

                startActivity(intent);



            }

            @Override
            protected String doInBackground(String... params) {

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/multi_chat_key.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute();
    }



}
