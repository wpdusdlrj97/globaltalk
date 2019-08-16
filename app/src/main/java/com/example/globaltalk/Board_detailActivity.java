package com.example.globaltalk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Board_detailActivity extends AppCompatActivity {

    public static Context mcontext;

    private static String TAG = "board_detail_test";


    String clientId = "cv81DQOKSlPw4lX1hyEe";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "s3Rf0jQDf0";//애플리케이션 클라이언트 시크릿값";



    String sourceHolder;
    String targetHolder;

    String sourceLang;
    String targetLang;


    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult;

    String BoardHolder;
    String LoginEmailHolder;
    String LoginNameHolder;
    String LoginImageHolder;

    ImageView board_detail_pfimage;

    TextView board_detail_name;
    TextView board_detail_time;
    TextView board_detail_text;
    TextView board_detail_transtext;


    TextView board_detail_teach;
    TextView board_detail_learn;


    ImageView board_detail_image0;
    ImageView board_detail_image1;
    ImageView board_detail_image2;
    ImageView board_detail_image3;

    ImageView board_detail_image4;
    ImageView board_detail_image5;
    ImageView board_detail_image6;
    ImageView board_detail_image7;
    ImageView board_detail_image8;

    ImageView board_detail_back;

    ImageView board_detail_heart;

    TextView board_detail_count;
    TextView board_detail_comment_count;

    ImageView board_detail_comment;

    ImageView board_detail_translate;

    ImageView board_detail_more;

    ImageView board_detail_sound;

    ImageView comment_write_send;

    EditText comment_write_content;

    String mJsonString;
    String cJsonString;


    String profile_image;
    String profile_teach;
    String profile_learn;

    String wdate;
    String content;
    String writer;
    String writeremail;


    String img0;
    String img1;
    String img2;
    String img3;
    String img4;
    String img5;
    String img6;
    String img7;
    String img8;

    String board_id;
    String heart_count;
    String heart_people;
    int comment_count;



    String email_check;


    String CommentHolder;

    int Heart_button;
    int Heart_count;


    int translate=0;

    private NaverDetectionTask mNaverDetectionTask;
    private NaverTTSTask mNaverTTSTask;

    //TTS 대상
    String[] mTextString;
    static String speaker;



    //댓글
    private ArrayList<CommentData> cArrayList;
    private CommentAdapter cAdapter;
    private RecyclerView cRecyclerView;


    //키보드 자판 내리기
    private InputMethodManager imm;
    String formatDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);



        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);


        mcontext=this;

        //Tab1으로부터 받아온 값
        Intent intent = getIntent();
        BoardHolder = intent.getStringExtra("board_id");
        Log.d("게시물 번호 받기", BoardHolder);
        LoginEmailHolder = intent.getStringExtra("login_email");
        LoginNameHolder = intent.getStringExtra("login_name");
        sourceHolder= intent.getStringExtra("learnHolder");
        targetHolder= intent.getStringExtra("teachHolder");
        LoginImageHolder = intent.getStringExtra("login_image");

        Log.d("현재 로그인 프사 받기", LoginImageHolder);



        // 뒤로 가기
        board_detail_back = (ImageView)findViewById(R.id.board_detail_back);

        board_detail_pfimage=findViewById(R.id.board_detail_pfimage);

        board_detail_name=findViewById(R.id.board_detail_name);
        board_detail_time=findViewById(R.id.board_detail_time);
        board_detail_text=findViewById(R.id.board_detail_text);
        board_detail_transtext=findViewById(R.id.board_detail_transtext);


        board_detail_teach=findViewById(R.id.board_detail_teach);
        board_detail_learn=findViewById(R.id.board_detail_learn);




        board_detail_image0=findViewById(R.id.board_detail_image0);
        board_detail_image1=findViewById(R.id.board_detail_image1);
        board_detail_image2=findViewById(R.id.board_detail_image2);
        board_detail_image3=findViewById(R.id.board_detail_image3);
        board_detail_image4=findViewById(R.id.board_detail_image4);
        board_detail_image5=findViewById(R.id.board_detail_image5);
        board_detail_image6=findViewById(R.id.board_detail_image6);
        board_detail_image7=findViewById(R.id.board_detail_image7);
        board_detail_image8=findViewById(R.id.board_detail_image8);


        board_detail_heart=findViewById(R.id.board_detail_heart);
        board_detail_count=findViewById(R.id.board_detail_count);
        board_detail_comment=findViewById(R.id.board_detail_comment);
        board_detail_comment_count=findViewById(R.id.board_detail_comment_count);

        board_detail_translate=findViewById(R.id.board_detail_translate);
        board_detail_more=findViewById(R.id.board_detail_more);
        board_detail_sound=findViewById(R.id.board_detail_sound);


        comment_write_send=findViewById(R.id.comment_write_send);
        comment_write_content=findViewById(R.id. comment_write_content);



        //영어 일때
        if(sourceHolder.equals("English")){
            sourceLang="en";
            //speaker="clara";

        }

        //한국어 일때
        if(sourceHolder.equals("Korean")){
            sourceLang="ko";
            //speaker="mijin";
        }

        //일어 일때
        if(sourceHolder.equals("Japanese")){
            sourceLang="ja";
            //speaker="yuri";
        }

        //중국어 일때
        if(sourceHolder.equals("Chinese")){
            sourceLang="zh-CN";
            //speaker="meimei";
        }


        //영어 일때
        if(targetHolder.equals("English")){
            targetLang="en";

        }

        //한국어 일때
        if(targetHolder.equals("Korean")){
            targetLang="ko";

        }

        //일어 일때
        if(targetHolder.equals("Japanese")){
            targetLang="ja";

        }

        //중국어 일때
        if(targetHolder.equals("Chinese")){
            targetLang="zh-CN";

        }


        // 입력받는 방법을 관리하는 Manager객체를  요청하여 InputMethodmanager에 반환한다.
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        cRecyclerView = (RecyclerView) findViewById(R.id.board_comment_list);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(this));





        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        cArrayList = new ArrayList<>();

        cAdapter = new CommentAdapter(this, cArrayList);
        cRecyclerView.setAdapter(cAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        cRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));




    }

    @Override
    public void onResume() {
        super.onResume();


        // 게시물 내용 가져오기
        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/board_detail.php", "");



        cArrayList.clear();
        cAdapter.notifyDataSetChanged();
        // 댓글 내용 가져오기
        GetComment task_comment = new GetComment();
        task_comment.execute(BoardHolder);


        board_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //프로필 이미지 클릭 시 해당 프로필로 이동
        board_detail_pfimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(LoginEmailHolder.equals(writeremail)){//나의 게시물을 조회했을 때

                    Intent intent = new Intent(Board_detailActivity.this, My_Profile_Activity.class);

                    intent.putExtra("My_email", LoginEmailHolder);

                    startActivity(intent);

                }else{ // 상대방의 게시물을 조회했을 때

                    Intent intent = new Intent(Board_detailActivity.this, Friends_Profile_Activity.class);

                    intent.putExtra("Friend_email",writeremail);

                    intent.putExtra("Login_email", LoginEmailHolder);

                    startActivity(intent);
                }

            }
        });


        // 전송버튼
        comment_write_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //서버에 전송
                // 작성된 글의 존재 여부
                CommentHolder =  comment_write_content.getText().toString();

                // 글을 작성하면 최초로 DB에 insert되고 그 이후에 가장 최근 board_id를 불러와서 사진을 update하는 형식

                if (TextUtils.isEmpty(CommentHolder)) { // 글이 입력되지 않았을 때
                    Toast.makeText(Board_detailActivity.this, "내용을 입력해주세요", Toast.LENGTH_LONG).show();

                } else { // 글이 입력되었다면 텍스트 업로드
                    TextFunction(LoginNameHolder,LoginEmailHolder,CommentHolder,BoardHolder);
                    comment_write_content.setText("");

                    //                CommentData commentData = new CommentData();
                    //
                    //                commentData.setMember_name(writer_name);
                    //                Log.d("어레이리스트에 name 추가", writer_name);
                    //                commentData.setMember_email(writer_email);
                    //                Log.d("어레이리스트에 email 추가", writer_email);
                    //                commentData.setMember_image(profile_image);
                    //                Log.d("어레이리스트에 image 추가", profile_image);
                    //                commentData.setMember_time(wdate);
                    //                Log.d("어레이리스트에 time 추가", wdate);
                    //                commentData.setMember_content(content);
                    //                Log.d("어레이리스트에 content 추가", content);
                    //
                    //                cArrayList.add(commentData);
                    //                Log.d("어레이리스트에 추가", String.valueOf(commentData));
                    //                cAdapter.notifyDataSetChanged();


                    CommentData commentData9 = new CommentData();

                    commentData9.setLogin_email(LoginEmailHolder);
                    Log.d("어레이리스트9에 Login_email 추가", LoginEmailHolder);

                    commentData9.setComment_id(String.valueOf(0));
                    Log.d("어레이리스트에 comment_id 추가", String.valueOf(0));

                    commentData9.setBoard_id(board_id);
                    Log.d("어레이리스트에 board_id 추가", board_id);

                    commentData9.setMember_name(LoginNameHolder);
                    Log.d("어레이리스트9에 name 추가", LoginNameHolder);
                    commentData9.setMember_email(LoginEmailHolder);
                    Log.d("어레이리스트9에 email 추가", LoginEmailHolder);
                    commentData9.setMember_image(LoginImageHolder);
                    Log.d("어레이리스트9에 image 추가", LoginImageHolder);
                    commentData9.setMember_time(formatDate);
                    Log.d("어레이리스트9에 time 추가", formatDate);
                    commentData9.setMember_content(CommentHolder);
                    Log.d("어레이리스트9에 content 추가", CommentHolder);

                    //cArrayList.add(0, dict); //첫 줄에 삽입
                    cArrayList.add(commentData9); //마지막 줄에 삽입
                    //cAdapter.notifyItemInserted(0);
                    cAdapter.notifyDataSetChanged();


                    //댓글 수 증가
                    comment_count=comment_count+1;
                    board_detail_comment_count.setText(String.valueOf(comment_count));

                    //키보드 자판 내리기
                    imm.hideSoftInputFromWindow(comment_write_content.getWindowToken(), 0);



                }


                //리사이클러뷰에 적용


            }
        });



        board_detail_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Heart_count= Integer.parseInt(heart_count);

                if(Heart_button==1){//하트를 클릭한 기록이 있을 경우, 하트 해제
                    Glide.with(Board_detailActivity.this)
                            .load(R.drawable.heart)
                            .into(board_detail_heart);
                    //하트 해제
                    Heart_button=Heart_button-1;

                    Log.d("하트 개수 반영 전", String.valueOf(Heart_count));
                    //하트 수 -1 후에 viewholder에 set해주기
                    Heart_count = Heart_count-1;
                    Log.d("하트 개수 반영 전", String.valueOf(Heart_count));
                    // string heart_count도 재정의 필요
                    heart_count= String.valueOf(Heart_count);

                    board_detail_count.setText(String.valueOf(Heart_count));

                    //Log.d("하트버튼 누른사람 전"+position,email_check);
                    //특정 문자열 제거해주기
                    //email_check=email_check.replace(loginemail,"");
                    //Log.d("하트버튼 누른사람 후"+position,email_check);

                    //서버,mysql에서 삭제
                    UserHateFunction(board_id,LoginEmailHolder);


                }else{
                    Glide.with(Board_detailActivity.this)
                            .load(R.drawable.redheart)
                            .into(board_detail_heart);
                    //하트 추가
                    Heart_button=Heart_button+1;

                    Log.d("하트 개수 반영 전", String.valueOf(Heart_count));
                    //하트 수 +1 후에 viewholder에 set해주기
                    Heart_count = Heart_count+1;
                    Log.d("하트 개수 반영 후", String.valueOf(Heart_count));

                    // string heart_count도 재정의 필요
                    heart_count= String.valueOf(Heart_count);

                    board_detail_count.setText(String.valueOf(Heart_count));

                    //Log.d("하트버튼 누른사람 전"+position,email_check);
                    //특정 문자열 추가해주기
                    //email_check=email_check.concat(loginemail);
                    //Log.d("하트버튼 누른사람 후"+position,email_check);

                    //서버,mysql에서 추가
                    UserLikeFunction(board_id,LoginEmailHolder);

                }

                //Toast.makeText(context, String.valueOf(finalHeart_button), Toast.LENGTH_LONG).show();

            }
        });



        // TTS
        board_detail_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, String.valueOf(position), Toast.LENGTH_LONG).show();
                String mText;
                mText = board_detail_text.getText().toString();
                mTextString = new String[]{mText};

                //AsyncTask 실행
                mNaverDetectionTask = new NaverDetectionTask();
                mNaverDetectionTask.execute(mTextString);

            }
        });


        board_detail_transtext.setVisibility(View.GONE);

        // 번역 버튼
        board_detail_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 번역 텍스트란 visible
                board_detail_transtext.setVisibility(View.VISIBLE);

                //TextHolder = bList.get(position).getcontent();
                //Toast.makeText(context, TextHolder, Toast.LENGTH_LONG).show();
                //viewholder.board_target_translate.setVisibility(View.VISIBLE);
                //viewholder.board_target_translate.setText(bList.get(position).getcontent());
                TranslateTask translateTask = new TranslateTask();
                String sText = content;
                Log.d("게시물 내용",sText);

                //bitmap=new ConvertUrlToBitmap().execute(array[i]).get();


                // 하트 클릭이 안되어 있을 때
                if(translate==0){
                    try {
                        //데이터를 translateAsynctask로부터 가져온다
                        String translate = translateTask.execute(content).get();
                        Log.d("게시물 translate",translate);

                        //JSON데이터를 자바객체로 변환해야 한다.
                        //Gson을 사용할 것이다.
                        Gson gson = new GsonBuilder().create();
                        JsonParser parser = new JsonParser();
                        JsonElement rootObj = parser.parse(translate)
                                //원하는 데이터 까지 찾아 들어간다.
                                .getAsJsonObject().get("message")
                                .getAsJsonObject().get("result");
                        //안드로이드 객체에 담기
                        TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);

                        // 모든 처리 이후에 문자열 set
                        board_detail_transtext.setText(items.getTranslatedText());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    translate=translate+1;
                }else{
                    board_detail_transtext.setVisibility(View.GONE);
                    translate=translate-1;
                }









            }
        });



        // 더보기란 클릭했을 때
        board_detail_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //접속한 사람과 게시물을 쓴 사람을 구분하기



                //같다면 수정화면으로 넘어가기
                if(LoginEmailHolder.equals(writeremail)) {

                    final List<String> ListItems = new ArrayList<>();
                    ListItems.add("수정하기");
                    ListItems.add("삭제하기");
                    final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Board_detailActivity.this);

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {

                            //수정하기
                            if(pos==0){

                                Intent intent = new Intent(Board_detailActivity.this, Board_editActivity.class);

                                // Board_ID 넘기기 (서버에서 수정할 시 board_id 필요)
                                intent.putExtra("board_id", board_id);
                                Log.d("보드 번호 수정",board_id);

                                //글
                                intent.putExtra("content", content);
                                Log.d("보드 내용 수정",content);

                                //이미지 0~9
                                intent.putExtra("img0", img0);
                                Log.d("보드 이미지 수정0", img0);

                                intent.putExtra("img1", img1);
                                Log.d("보드 이미지 수정1",img1);

                                intent.putExtra("img2", img2);
                                Log.d("보드 이미지 수정2",img2);

                                intent.putExtra("img3", img3);
                                Log.d("보드 이미지 수정3",img3);

                                intent.putExtra("img4", img4);
                                Log.d("보드 이미지 수정4",img4);

                                intent.putExtra("img5", img5);
                                Log.d("보드 이미지 수정5",img5);

                                intent.putExtra("img6", img6);
                                Log.d("보드 이미지 수정6",img6);

                                intent.putExtra("img7", img7);
                                Log.d("보드 이미지 수정7",img7);

                                intent.putExtra("img8", img8);
                                Log.d("보드 이미지 수정8",img8);

                                startActivityForResult(intent, 1001);


                            }else{

                                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Board_detailActivity.this);
                                alert_confirm.setMessage("해당 게시물을 정말 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                DeleteFunction(board_id);
                                                Log.d("포지션",board_id);



                                            }
                                        }).setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(context, "취소하셨습니다", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        });
                                AlertDialog alert = alert_confirm.create();
                                alert.show();


                            }

                        }
                    });

                    builder.show();


                }else{
                    Toast.makeText(Board_detailActivity.this, "본인의 게시물만 수정가능합니다", Toast.LENGTH_LONG).show();
                }


            }
        });


        //Log.d("하트-이메일 대조"+position,loginemail);
        //final int[] heart_button = {bList.get(position).getheart_boolean()};

        //final int[] heart_count = {Integer.parseInt(bList.get(position).getheart_count())};







    }


    class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Board_detailActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                //pf_name.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/board_detail.php";
            String postParameters = "board_id=" + BoardHolder;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="webnautes";


        String TAG_EMAIL ="email";
        String TAG_WRITER ="writer";
        String TAG_WDATE ="wdate";
        String TAG_CONTENT = "content";

        String TAG_IMG0 ="img_file0";
        String TAG_IMG1 ="img_file1";
        String TAG_IMG2 ="img_file2";

        String TAG_IMG3 ="img_file3";
        String TAG_IMG4 ="img_file4";
        String TAG_IMG5 ="img_file5";

        String TAG_IMG6 ="img_file6";
        String TAG_IMG7 ="img_file7";
        String TAG_IMG8 ="img_file8";

        String TAG_IMAGE ="profile_image";
        String TAG_TEACH ="profile_teach";
        String TAG_LEARN ="profile_learn";



        String TAG_BOARDID ="board_id";

        String TAG_HEARTCOUNT ="heart_count";
        String TAG_HEARTPEOPLE ="heart_people";

        String TAG_COMMENTCOUNT ="comment_count";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                //String id = item.getString(TAG_ID);

                board_id = item.getString(TAG_BOARDID);
                Log.d("게시물 번호", board_id);

                heart_count = item.getString(TAG_HEARTCOUNT);
                Log.d("하트 개수", heart_count);

                heart_people = item.getString(TAG_HEARTPEOPLE);
                Log.d("하트 누른 사람", heart_people);

                comment_count = Integer.parseInt(item.getString(TAG_COMMENTCOUNT));
                Log.d("댓글 개수", String.valueOf(comment_count));


                profile_image = item.getString(TAG_IMAGE);
                Log.d("프로필 이미지", profile_image);

                profile_teach = item.getString(TAG_TEACH);
                Log.d("프로필 가르칠 언어", profile_teach);

                profile_learn = item.getString(TAG_LEARN);
                Log.d("프로필 학습 언어", profile_learn);



                writer = item.getString(TAG_WRITER);
                Log.d("제이슨", writer);

                writeremail = item.getString(TAG_EMAIL);
                Log.d("제이슨", writeremail);


                wdate = item.getString(TAG_WDATE);
                Log.d("제이슨", wdate);
                content = item.getString(TAG_CONTENT);
                Log.d("제이슨", content);

                img0 = item.getString(TAG_IMG0);
                Log.d("제이슨0", img0);
                img1 = item.getString(TAG_IMG1);
                Log.d("제이슨1", img1);
                img2 = item.getString(TAG_IMG2);
                Log.d("제이슨2", img2);


                img3 = item.getString(TAG_IMG3);
                Log.d("제이슨3", img3);
                img4 = item.getString(TAG_IMG4);
                Log.d("제이슨4", img4);
                img5 = item.getString(TAG_IMG5);
                Log.d("제이슨5", img5);

                img6 = item.getString(TAG_IMG6);
                Log.d("제이슨6", img6);
                img7 = item.getString(TAG_IMG7);
                Log.d("제이슨7", img7);
                img8 = item.getString(TAG_IMG8);
                Log.d("제이슨8", img8);





                Glide.with(this)
                        .load(profile_image)
                        //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.gray)
                        //.skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(board_detail_pfimage);
                Log.d("보드 프로필이미지", profile_image);


                board_detail_name.setText(writer);
                Log.d("보드 네임", writer);


                board_detail_teach.setText(profile_teach);
                Log.d("보드 가르칠 언어", profile_teach);

                board_detail_learn.setText(profile_learn);
                Log.d("보드 학습언어", profile_learn);


                board_detail_time.setText(wdate);
                Log.d("보드 날짜", wdate);

                board_detail_text.setText(content);
                Log.d("보드 내용", content);


                board_detail_count.setText(heart_count);

                board_detail_comment_count.setText(String.valueOf(comment_count));

                if(heart_people.contains(LoginEmailHolder)){
                    Glide.with(this)
                            .load(R.drawable.redheart)
                            .into(board_detail_heart);
                    Heart_button=1;
                    //heart_button[0] =1;
                    //Log.d("하트버튼 1로", String.valueOf(heart_button[0]));
                }else{
                    Glide.with(this)
                            .load(R.drawable.heart)
                            .into(board_detail_heart);
                    Heart_button=0;
                    //heart_button[0] =0;
                    //Log.d("하트버튼 0로", String.valueOf(heart_button[0]));
                }




                if(img0.equals("null")){
                    board_detail_image0.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지0", bList.get(position).getimg0());
                }else{
                    board_detail_image0.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img0)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image0);

                    //Log.d("v 보드 이미지0", bList.get(position).getimg0());
                }

                //1
                if(img1.equals("null")){
                    board_detail_image1.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지1", bList.get(position).getimg1());
                }else{
                    board_detail_image1.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img1)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image1);
                    //Log.d("v 보드 이미지1", bList.get(position).getimg1());
                }

                //2
                if(img2.equals("null")){
                    board_detail_image2.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지2", bList.get(position).getimg2());
                }else{
                    board_detail_image2.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img2)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image2);
                    //Log.d("v 보드 이미지2", bList.get(position).getimg2());
                }

                //3
                if(img3.equals("null")){
                    board_detail_image3.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지3", bList.get(position).getimg3());
                }else{
                    board_detail_image3.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img3)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image3);
                    //Log.d("v 보드 이미지3", bList.get(position).getimg3());
                }

                // 4
                if(img4.equals("null")){
                    board_detail_image4.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지4", bList.get(position).getimg4());
                }else{
                    board_detail_image4.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img4)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image4);
                    //Log.d("v 보드 이미지4", bList.get(position).getimg4());
                }

                //5
                if(img5.equals("null")){
                    board_detail_image5.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지5", bList.get(position).getimg5());
                }else{
                    board_detail_image5.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img5)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image5);
                    //Log.d("v 보드 이미지5", bList.get(position).getimg5());
                }

                //6
                if(img6.equals("null")){
                    board_detail_image6.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지6", bList.get(position).getimg6());
                }else{
                    board_detail_image6.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img6)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image6);
                    //Log.d("v 보드 이미지6", bList.get(position).getimg6());
                }


                //7
                if(img7.equals("null")){
                    board_detail_image7.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지7", bList.get(position).getimg7());
                }else{
                    board_detail_image7.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img7)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image7);
                    //Log.d("v 보드 이미지7", bList.get(position).getimg7());
                }

                //8
                if(img8.equals("null")){
                    board_detail_image8.setVisibility(View.GONE);
                    //Log.d("g 보드 이미지8", bList.get(position).getimg8());
                }else{
                    board_detail_image8.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(img8)
                            //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.gray)
                            //.skipMemoryCache(true)
                            .thumbnail(0.1f)
                            .fitCenter()
                            .into(board_detail_image8);
                    //Log.d("v 보드 이미지8", bList.get(position).getimg8());
                }







                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }



    public void Delete_comment(){

        comment_count=comment_count-1;
        board_detail_comment_count.setText(String.valueOf(comment_count));
    }


    //게시물 삭제 함수
    public void DeleteFunction(final String board_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(Board_detailActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("삭제 성공")) {

                    Toast.makeText(Board_detailActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();

                    //삭제 후에 액티비티 전환
                    finish();

                } else {

                    Toast.makeText(Board_detailActivity.this, "삭제 실패", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_delete.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id);
    }


    //게시물 좋아요 함수
    public void UserLikeFunction(final String board_id, final String liker) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 누르셨습니다' 토스트
                Toast.makeText(Board_detailActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                hashMap.put("liker", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_like_plus.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id, liker);
    }


    //게시물 좋아요 취소 함수
    public void UserHateFunction(final String board_id, final String liker) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //'좋아요를 취소하셨습니다' 토스트
                Toast.makeText(Board_detailActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();




            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("board_id", params[0]);

                hashMap.put("liker", params[1]);

                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_like_minus.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(board_id, liker);
    }


    //자바용 그릇
    private class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }


    class TranslateTask extends AsyncTask<String,Void,String> {

        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.

        // 한국어(ko)-영어(en), 한국어(ko)-일본어(ja), 한국어(ko)-중국어 간체(zh-CN), 한국어(ko)-중국어 번체(zh-TW),
        // 중국어 간체(zh-CN) - 일본어(ja), 중국어 번체(zh-TW) - 일본어(ja), 영어(en)-일본어(ja), 영어(en)-중국어 간체(zh-CN), 영어(en)-중국어 번체(zh-TW)


        @Override
        protected String doInBackground(String... strings) {

            String sourceText = strings[0];


            try {
                //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                return response.toString();

            } catch (Exception e) {
                System.out.println(e);
                return null;
            }


        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //최종 결과 처리부
            //Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.



        }



    }



    //언어감지 먼저
    private class NaverDetectionTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            //APIExamTTS.main(mTextString);

            APIExamDetectLangs.main(mTextString);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //언어를 감지한 후 speaker 설정완료했으면 TTS로 넘어가기
            //AsyncTask 실행
            mNaverTTSTask = new NaverTTSTask();
            mNaverTTSTask.execute(mTextString);

            //tv_title.setText(result);

            //방금 받은 파일명의 mp3가 있으면 플레이 시키자. 맞나 여기서 하는거?
            //아닌가 파일을 만들고 바로 실행되게 해야 하나? AsyncTask 백그라운드 작업중에...?

        }
    }


    private class NaverTTSTask extends AsyncTask<String[], Void, String> {

        @Override
        protected String doInBackground(String[]... strings) {
            //여기서 서버에 요청
            APIExamTTS.main(mTextString);

            //APIExamDetectLangs.main(mTextString);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            speaker="";

            //tv_title.setText(result);

            //방금 받은 파일명의 mp3가 있으면 플레이 시키자. 맞나 여기서 하는거?
            //아닌가 파일을 만들고 바로 실행되게 해야 하나? AsyncTask 백그라운드 작업중에...?

        }
    }




    // 텍스트 읽기 API
    public static class APIExamTTS {

        private static String TAG = "APIExamTTS";



        public static void main(String[] args) {
            String clientId = "m651g3vgcv";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "pZIxomSQuCaOR41OcDYAg2jKxZZksv7sFo4z0RZu";//애플리케이션 클라이언트 시크릿값";
            try {
                String text = URLEncoder.encode(args[0], "UTF-8"); // 13자
                String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                // post request
                String postParams = "speaker="+speaker+"&speed=0&text=" + text;
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());///여기서 에러 난다?
                Log.d(TAG, String.valueOf(wr));
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    InputStream is = con.getInputStream();
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    //폴더를 만들어 줘야 겠다. 없으면 새로 생성하도록 해야 한다. 일단 Naver폴더에 저장하도록 하자.
                    File dir = new File(Environment.getExternalStorageDirectory()+"/", "Naver");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    // 랜덤한 이름으로 mp3 파일 생성
                    //String tempname = Long.valueOf(new Date().getTime()).toString();
                    String tempname = "naverttstemp"; //하나의 파일명으로 덮어쓰기 하자.
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Naver/" + tempname + ".mp3");
                    f.createNewFile();
                    OutputStream outputStream = new FileOutputStream(f);
                    while ((read =is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    is.close();

                    //여기서 바로 재생하도록 하자. mp3파일 재생 어떻게 하지? 구글링!
                    String Path_to_file = Environment.getExternalStorageDirectory()+ File.separator+"Naver/"+tempname+".mp3";
                    MediaPlayer audioPlay = new MediaPlayer();
                    audioPlay.setDataSource(Path_to_file);
                    audioPlay.prepare();//이걸 해줘야 하는군. 없으면 에러난다.
                    audioPlay.start();
                    //재생하고 나서 파일을 지워줘야 하나? 이거참 고민이네... if문으로 분기 시켜야 하나?
                    //아니면 유니크한 파일로 만들지 말고 하나의 파일명으로 저장하게 할수도 있을듯...



                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    // 언어감지
    public static class APIExamDetectLangs {

        private static String TAG = "APIExamDetectLangs";

        public static void main(String[] args) {
            String clientId = "UJmZrs4vAjLiLJbPvbXP";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "9kyyMA9ZJq";//애플리케이션 클라이언트 시크릿값";
            try {
                String query = URLEncoder.encode(args[0], "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/detectLangs";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "query=" + query;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                Log.d(TAG, String.valueOf(wr));
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());

                //Json 파싱해서 언어만 가져온 후 speaker 대입하기
                try
                {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String language = jsonObject.getString("langCode");


                    if(language.equals("ko")){
                        //tv_title.setText("Korean");
                        speaker="mijin";
                    }
                    if(language.equals("en")){
                        //tv_title.setText("English");
                        speaker="clara";
                    }
                    if(language.equals("ja")){
                        //tv_title.setText("Japan");
                        speaker="yuri";
                    }
                    if(language.equals("zh-CN")){
                        //tv_title.setText("Chinese");
                        speaker="meimei";
                    }



                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }




            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }



    //게시물 텍스트 업로드 함수
    public void TextFunction(final String writer_name, final String writer_email, final String content, final String board_id) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //progressDialog = ProgressDialog.show(Board_detailActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                //progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("업로드 성공")) {

                    //finish();

                } else {

                    Toast.makeText(Board_detailActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> hashMap = new HashMap<>();


                hashMap.put("writer_name", params[0]);
                hashMap.put("writer_email", params[1]);
                hashMap.put("content", params[2]);
                hashMap.put("board_id", params[3]);


                finalResult = httpParse.postRequest(hashMap, "http://54.180.122.247/global_communication/board_comment_write.php");

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(writer_name,writer_email,content,board_id);
    }



    private class GetComment extends AsyncTask<String, Void, String> {

        //ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                // mTextViewResult.setText(errorString);
            }
            else {

                cJsonString = result;
                showComment();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            //String serverURL = params[0];
            //String postParameters = params[1];

            String searchKeyword1 = params[0];
            //String searchKeyword2 = params[1];

            String serverURL = "http://54.180.122.247/global_communication/comment_list.php";
            String postParameters = "board_id=" + searchKeyword1;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetComment : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showComment(){

        String TAG_JSON="webnautes";

        //String TAG_EMAIL ="email";
        //String TAG_IMAGE ="image";
        //String TAG_NAME = "name";
        //String TAG_TEACH ="teach";
        //String TAG_LEARN ="learn";



        try {
            JSONObject jsonObject = new JSONObject(cJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);


                //String board_id = item.getString("board_id");
                String comment_id = item.getString("comment_id");
                String writer_name = item.getString("writer_name");
                String writer_email = item.getString("writer_email");
                String profile_image = item.getString("profile_image");
                String wdate = item.getString("wdate");
                String content = item.getString("content");




                CommentData commentData = new CommentData();



                commentData.setLogin_email(LoginEmailHolder);
                Log.d("어레이리스트에 로그인 이메일 추가", writer_name);


                commentData.setBoard_id(board_id);
                Log.d("어레이리스트에 board_id 추가", board_id);
                commentData.setComment_id(comment_id);
                Log.d("어레이리스트에 comment_id 추가", comment_id);
                commentData.setMember_name(writer_name);
                Log.d("어레이리스트에 name 추가", writer_name);
                commentData.setMember_email(writer_email);
                Log.d("어레이리스트에 email 추가", writer_email);
                commentData.setMember_image(profile_image);
                Log.d("어레이리스트에 image 추가", profile_image);
                commentData.setMember_time(wdate);
                Log.d("어레이리스트에 time 추가", wdate);
                commentData.setMember_content(content);
                Log.d("어레이리스트에 content 추가", content);

                cArrayList.add(commentData);
                Log.d("어레이리스트에 추가", String.valueOf(commentData));
                cAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showComment : ", e);
        }

    }











}
