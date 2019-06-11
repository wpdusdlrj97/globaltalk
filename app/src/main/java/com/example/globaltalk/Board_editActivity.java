package com.example.globaltalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class Board_editActivity extends AppCompatActivity {

    String BoardHolder;

    String ContentHolder;

    String img0Holder;
    String img1Holder;
    String img2Holder;
    String img3Holder;
    String img4Holder;
    String img5Holder;
    String img6Holder;
    String img7Holder;
    String img8Holder;

    TextView board_edit_text;

    TextView edit_maximage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_edit);

        //Tab1으로부터 받아온 값
        Intent intent = getIntent();
        BoardHolder = intent.getStringExtra("board_id");
        Log.d("게시물 번호 받기", BoardHolder);

        ContentHolder = intent.getStringExtra("content");
        Log.d("게시물 내용 받기", ContentHolder);

        img0Holder = intent.getStringExtra("img0");
        Log.d("게시물 이미지0 받기", img0Holder);

        img1Holder = intent.getStringExtra("img1");
        Log.d("게시물 이미지1 받기", img1Holder);

        img2Holder = intent.getStringExtra("img2");
        Log.d("게시물 이미지2 받기", img2Holder);

        img3Holder = intent.getStringExtra("img3");
        Log.d("게시물 이미지3 받기", img3Holder);

        img4Holder = intent.getStringExtra("img4");
        Log.d("게시물 이미지4 받기", img4Holder);

        img5Holder = intent.getStringExtra("img5");
        Log.d("게시물 이미지5 받기", img5Holder);

        img6Holder = intent.getStringExtra("img6");
        Log.d("게시물 이미지6 받기", img6Holder);

        img7Holder = intent.getStringExtra("img7");
        Log.d("게시물 이미지7 받기", img7Holder);

        img8Holder = intent.getStringExtra("img8");
        Log.d("게시물 이미지8 받기", img8Holder);


        //게시글 수정란
        board_edit_text=findViewById(R.id.board_edit_text);

        //이미지 수정 및 추가
        edit_maximage=findViewById(R.id.edit_maximage);





    }

    @Override
    public void onResume() {
        super.onResume();

        //글이 있을 경우
        board_edit_text.setText(ContentHolder);





    }









}
