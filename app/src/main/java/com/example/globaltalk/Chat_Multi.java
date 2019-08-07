package com.example.globaltalk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Chat_Multi extends AppCompatActivity {

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

    private DrawerLayout m_drawerLayout;
    private View m_drawerView;


    private static String TAG = "phptest";
    private String mJsonString;
    private String mJsonString9;
    private String mJsonString19;

    TextView m_friend_name;

    //TextView m_txtMessage;
    Button m_btnSend;
    EditText m_editMessage;

    ImageView m_chat_write_more;

    Handler msgHandler;

    //쓰레드 3개 만들 것
    SocketClient client;
    ReceiveThread receive;
    SendThread send;


    String IP = "192.168.0.7";
    String PORT = "9999";

    Socket socket;
    //LinkedList<SocketClient> threadList;
    Context context;

    String RoomHolder;
    String MyEmailHolder;
    String FriendEmailHolder;
    String UserListHolder;


    String message;
    String formatDate;


    String Servermessage;
    int idx;
    String chatuser_email;
    String chat_content;


    private ArrayList<InChatData> icArrayList;
    private InChatAdapter icAdapter;
    private RecyclerView icRecyclerView;

    private ArrayList<M_InChatUserData> m_icuser_ArrayList;
    private M_InChatUserAdapter m_icuser_Adapter;
    private RecyclerView m_icuser_RecyclerView;


    ArrayList<String> Userlist = new ArrayList<String>();

    InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_multi);

        Log.d("채팅 멀티", "oncreate");

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();

        //나의 이메일
        MyEmailHolder = intent.getStringExtra("myemail");
        Log.d("나의 이메일 받아오기", MyEmailHolder);

        //친구의 이메일
        //FriendEmailHolder = intent.getStringExtra("friend_email");
        //Log.d("친구의 이메일 받아오기",FriendEmailHolder);


        //채팅 유저리스트
        UserListHolder = intent.getStringExtra("user_list");
        Log.d("유저리스트 받아오기", UserListHolder);

        RoomHolder = intent.getStringExtra("room_id");
        Log.d("방번호 받아오기", RoomHolder);


        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);


        context = this;

        m_editMessage = findViewById(R.id.m_editMessage);

        m_btnSend = findViewById(R.id.m_btnSend);

        m_chat_write_more = findViewById(R.id.m_chat_write_more);

        //m_txtMessage = findViewById(R.id.m_txtMessage);
        m_friend_name = findViewById(R.id.m_friend_name);


        client = new SocketClient(IP, PORT);
        //client 스타트시 SocketClient에서 run 메서드를 돌린다다
        client.start();


        //사용자 추가
        String[] splitStr = UserListHolder.split(",");
        for (int i = 1; i < splitStr.length; i++) {
            Userlist.add(splitStr[i]);
            Log.d("유저리스트 반복", String.valueOf(Userlist));
        }


        //대화 상대 이름
        m_friend_name.setText("그룹채팅 "+String.valueOf(Userlist.size()));


        icRecyclerView = (RecyclerView) findViewById(R.id.chat_bubble_list);
        icRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        icArrayList = new ArrayList<>();

        icAdapter = new InChatAdapter(this, icArrayList);
        icRecyclerView.setAdapter(icAdapter);




        // 드로우뷰
        m_icuser_RecyclerView = (RecyclerView) findViewById(R.id.m_inchatroom_user_list);
        m_icuser_RecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        m_icuser_ArrayList = new ArrayList<>();

        m_icuser_Adapter = new M_InChatUserAdapter(this, m_icuser_ArrayList);
        m_icuser_RecyclerView.setAdapter(m_icuser_Adapter);
        m_icuser_RecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));


        // 더보기란
        m_drawerLayout = (DrawerLayout) findViewById(R.id.m_drawer_layout);
        m_drawerView = (View) findViewById(R.id.m_drawer);


    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d("채팅 멀티", "onresume");

        //채팅한 대화내역 가져오기
        icArrayList.clear();
        icAdapter.notifyDataSetChanged();

        GetData task = new GetData();
        task.execute(RoomHolder, MyEmailHolder);


        //리사이클러뷰 가장 하단으로 이동
        //icRecyclerView.scrollToPosition(icArrayList.size()-1);


        // 더보기 클릭시 드로우뷰 튀어나오기
        m_chat_write_more.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                m_drawerLayout.openDrawer(m_drawerView);

                // 해당 채팅방 번호로 chat_key의 user_list 불러오기, 하나하나 데이터 추가해주기기
                m_icuser_ArrayList.clear();
                m_icuser_Adapter.notifyDataSetChanged();

                GetDrawer taskdrawer = new GetDrawer();
                taskdrawer.execute(RoomHolder);
                Log.d("드로워 실행", String.valueOf(taskdrawer));
            }

        });

        // 드로우뷰 들여보내기
        ImageView buttonCloseDrawer = (ImageView) findViewById(R.id.m_closedrawer);
        buttonCloseDrawer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                m_drawerLayout.closeDrawers();
            }
        });

        ImageView m_chat_invite = (ImageView) findViewById(R.id.m_chat_invite);


        //채팅방 초대하기
        //M_Chat_invite_Activity 재 생성
        m_chat_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //나의 이메일만 가지고 팔로우리스트 목록 가져오기
                Intent intent19 = new Intent(Chat_Multi.this, M_Chat_invite_Activity.class);


                //이메일을 던지고 그에 해당하는 것들을 조건문으로 걸러낸 뒤에 recyclerview에 뿌려주기
                intent19.putExtra("Email", MyEmailHolder);
                intent19.putExtra("Room_no", RoomHolder);

                intent19.putExtra("ChatListHolder",UserListHolder);

                startActivity(intent19);

                finish();


            }
        });


        ImageView m_chat_exit = (ImageView) findViewById(R.id.m_chat_exit);
        //채팅방 나가기
        m_chat_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //멀티 채팅의 경우
                //채팅방 목록 chat_key에 접근해서 해당방의 유저리스트에서 제외

                //채팅방에 남아있는 사람도 해당 사람이 나간 것을 알아야한다

                //채팅방을 나갈시 현재 채팅방이 사라지고 채팅방 목록에서 현재 채팅방이 사라진다
                //또한 chat_remember에 방번호, 나간유저 이메일, 채팅order(나중에 다시 들어왔을 때 필요)를 0으로 준다

                // 1. 유저리스트에서 본인 이름 제외하기
                M_ChatRoom_exit_Function(RoomHolder, MyEmailHolder);


                //해당유저가 나갔다고 공지해주기


                //해당 유저가 다시 들어올 시 나간 이후의 채팅은 못본다
                //-----------OO님이 나갔습니다------------ 는 볼 수 없고
                //-----------OO님이 초대되었습니다-------- 부터 볼 수 있다
                //불러올 때 사이즈 계산
                //PHP 딴에서 remember_order=remember_order+'$remember_order'가 아니라 사이즈가 다를 수도
                M_ChatRoom_Exit_Remember_Function(RoomHolder, MyEmailHolder, String.valueOf(icArrayList.size()));

                //Log.d("어레이 리스트 사이즈", String.valueOf(icArrayList.size()));

                finish();


            }
        });


        //핸들러는 액티비티가 만들어질 때 여기서 동작한다
        //안드로이드에서 네트워크 작업을 할 시 백그라운드로 돌려야한다
        //백그라운드에서는 메인UI를 건드릴 수 없으므로 runOnUi를 쓰든지 핸들러를 써야한다
        //핸들러는 화면을 고치는 일을 전담한다
        msgHandler = new Handler() {
            //메시지 수신시 백그라운드 스레드에서 받은 메시지를 처리
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1111) { //1111은 식별자로 핸들러는 하나지만 핸들러를 호출하는 곳은 여러군데일 수 있다
                    //채팅 서버로부터 수신한 메시지를 텍스트뷰에 추가


                    //서버로부터 받은 메시지형태 -> heungmin@naver.com : aaaaa
                    //따라서 이메일과 aaaa를 구분한뒤 adapter에 넣어줘야 한다
                    Servermessage = msg.obj.toString();

                    idx = Servermessage.indexOf(":");

                    chatuser_email = Servermessage.substring(0, idx);

                    // 뒷부분을 추출
                    // 아래 substring은 @ 바로 뒷부분인 n부터 추출된다.
                    chat_content = Servermessage.substring(idx + 1);


                    //db에서 가져오는 것이 아닌
                    //바로바로 TCP 통신을 할 때
                    //상대방의 사진과 이름을 가져와야한다


                    //가져온 이메일과 나의 이메일이 다를 경우에만 반영
                    //내가 보낸 건 이미 send 버튼 누를 때 어레이리스트에 반영했기 때문에
                    //상대방이 보낸 것만 불러오면 된다
                    if (chatuser_email.equals(MyEmailHolder)) {
                        Log.d("이메일 같을 때", "실행");
                    } else {
                        Log.d("이메일 다를 때", "실행");
                        GetData9 task9 = new GetData9();
                        task9.execute(chatuser_email);
                        Log.d("2번", String.valueOf(task9));
                    }


                }
            }
        };


        //서버에 메시지를 전송하는 버튼
        m_btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /////////자바 서버로 보내기/////////
                //사용자가 입력한 메시지
                message = m_editMessage.getText().toString();


                if (!message.equals("")) {

                    //소켓에 보내기 전에 해당 클라이언트의 어레이리스트에 선 추가
                    //txtMessage.append(msg.obj.toString()+"\n");
                    InChatData inchatData = new InChatData();

                    //어차피 내가 보낸거니까 chat_email이랑 my_email을 같게해서 보낸다
                    inchatData.setchat_email(MyEmailHolder);
                    inchatData.setchat_content(message);
                    inchatData.setmy_email(MyEmailHolder);
                    inchatData.setchat_wdate(formatDate);

                    icArrayList.add(inchatData);
                    Log.d("클라이언트 선 전달1", String.valueOf(inchatData));
                    // 밑의 두 방식 모두 가능하지만 첫번쨰 notifyDatasetchange는 깜빡거리고 insert는 그떄 뷰만 추가
                    //icAdapter.notifyDataSetChanged();
                    icAdapter.notifyItemInserted(icArrayList.size());
                    icRecyclerView.smoothScrollToPosition(icAdapter.getItemCount() - 1);


                    String chatuserlist = String.valueOf(Userlist);
                    chatuserlist = chatuserlist.replace("[", ",");
                    chatuserlist = chatuserlist.replace("]", "");
                    chatuserlist = chatuserlist.replace(" ", "");
                    Log.d("채팅유저리스트", chatuserlist);

                    /////////DB 서버로 보내기/////////
                    ChatRoom_Function(RoomHolder, MyEmailHolder, message, formatDate);

                    //채팅방(chat_key)에 유저리스트 추가
                    //유저리스트만 계속 업데이트 시킨다
                    Chat_UserList_Function(RoomHolder, chatuserlist, message, "multi_chat");


                    //이거는 초대했을 때 userlist에 추가하고 넣어주면 된다
                    //멀티 채팅 퇴장한 사람의 경우 user_list에 이름 다시 넣어주기
                    M_ChatRoom_Exit_Person_Function(RoomHolder,MyEmailHolder);


                    m_editMessage.setText("");

                    //서버로는 가장 나중에 보내준다
                    //send 쓰레드를 전송용 쓰레드를 만들어서 호출
                    send = new SendThread(message, socket);
                    send.start();

                }

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("채팅리스트 사이즈", String.valueOf(icArrayList.size()));

        if (icArrayList.size() == 0) { //채팅 친 내역이 없다는 얘기므로 삭제시키기

            ChatRoom_destroy_Function(RoomHolder);

        }

    }


    //내부 클래스
    class SocketClient extends Thread {
        boolean threadAlive; //쓰레드의 동작여부 (필요한 이유-> 앱은 종료되는데 쓰레드가 죽지않는 경우 때문에)
        String ip;
        String port;
        OutputStream outputStream = null;
        DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            try {
                //채팅서버에 접속 (IP와 포트번호를 전달 시 accept 대기상태인 서버에서 소켓을 만들어준다)
                socket = new Socket(ip, Integer.parseInt(port));
                //서버에 메시지를 전달하기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
                //서버에서 받은 메시지를 수신하는 스레드 생성
                receive = new ReceiveThread(socket);
                receive.start();


                //방번호 -> DB의 고유한 키값
                output.writeUTF(RoomHolder);
                Log.d("Room", RoomHolder);


                //식별자 -> 나의 이메일
                output.writeUTF(MyEmailHolder);
                Log.d("MyEmail", MyEmailHolder);


                //output.writeUTF(mac9);
                //Log.d("mac9",mac9);
                //output.writeUTF(mac19);
                //Log.d("mac19",mac19);
                //그러면 eclipse의 서버에서 아이피 주소와 Mac Address가 출력이 된다

                //서버 코드
                //InetAddress ip=socket.getInetAddress();
                //System.out.println(ip+" connected");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }// 소켓 클라이언트 쓰레드의 끝


    //내부 클래스 (메시지 수신용 쓰레드) 메시지를 받는 역할을 한다
    class ReceiveThread extends Thread {
        Socket socket = null;
        DataInputStream input = null;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                //채팅서버로부터 메시지를 받기 위한 스트림 생성
                input = new DataInputStream(socket.getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (input != null) {
                    //채팅 서버로부터 받은 메시지
                    String msg = input.readUTF();
                    if (msg != null) {
                        //핸들러에게 전달할 메시지 객체
                        Message hdmsg = msgHandler.obtainMessage();
                        hdmsg.what = 1111; //메시지의 식별자
                        hdmsg.obj = msg; //메시지의 본문
                        //핸들러에게 메시지 전달(화면 변경 요청)
                        msgHandler.sendMessage(hdmsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }// 수신용 쓰레드 클래스의 끝


    //내부 클래스
    class SendThread extends Thread {
        Socket socket;
        String sendmsg;
        DataOutputStream output;

        public SendThread(String sendmsg, Socket socket) {
            this.sendmsg = sendmsg;
            this.socket = socket;
            try {
                //채팅서버로 메시지를 보내기 위한  스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                if (output != null) {
                    if (sendmsg != null) {
                        output.writeUTF(MyEmailHolder + ":" + sendmsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    // 메시지 보내기 DB값에 빈방 추가
    public void ChatRoom_Function(final String chatroom_id, final String writer, final String content, final String date) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);

                hashMap.put("writer", params[1]);

                hashMap.put("content", params[2]);

                hashMap.put("date", params[3]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/chatroom.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_id, writer, content, date);
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
            String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/getchat_content.php";
            String postParameters = "chatroom_no=" + searchKeyword1 + "&email=" + searchKeyword2;


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

                String writer = item.getString("writer");
                Log.d("채팅 쓴 사람", writer);
                String content = item.getString("content");
                Log.d("채팅 내용", content);
                String wdate = item.getString("wdate");
                Log.d("채팅 날짜", wdate);
                String chatuser_image = item.getString("chatuser_image");
                Log.d("채팅 프로필 사진", chatuser_image);
                String chatuser_name = item.getString("chatuser_name");
                Log.d("채팅 프로필 이름", chatuser_name);


                InChatData inchatData = new InChatData();

                inchatData.setchat_email(writer);
                inchatData.setchat_content(content);
                inchatData.setmy_email(MyEmailHolder);
                Log.d("이메일", MyEmailHolder);
                inchatData.setchat_profile_image(chatuser_image);
                inchatData.setchat_profile_name(chatuser_name);

                wdate = wdate.substring(11, 16);


                inchatData.setchat_wdate(wdate);


                icArrayList.add(inchatData);


                // 밑의 두 방식 모두 가능하지만 첫번쨰 notifyDatasetchange는 깜빡거리고 insert는 그떄 뷰만 추가
                //icAdapter.notifyDataSetChanged();
                icAdapter.notifyItemInserted(icArrayList.size());

                icRecyclerView.smoothScrollToPosition(icAdapter.getItemCount() - 1);


                //방의 프로필사진, 대화자 목록 데이터 받아오기
                //GetData9 task9 = new GetData9();
                //task9.execute(Chatlist[1], chatroom_no);

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    private class GetData9 extends AsyncTask<String, Void, String> {


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

                mJsonString9 = result;
                showResult9();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://54.180.122.247/global_communication/chat_name_image.php";
            String postParameters = "email=" + searchKeyword1;


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


    private void showResult9() {

        String TAG_JSON = "webnautes";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString9);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);


                String chatuser_image = item.getString("image");
                Log.d("채팅 프로필 사진", chatuser_image);
                String chatuser_name = item.getString("name");
                Log.d("채팅 프로필 이름", chatuser_name);


                //txtMessage.append(msg.obj.toString()+"\n");
                InChatData inchatData = new InChatData();

                inchatData.setchat_email(chatuser_email);
                inchatData.setchat_content(chat_content);
                Log.d("핸들러", chat_content);
                inchatData.setmy_email(MyEmailHolder);
                Log.d("이메일", MyEmailHolder);

                //프로필 사진, 이름
                inchatData.setchat_profile_image(chatuser_image);
                inchatData.setchat_profile_name(chatuser_name);
                inchatData.setchat_wdate(formatDate);


                icArrayList.add(inchatData);
                Log.d("클라이언트 선 전달2", String.valueOf(inchatData));
                // 밑의 두 방식 모두 가능하지만 첫번쨰 notifyDatasetchange는 깜빡거리고 insert는 그떄 뷰만 추가
                //icAdapter.notifyDataSetChanged();
                icAdapter.notifyItemInserted(icArrayList.size());
                icRecyclerView.smoothScrollToPosition(icAdapter.getItemCount() - 1);


            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    // drawlayout의 리사이클러뷰 목록 가져오기

    private class GetDrawer extends AsyncTask<String, Void, String> {


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

                mJsonString19 = result;
                showResult19();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://54.180.122.247/global_communication/inchat_userlist.php";
            String postParameters = "chatroom_no=" + searchKeyword1;


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


    private void showResult19() {

        String TAG_JSON = "webnautes";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString19);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);


                String inchatuser_email = item.getString("email");
                Log.d("in채팅 프로필 이메일", inchatuser_email);
                String inchatuser_image = item.getString("image");
                Log.d("in채팅 프로필 사진", inchatuser_image);
                String inchatuser_name = item.getString("name");
                Log.d("in채팅 프로필 이름", inchatuser_name);


                //txtMessage.append(msg.obj.toString()+"\n");
                M_InChatUserData m_inchatuserData = new M_InChatUserData();


                m_inchatuserData.setchat_email(inchatuser_email);
                m_inchatuserData.setchat_profile_image(inchatuser_image);
                m_inchatuserData.setchat_profile_name(inchatuser_name);


                m_icuser_ArrayList.add(m_inchatuserData);
                m_icuser_Adapter.notifyDataSetChanged();


            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    // 메시지 보내기 DB값에 빈방 추가
    public void Chat_UserList_Function(final String chatroom_id, final String userlist, final String chat_message, final String chatroom_type) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);

                hashMap.put("userlist", params[1]);

                hashMap.put("chat_message", params[2]);

                hashMap.put("chatroom_type", params[3]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/chat_add_userlist.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_id, userlist, chat_message, chatroom_type);
    }


    // 메시지 보내기 DB값에 빈방 추가
    public void ChatRoom_destroy_Function(final String chatroom_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/chatroom_destroy.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_id);
    }

    // 채팅방 나가기
    public void M_ChatRoom_exit_Function(final String chatroom_id, final String exit_email) {

        Log.d("멀티 채팅방 나가기","멀티 채팅방 나가기");

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);
                hashMap.put("email", params[1]);


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/m_chatroom_exit.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_id, exit_email);
    }


    // 멀티 채팅방 나간사람과 다시 대화할시 user_list에 추가하기
    public void M_ChatRoom_Exit_Person_Function(final String chatroom_id, final String email) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);
                hashMap.put("email", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/m_chatroom_exit_person.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_id,email);
    }

    // 멀티 채팅방 나간사람과 다시 대화할때 필요한 대화사이즈 -> 나갔다가 들어오면 채팅 내역 삭제
    public void M_ChatRoom_Exit_Remember_Function(final String chatroom_no, final String exit_email, final String remember_order) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                Toast.makeText(Chat_Multi.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("chatroom_no", params[0]);

                hashMap.put("exit_email", params[1]);

                hashMap.put("remember_order", params[2]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/m_chatroom_exit_remember.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(chatroom_no, exit_email, remember_order);
    }


}
