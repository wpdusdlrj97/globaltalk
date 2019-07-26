package com.example.globaltalk;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Chat1 extends AppCompatActivity {

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

    TextView friend_name;

    TextView txtMessage;
    Button btnSend;
    EditText editMessage;

    Handler msgHandler;

    //쓰레드 3개 만들 것
    SocketClient client;
    ReceiveThread receive;
    SendThread send;


    String IP="192.168.0.2";
    String PORT="9999";

    Socket socket;
    //LinkedList<SocketClient> threadList;
    Context context;

    String RoomHolder;
    String MyEmailHolder;
    String FriendNameHolder;


    String message;
    String formatDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);


        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();

        //나의 이메일
        MyEmailHolder = intent.getStringExtra("myemail");
        Log.d("나의 이메일 받아오기",MyEmailHolder);

        //친구의 이름
        FriendNameHolder = intent.getStringExtra("friend_name");
        Log.d("친구의 이름 받아오기",FriendNameHolder);

        RoomHolder = intent.getStringExtra("room_id");
        Log.d("방번호 받아오기",RoomHolder);


        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);



        context = this;
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        txtMessage = findViewById(R.id.txtMessage);
        friend_name = findViewById(R.id.friend_name);



         client = new SocketClient(IP, PORT);
         //client 스타트시 SocketClient에서 run 메서드를 돌린다다
         client.start();


         friend_name.setText(FriendNameHolder);


        //핸들러는 액티비티가 만들어질 때 여기서 동작한다
        //안드로이드에서 네트워크 작업을 할 시 백그라운드로 돌려야한다
        //백그라운드에서는 메인UI를 건드릴 수 없으므로 runOnUi를 쓰든지 핸들러를 써야한다
        //핸들러는 화면을 고치는 일을 전담한다
        msgHandler = new Handler(){
            //메시지 수신시 백그라운드 스레드에서 받은 메시지를 처리
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1111){ //1111은 식별자로 핸들러는 하나지만 핸들러를 호출하는 곳은 여러군데일 수 있다
                    //채팅 서버로부터 수신한 메시지를 텍스트뷰에 추가
                    txtMessage.append(msg.obj.toString()+"\n");
                }
            }
        };



        //서버에 메시지를 전송하는 버튼
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /////////자바 서버로 보내기/////////
                //사용자가 입력한 메시지
                message=editMessage.getText().toString();
                //입력한 메시지가 null도 아니고 빈값도 아닐 시
                if(message != null || !message.equals("")){
                    //send 쓰레드를 전송용 쓰레드를 만들어서 호출
                    send=new SendThread(socket);
                    send.start();
                    editMessage.setText("");
                }

                /////////DB 서버로 보내기/////////
                ChatRoom_Function(RoomHolder, MyEmailHolder, message, formatDate);





            }
        });




    }



    @Override
    public void onResume() {
        super.onResume();




    }



    //내부 클래스
    class SocketClient extends Thread{
        boolean threadAlive; //쓰레드의 동작여부 (필요한 이유-> 앱은 종료되는데 쓰레드가 죽지않는 경우 때문에)
        String ip;
        String port;
        OutputStream outputStream = null;
        DataOutputStream output = null;

        public SocketClient(String ip, String port){
            threadAlive=true;
            this.ip=ip;
            this.port=port;
        }

        public void run(){
            try{
                //채팅서버에 접속 (IP와 포트번호를 전달 시 accept 대기상태인 서버에서 소켓을 만들어준다)
                socket = new Socket(ip, Integer.parseInt(port));
                //서버에 메시지를 전달하기 위한 스트림 생성
                output= new DataOutputStream(socket.getOutputStream());
                //서버에서 받은 메시지를 수신하는 스레드 생성
                receive = new ReceiveThread(socket);
                receive.start();


                //방번호 -> DB의 고유한 키값
                output.writeUTF(RoomHolder);
                Log.d("Room",RoomHolder);


                //식별자 -> 나의 이메일
                output.writeUTF(MyEmailHolder);
                Log.d("MyEmail",MyEmailHolder);

                //식별자 -> 친구의 이메일
                //output.writeUTF(FriendEmailHolder);
                //Log.d("FriendEmail",FriendEmailHolder);



                //output.writeUTF(mac9);
                //Log.d("mac9",mac9);
                //output.writeUTF(mac19);
                //Log.d("mac19",mac19);
                //그러면 eclipse의 서버에서 아이피 주소와 Mac Address가 출력이 된다

                //서버 코드
                //InetAddress ip=socket.getInetAddress();
                //System.out.println(ip+" connected");
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }// 소켓 클라이언트 쓰레드의 끝


    //내부 클래스 (메시지 수신용 쓰레드) 메시지를 받는 역할을 한다
    class ReceiveThread extends Thread {
        Socket socket= null;
        DataInputStream input=null;
        public ReceiveThread(Socket socket){
            this.socket=socket;
            try{
                //채팅서버로부터 메시지를 받기 위한 스트림 생성
                input = new DataInputStream(socket.getInputStream());

            }catch(Exception e){
                e.printStackTrace();
            }


        }
        public void run(){
            try{
                while(input!=null){
                    //채팅 서버로부터 받은 메시지
                    String msg=input.readUTF();
                    if(msg!=null){
                        //핸들러에게 전달할 메시지 객체
                        Message hdmsg=msgHandler.obtainMessage();
                        hdmsg.what=1111; //메시지의 식별자
                        hdmsg.obj=msg; //메시지의 본문
                        //핸들러에게 메시지 전달(화면 변경 요청)
                        msgHandler.sendMessage(hdmsg);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }


        }
    }// 수신용 쓰레드 클래스의 끝


    //내부 클래스
    class SendThread extends  Thread{
        Socket socket;
        String sendmsg = editMessage.getText().toString();
        DataOutputStream output;
        public SendThread(Socket socket){
            this.socket = socket;
            try{
                //채팅서버로 메시지를 보내기 위한  스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public void run(){
            try{
                if(output !=null){
                    if(sendmsg != null){
                        output.writeUTF(MyEmailHolder+ ":" + sendmsg);
                    }
                }

            }catch (Exception e){
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
                Toast.makeText(Chat1.this, chatResponseMsg, Toast.LENGTH_LONG).show();



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






}
