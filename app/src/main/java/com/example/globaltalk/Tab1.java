package com.example.globaltalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab1 extends Fragment {

    private String EmailHolder;
    private String NameHolder;

    public static final String UserEmail = "";
    public static final String UserName = "name";


    ImageView write_button;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
            NameHolder = getArguments().getString("NameHolder");
        }

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        View rootView = inflater.inflate(R.layout.tab1, container, false);

        write_button = (ImageView) rootView.findViewById(R.id.write_button);


        return rootView;

    }



    @Override
    public void onResume() {
        super.onResume();

        // 게시물 쓰기 화면으로 이동
        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getActivity(), Board_writeActivity.class);
                intent1.putExtra(UserEmail, EmailHolder);
                intent1.putExtra(UserName, NameHolder);
                startActivity(intent1);

            }
        });

    }




}