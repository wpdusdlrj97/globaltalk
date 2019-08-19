package com.example.globaltalk;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class Player_Activity extends AppCompatActivity {

    // The top-level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK camera view
    private WOWZPlayerView mStreamPlayerView;

    private WOWZPlayerConfig mStreamPlayerConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_);

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



    }


    @Override
    protected void onResume() {
        super.onResume();

        mStreamPlayerConfig = new WOWZPlayerConfig();
        mStreamPlayerConfig.setIsPlayback(true);
        mStreamPlayerConfig.setHostAddress("54.180.172.0");
        mStreamPlayerConfig.setApplicationName("live");
        mStreamPlayerConfig.setStreamName("heungmin@naver.com");
        mStreamPlayerConfig.setPortNumber(1935);

        mStreamPlayerConfig.set(WOWZMediaConfig.FRAME_SIZE_1920x1080);


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





       if (mStreamPlayerView.isReadyToPlay()) {

            Toast.makeText(this, "해당 방송 로딩중",Toast.LENGTH_LONG).show();

            //여기서 프로그레스 다이얼로그

        }









    }





    class StatusCallback implements WOWZStatusCallback {
        @Override
        public void onWZStatus(WOWZStatus wzStatus) {



            /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    WOWZLog.debug("DECODER STATUS: 000 [player activity] current: "+ wzStatus.toString());
                    switch( wzStatus.getState()) {
                        case WOWZPlayerView.STATE_PLAYING:

                            Log.d("단계","1");

                            break;

                        case WOWZPlayerView.STATE_READY_TO_PLAY:


                            Log.d("단계","2");

                            break;

                        case WOWZPlayerView.STATE_PREBUFFERING_STARTED:

                            Log.d("단계","3");

                            break;

                        case WOWZPlayerView.STATE_PREBUFFERING_ENDED:

                            Log.d("단계","4");

                            break;

                        case WOWZPlayerView.STATE_PLAYBACK_COMPLETE:

                            Log.d("단계","5");


                            break;

                        default:

                            break;
                    }

                }
            });*/


        }
        @Override
        public void onWZError(WOWZStatus wzStatus) {




        }
    }







}
