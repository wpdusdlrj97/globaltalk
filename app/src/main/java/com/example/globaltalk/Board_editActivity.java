package com.example.globaltalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Board_editActivity extends AppCompatActivity {

    String BoardHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_edit);

        //Tab1으로부터 받아온 값
        Intent intent = getIntent();
        BoardHolder = intent.getStringExtra("board_id");
        Log.d("게시물 번호 받기", BoardHolder);


    }
}
