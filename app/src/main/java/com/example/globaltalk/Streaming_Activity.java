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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    String streaming_no;
    String room_name;
    String StreamName;
    String Streamer;

    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

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


        //나의 이메일
        streaming_no = intent.getStringExtra("streaming_no");
        Log.d("스트리밍 방번호", streaming_no);

        //친구의 이름
        room_name = intent.getStringExtra("room_name");
        Log.d("스트리밍 방제목",room_name);

        //채팅 유저리스트
        StreamName = intent.getStringExtra("StreamName");
        Log.d("스트리밍 키", StreamName);

        //Streamer = intent.getStringExtra("Streamer");
        //Log.d("스트리머 이름", Streamer);





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
        goCoderBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1920x1080);


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
                Toast.makeText(Streaming_Activity.this, statusMessage, Toast.LENGTH_LONG).show();
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
            Streaming_delete_Function(streaming_no);



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


            //방송 시작 시 DB에 정보 업데이트하기
            Streaming_Start_Function(StreamName, room_name, streaming_no);

            Log.d("정보", StreamName);
            Log.d("정보", room_name);
            Log.d("정보", streaming_no);


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
    public void Streaming_Start_Function(final String stream_name, final String room_name, final String streaming_no) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 누르셨습니다' 토스트
                Toast.makeText(Streaming_Activity.this, httpResponseMsg, Toast.LENGTH_LONG).show();



            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("stream_name", params[0]);

                hashMap.put("room_name", params[1]);

                hashMap.put("streaming_no", params[2]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/streaming_start.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(stream_name, room_name, streaming_no);
    }





    // 스트리밍 방 삭제 함수
    public void Streaming_delete_Function(final String streaming_no) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String chatResponseMsg) {

                super.onPostExecute(chatResponseMsg);

                // 채팅방 키 가져오기
                //Toast.makeText(Chat9.this, chatResponseMsg, Toast.LENGTH_LONG).show();


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("streaming_no", params[0]);


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/streaming_delete.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(streaming_no);
    }
















}
