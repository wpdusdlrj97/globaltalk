package com.example.globaltalk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;
import com.wowza.gocoder.sdk.api.devices.WOWZCameraView;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

// Main app activity class
// Main app activity class
public class Streaming_Activity extends AppCompatActivity
        implements WOWZStatusCallback, View.OnClickListener {


    // The top-level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK camera view
    private WOWZCameraView goCoderCameraView;

    // The GoCoder SDK audio device
    private WOWZAudioDevice goCoderAudioDevice;

    // The GoCoder SDK broadcaster
    private WOWZBroadcast goCoderBroadcaster;

    // The broadcast configuration settings
    private WOWZBroadcastConfig goCoderBroadcastConfig;

    // Properties needed for Android 6+ permissions handling
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    private boolean mPermissionsGranted = true;
    private String[] mRequiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };


    //카메라 전환 버튼
    ImageView mBtnSwitchCamera;

    //카메라 플래시
    //ImageView mBtnSwitchCamera;

    //타이머
    private TextView timeView;
    private Thread timeThread = null;
    private Boolean isRunning = true;


    //방송 시작, 중지 버튼
    ImageView broadcastButton;



    //스트리밍을 위한 준비물 인텐트로 받아오기

    String room_name;
    String StreamName;
    String TeachHolder;
    String LearnHolder;

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;



    //채팅
    //TextView stream_txtMessage;
    Button stream_btnSend;
    EditText stream_editMessage;

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


    String Servermessage;
    int idx;
    int idx_name;
    String chatuser_email;
    String chatuser_name;
    String chat_content;


    private ArrayList<InStreamingData> isArrayList;
    private InStreamingAdapter isAdapter;
    private RecyclerView isRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        // Initialize the GoCoder SDK
        // 와우자의 GoCoder sdk를 라이센스키를 입력해 시작한다
        // SDK, Software Development Kit  소프트웨어 개발 도구 모음으로 SDK 안에는 개발에 도움이 될 개발 도구 프로그램, 디버깅 프로그램, 문서, API 등이 있다.
        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-CC46-010C-C8BD-B613-2A42");


        // Gocoder가 없을 경우 에러 메시지 띄우기
        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WOWZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }


        // Create an audio device instance for capturing and broadcasting audio
        // GoCoder 오디오 객체 생성
        goCoderAudioDevice = new WOWZAudioDevice();

        // Associate the WOWZCameraView defined in the U/I layout with the corresponding class member
        // 카메라 뷰 설정해주기
        goCoderCameraView = (WOWZCameraView) findViewById(R.id.camera_preview);


        // Associate the onClick() method as the callback for the broadcast button's click event
        // 방송하기 버튼에 클릭 리스너 연결
        broadcastButton = (ImageView) findViewById(R.id.broadcast_button);
        broadcastButton.setOnClickListener(this);


        // 카메라 전환 버튼
        mBtnSwitchCamera = findViewById(R.id.switch_camera);

        // 타이머
        timeView = findViewById(R.id.timeView);




        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();

        //방 제목
        room_name = intent.getStringExtra("room_name");
        Log.d("스트리밍 방제목",room_name);
        //스트림 네임
        StreamName = intent.getStringExtra("StreamName");
        Log.d("스트리밍 키", StreamName);

        TeachHolder = intent.getStringExtra("TeachHolder");

        LearnHolder = intent.getStringExtra("LearnHolder");






        //스트리밍 채팅

        context = this;
        stream_editMessage = findViewById(R.id.stream_editMessage);
        stream_btnSend = findViewById(R.id.stream_btnSend);

        //stream_txtMessage = findViewById(R.id.stream_txtMessage);


        isRecyclerView = (RecyclerView) findViewById(R.id.stream_bubble_list);
        isRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        isArrayList = new ArrayList<>();

        isAdapter = new InStreamingAdapter(this, isArrayList);
        isRecyclerView.setAdapter(isAdapter);


        //onCreate에서 스트리밍 시작 전에은 채팅창 GONE
        isRecyclerView.setVisibility(View.INVISIBLE);
        stream_editMessage.setVisibility(View.INVISIBLE);
        stream_btnSend.setVisibility(View.INVISIBLE);
        //stream_txtMessage.setVisibility(View.GONE);







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

                    idx_name = chatuser_email.indexOf("@");

                    chatuser_name = chatuser_email.substring(0, idx_name);


                    InStreamingData instreamingData = new InStreamingData();

                    instreamingData.setchat_email(chatuser_email);
                    Log.d("스트리밍 채팅이메일",chatuser_email);

                    instreamingData.setchat_profile_name(chatuser_name);
                    Log.d("스트리밍 채팅이름",chatuser_name);

                    instreamingData.setchat_content(chat_content);
                    Log.d("스트리밍 채팅내용",chat_content);

                    instreamingData.setStream_Name(StreamName);

                    instreamingData.setlearn_login(LearnHolder);
                    instreamingData.setteach_login(TeachHolder);



                    isArrayList.add(instreamingData);
                    Log.d("클라이언트 선 전달2", String.valueOf(instreamingData));
                    // 밑의 두 방식 모두 가능하지만 첫번쨰 notifyDatasetchange는 깜빡거리고 insert는 그떄 뷰만 추가
                    //icAdapter.notifyDataSetChanged();
                    isAdapter.notifyItemInserted(isArrayList.size());
                    isRecyclerView.smoothScrollToPosition(isAdapter.getItemCount() - 1);
                    //stream_txtMessage.append(msg.obj.toString() + "\n");




                }
            }
        };


        //서버에 메시지를 전송하는 버튼
        stream_btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /////////자바 서버로 보내기/////////
                //사용자가 입력한 메시지
                stream_message = stream_editMessage.getText().toString();
                //입력한 메시지가 null도 아니고 빈값도 아닐 시
                if (!stream_message.equals("")) {
                    //send 쓰레드를 전송용 쓰레드를 만들어서 호출
                    send = new SendThread(socket);
                    send.start();
                    stream_editMessage.setText("");
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
                output.writeUTF(StreamName);
                Log.d("MyEmail", StreamName);


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
        String sendmsg = stream_editMessage.getText().toString();
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
                        output.writeUTF(StreamName + ":" + sendmsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }










    //
// Enable Android's immersive, sticky full-screen mode
//
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    //
// Called when an activity is brought to the foreground
//
    @Override
    protected void onResume() {
        super.onResume();

        // If running on Android 6 (Marshmallow) and later, check to see if the necessary permissions
        // have been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted)
                ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        } else
            mPermissionsGranted = true;


        // Start the camera preview display
        // 카메라 미리보기(방송 시작 전 카메라)를 시작한다
        if (mPermissionsGranted && goCoderCameraView != null) {


            //만약 카메라가 전면으로 되어있지 않다면 전면으로 바꿔주기
            if (!goCoderCameraView.getCamera().isFront())
                goCoderCameraView.switchCamera();
            {



                if (goCoderCameraView.isPreviewPaused())
                    goCoderCameraView.onResume();
                else
                    goCoderCameraView.startPreview();




            }

        }





        // 카메라 전환
        mBtnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goCoderCameraView == null) return;

                WOWZCamera newCamera = goCoderCameraView.switchCamera();
                if (newCamera != null) {
                    if (newCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                        newCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);

                    boolean hasTorch = newCamera.hasCapability(WOWZCamera.TORCH);
                    if (hasTorch) {
                        //mBtnTorch.setState(newCamera.isTorchOn());
                        //mBtnTorch.setEnabled(true);
                    }
                }
            }
        });


        // Create a broadcaster instance
        // 방송 송신 객체를 새로 생성
        goCoderBroadcaster = new WOWZBroadcast();

        // Create a configuration instance for the broadcaster
        // 방송 송신 관련 설정을 위한 객체 생성
        goCoderBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_640x480);


        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Streaming Cloud live stream
        goCoderBroadcastConfig.setHostAddress("54.180.172.0");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setApplicationName("live");
        goCoderBroadcastConfig.setStreamName(StreamName);
        goCoderBroadcastConfig.setUsername("zzxcho11");
        goCoderBroadcastConfig.setPassword("0elektm");


        // Set the frame rate to 15 fps
        goCoderBroadcastConfig.setVideoFramerate(15);
        goCoderBroadcastConfig.set(WOWZMediaConfig.FRAME_SIZE_1920x1080);



        // Designate the camera preview as the video source
        goCoderBroadcastConfig.setVideoBroadcaster(goCoderCameraView);

        // Designate the audio device as the audio broadcaster
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);

    }


    // 스트리밍을 종료하지 않고 그냥 나갈 시에 OnPause에서 스트리밍 꺼주기
    @Override
    protected void onPause() {
        super.onPause();

        // return if the user hasn't granted the app the necessary permissions
        if (!mPermissionsGranted) return;

        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_SHORT).show();
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running

            goCoderBroadcaster.endBroadcast(this);

            timeThread.interrupt();

            //방 삭제해주기
            Streaming_delete_Function(StreamName);

            //onCreate에서 스트리밍 시작 전에은 채팅창 GONE
            //stream_editMessage.setVisibility(View.GONE);
            //stream_btnSend.setVisibility(View.GONE);
            //stream_txtMessage.setVisibility(View.GONE);

            //clear();

            Toast.makeText(this, "스트리밍 종료", Toast.LENGTH_SHORT).show();

        } else {


        }

    }


    //
