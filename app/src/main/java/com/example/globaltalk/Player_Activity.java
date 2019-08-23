package com.example.globaltalk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.player.GlobalPlayerStateManager;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Player_Activity extends AppCompatActivity {

    // The top-level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK camera view
    private WOWZPlayerView mStreamPlayerView;

    private WOWZPlayerConfig mStreamPlayerConfig;


    String EmailHolder;
    //스트림 네임(스트리밍 방 나누기)
    String StreamName;
    //스트리밍 방 번호 (채팅 시 필요)
    String streaming_no;

    private String TeachHolder;
    private String LearnHolder;


    ProgressDialog progressDialog;


    //채팅
    //TextView player_txtMessage;
    Button player_btnSend;
    EditText player_editMessage;
    ImageView streaming_exit_button;

    Handler msgHandler;

    String IP = "192.168.0.41";
    String PORT = "9999";

    Socket socket;
    Context context;

    String stream_message;

    //쓰레드 3개 만들 것
    SocketClient client;
    ReceiveThread receive;
    SendThread send;


    //플레이어 측 리사이클러뷰 적용
    String Servermessage;
    int idx;
    int idx_name;
    String chatuser_email;
    String chatuser_name;
    String chat_content;


    private ArrayList<InPlayerData> ipArrayList;
    private InPlayerAdapter ipAdapter;
    private RecyclerView ipRecyclerView;


    private ProgressDialog mBufferingDialog = null;

    //나갔을 때 여부 (스트리머의 종료와
    int exit;
    int ask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_);



        Intent intent = getIntent();
        EmailHolder = intent.getStringExtra("EmailHolder");
        Log.d("이메일 받기", EmailHolder);

        StreamName = intent.getStringExtra("Stream_name");
        Log.d("스트림네임 받기", StreamName);

        streaming_no = intent.getStringExtra("streaming_no");
        Log.d("streaming_no 받기", streaming_no);

        TeachHolder = intent.getStringExtra("TeachHolder");
        Log.d("Teach 받기", TeachHolder);

        LearnHolder = intent.getStringExtra("LearnHolder");
        Log.d("Learn 받기", LearnHolder);





        ask=1;
        exit=0;


        // Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-CC46-010C-C8BD-B613-2A42");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WOWZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }


        mStreamPlayerView = (WOWZPlayerView) findViewById(R.id.vwStreamPlayer);




        //스트리밍 채팅

        context = this;
        player_editMessage = findViewById(R.id.player_editMessage);
        player_btnSend = findViewById(R.id.player_btnSend);


        // 나가기 버튼
        streaming_exit_button = findViewById(R.id.streaming_exit_button);



        client = new SocketClient(IP, PORT);
        //client 스타트시 SocketClient에서 run 메서드를 돌린다다
        client.start();



        ipRecyclerView = (RecyclerView) findViewById(R.id.player_bubble_list);
        ipRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        ipArrayList = new ArrayList<>();

        ipAdapter = new InPlayerAdapter(this, ipArrayList);
        ipRecyclerView.setAdapter(ipAdapter);



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
                    //player_txtMessage.append(msg.obj.toString() + "\n");

                    //서버로부터 받은 메시지형태 -> heungmin@naver.com : aaaaa
                    //따라서 이메일과 aaaa를 구분한뒤 adapter에 넣어줘야 한다
                    Servermessage = msg.obj.toString();

                    idx = Servermessage.indexOf(":");

                    chatuser_email = Servermessage.substring(0, idx);

                    // 뒷부분을 추출
                    // 아래 substring은 @ 바로 뒷부분인 n부터 추출된다.
                    chat_content = Servermessage.substring(idx + 1);


                    idx_name = chatuser_email.indexOf("@");

                    chatuser_name = chatuser_email.substring(0, idx_name);


                    InPlayerData inplayerData = new InPlayerData();

                    inplayerData.setchat_email(chatuser_email);
                    Log.d("스트리밍 채팅이메일",chatuser_email);
                    inplayerData.setchat_profile_name(chatuser_name);
                    Log.d("스트리밍 채팅이름",chatuser_name);

                    inplayerData.setchat_content(chat_content);
                    Log.d("스트리밍 채팅내용",chat_content);

                    inplayerData.setStream_Name(StreamName);

                    inplayerData.setlearn_login(LearnHolder);
                    inplayerData.setteach_login(TeachHolder);


                    ipArrayList.add(inplayerData);
                    Log.d("클라이언트 선 전달2", String.valueOf(inplayerData));
                    // 밑의 두 방식 모두 가능하지만 첫번쨰 notifyDatasetchange는 깜빡거리고 insert는 그떄 뷰만 추가
                    //icAdapter.notifyDataSetChanged();
                    ipAdapter.notifyItemInserted(ipArrayList.size());
                    ipRecyclerView.smoothScrollToPosition(ipAdapter.getItemCount() - 1);
                    //stream_txtMessage.append(msg.obj.toString() + "\n");

                }
            }
        };




        //서버에 메시지를 전송하는 버튼
        player_btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /////////자바 서버로 보내기/////////
                //사용자가 입력한 메시지
                stream_message = player_editMessage.getText().toString();
                //입력한 메시지가 null도 아니고 빈값도 아닐 시
                if (!stream_message.equals("")) {
                    //send 쓰레드를 전송용 쓰레드를 만들어서 호출
                    send = new SendThread(socket);
                    send.start();
                    player_editMessage.setText("");
                }




            }
        });




    }



    //채팅 내부 클래스
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
                output.writeUTF(StreamName);
                Log.d("Room",StreamName);

                //식별자 -> 나의 이메일
                output.writeUTF(EmailHolder);
                Log.d("MyEmail", EmailHolder);


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
        String sendmsg = player_editMessage.getText().toString();
        DataOutputStream output;

        public SendThread(Socket socket) {
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
                        output.writeUTF(EmailHolder + ":" + sendmsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }







    @Override
    protected void onResume() {
        super.onResume();



        streaming_exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Player_Activity.this, Streaming_ListActivity.class);

                Log.d("피니시","종료");
                exit=1;

                intent.putExtra("EmailHolder", EmailHolder);

                intent.putExtra("TeachHolder", TeachHolder);
                intent.putExtra("LearnHolder", LearnHolder);



                startActivity(intent);

                finish();




            }
        });




        mStreamPlayerConfig = new WOWZPlayerConfig();
        mStreamPlayerConfig.setIsPlayback(true);
        mStreamPlayerConfig.setHostAddress("54.180.172.0");
        mStreamPlayerConfig.setApplicationName("live");
        mStreamPlayerConfig.setStreamName(StreamName);
        mStreamPlayerConfig.setPortNumber(1935);

        mStreamPlayerConfig.set(WOWZMediaConfig.FRAME_SIZE_640x480);


        //기본 스트림 연결에 실패한 경우 Wowza GoCoder SDK 앱이 Apple HLS 스트림을 폴백으로 재생하도록 지시 할 수 있습니다.

        //mStreamPlayerConfig.setHLSEnabled(true);
        //mStreamPlayerConfig.setHLSBackupURL("http://54.180.172.0:1935/live/myStream/playlist.m3u8");

        mStreamPlayerConfig.setAudioEnabled(true);
        mStreamPlayerConfig.setVideoEnabled(true);

        mStreamPlayerView.setVolume(3);
        mStreamPlayerView.setScaleMode(WOWZMediaConfig.FILL_VIEW);


        WOWZStatusCallback statusCallback = new StatusCallback();
        mStreamPlayerView.play(mStreamPlayerConfig, statusCallback);

        //mStreamPlayerView.getStreamStats();



    }



    @Override
    protected void onPause() {
        super.onPause();

        //온 퍼즈 상태에서 해당 플레이어 종료
        mStreamPlayerView.stop();
        finish();

    }



    class StatusCallback implements WOWZStatusCallback {

        @Override
        public void onWZStatus(final WOWZStatus goCoderStatus) {

            //final WOWZStatus playerStatus = new WOWZStatus(wzStatus);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    switch (goCoderStatus.getState()) {
                        case WOWZState.STARTING:
                            Log.d("스테이트","START");
                            break;

                        case WOWZState.READY:
                            Log.d("스테이트","READY");
                            break;

                        case WOWZState.RUNNING:
                            Log.d("스테이트","RUNNING");
                            break;

                        case WOWZState.STOPPING:
                            Log.d("스테이트","STOPPING");
                            break;

                        case WOWZState.IDLE:
                            Log.d("스테이트","IDLE");



                            if(exit==1){ //그냥 방나가기

                            }else{ // 스트리머의 방종

                                if(ask==1){
                                    mStreamPlayerView.stop();
                                }

                                askonetime(ask);

                                ask=ask+1;

                            }



                            break;

                        default:
                            return;
                    }
                }
            });





        }
        @Override
        public void onWZError(WOWZStatus wzStatus) {




        }
    }




    //한번 실행 메서드
    public void askonetime(int a){

        if(a==1){

            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
            alert_confirm.setMessage("스트리머가 방송을 종료하였습니다").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                            Intent intent = new Intent(Player_Activity.this, Streaming_ListActivity.class);


                            intent.putExtra("EmailHolder", EmailHolder);

                            intent.putExtra("TeachHolder", TeachHolder);
                            intent.putExtra("LearnHolder", LearnHolder);

                            startActivity(intent);

                            finish();



                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();

            //alert.dismiss();
            //finish();

        }else{


            Log.d("에스크 빠져나가기", String.valueOf(ask));
        }

    }








}
