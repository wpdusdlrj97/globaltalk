package com.example.globaltalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Heart_peopleActivity extends AppCompatActivity {


    String BoardHolder;

    String PeopleHolder;

    List<String> mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_people);


        //Tab1으로부터 받아온 값
        Intent intent = getIntent();
        BoardHolder = intent.getStringExtra("board_id");
        Log.d("게시물 번호 받기", BoardHolder);

        PeopleHolder = intent.getStringExtra("heart_people");
        Log.d("게시물 하트리스트 받기", PeopleHolder);


        // , 단위로 나누기
        mSelected = Arrays.asList(PeopleHolder.split(","));
        Log.d("게시물 하트 배열", String.valueOf(mSelected));


    }
}