// Callback invoked in response to a call to ActivityCompat.requestPermissions() to interpret
// the results of the permissions request
//
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // Check the result of each permission granted
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    //
// Utility method to check the status of a permissions request for an array of permission identifiers
//
    private static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }


    //
// The callback invoked upon changes to the state of the broadcast
//


    // 방송 모니터링 콜백 추가 (시작 전, 준비, 방송 활성화, 방송 중지, 방송 끝 )
    @Override
    public void onWZStatus(final WOWZStatus goCoderStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {
            case WOWZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WOWZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WOWZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WOWZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WOWZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Activity.this, statusMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //
// The callback invoked when an error occurs during a broadcast
//
    // 스트리밍 중 에러가 났을 때 콜백 메서드
    @Override
    public void onWZError(final WOWZStatus goCoderStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Activity.this,
                        "Streaming error: " + goCoderStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    //
// The callback invoked when the broadcast button is tapped
//

    //방송 버튼을 클릭할 때 (방송활성화 되어있을 시 -> 방송 끄기), (방송이 꺼져있을 때 -> 방송 키기)
    @Override
    public void onClick(View view) {
        // return if the user hasn't granted the app the necessary permissions
        if (!mPermissionsGranted) return;

        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running

            goCoderBroadcaster.endBroadcast(this);

            Glide.with(this)
                    .load(R.drawable.ic_start)
                    .into(broadcastButton);

            timeThread.interrupt();


            //방송 끌 시에는 방 없애기
            Streaming_delete_Function(StreamName);


            //스트리밍 종료 후에는 채팅창 GONE
            stream_editMessage.setVisibility(View.INVISIBLE);
            stream_btnSend.setVisibility(View.INVISIBLE);
            isRecyclerView.setVisibility(View.INVISIBLE);
            //stream_txtMessage.setVisibility(View.GONE);

            //스트리밍 종료 후 해당 채팅 삭제
            //clear();



        } else {
            // Start streaming
            goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
            // 스트리밍을 시작하면서 바꿔주기 아이콘
            Glide.with(this)
                    .load(R.drawable.ic_stop)
                    .into(broadcastButton);

            //타이머 시작
            timeThread = new Thread(new timeThread());
            timeThread.start();



            //방송 시작 시 채팅 소켓 연결
            client = new SocketClient(IP, PORT);
            //client 스타트시 SocketClient에서 run 메서드를 돌린다다
            client.start();



            //방송 시작 시 DB에 정보 넣어주기 (Insert로)
            Streaming_Start_Function(StreamName,room_name);

            //스트리밍 시작 후에는 채팅창 VISIBLE
            stream_editMessage.setVisibility(View.VISIBLE);
            stream_btnSend.setVisibility(View.VISIBLE);
            isRecyclerView.setVisibility(View.VISIBLE);



            //재시작할 때 채팅한 것 없애기
            //리사이클러뷰 초기화후, 재정렬
            isArrayList.clear();
            isAdapter.notifyDataSetChanged();






        }
    }





    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //int mSec = msg.arg1 % 100;
            //int sec = (msg.arg1 / 100) % 60;
            //int min = (msg.arg1 / 100) / 60;
            //int hour = (msg.arg1 / 100) / 360;

            int sec = msg.arg1 % 60;
            int min = (msg.arg1 / 60) % 60;
            int hour = (msg.arg1 / 60) / 60;


            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d", hour, min, sec);
            if (result.equals("01:00:00")) {
                Toast.makeText(Streaming_Activity.this, "방송을 시작한지 1시간이 경과하였습니다", Toast.LENGTH_SHORT).show();
            }
            timeView.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //timeView.setText("");
                                timeView.setText("00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }







    //스트리밍 방 생성 함수
    public void Streaming_Start_Function(final String stream_name, final String room_name) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'스트리밍을 시작하였습니다' 토스트
                Toast.makeText(Streaming_Activity.this, httpResponseMsg, Toast.LENGTH_SHORT).show();



            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("stream_name", params[0]);

                hashMap.put("room_name", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/streaming_start.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(stream_name, room_name);
    }





    // 스트리밍 방 삭제 함수
    public void Streaming_delete_Function(final String stream_name) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 스트리밍 종료 토스트
                Toast.makeText(Streaming_Activity.this, chatResponseMsg, Toast.LENGTH_SHORT).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("stream_name", params[0]);


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/streaming_delete.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(stream_name);
    }
















}
