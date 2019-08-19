package com.example.globaltalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Belal on 2/3/2016.
 */

//Our class extending fragment
public class Tab6 extends Fragment {


    private String EmailHolder;

    Button button_Streaming;

    Button button_play;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            EmailHolder = getArguments().getString("EmailHolder");
        }
        Log.d("이메일 받아오기",EmailHolder);


        View rootView = inflater.inflate(R.layout.tab6, container, false);

        button_Streaming = (Button) rootView.findViewById(R.id.button_Streaming);

        button_play = (Button) rootView.findViewById(R.id.button_play);

        return rootView;


    }



    //생명주기 활용 필요

    @Override
    public void onResume() {
        super.onResume();


        button_Streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent6 = new Intent(getActivity(), Streaming_Activity.class);

                //intent.putExtra(UserEmail, email);
                startActivity(intent6);


            }
        });



        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent6 = new Intent(getActivity(), Player_Activity.class);

                //intent.putExtra(UserEmail, email);
                startActivity(intent6);


            }
        });













    }




}