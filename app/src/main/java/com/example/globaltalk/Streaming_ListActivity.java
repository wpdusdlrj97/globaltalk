package com.example.globaltalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Streaming_ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String EmailHolder;
    private String TeachHolder;
    private String LearnHolder;

    private static String TAG = "phptest";

    private SwipeRefreshLayout swipeRefresh;

    private String sJsonString;


    private ArrayList<StreamingData> sArrayList;
    private StreamingAdapter sAdapter;
    private RecyclerView sRecyclerView;


    int page_no;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming__list);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();


        //스트림 네임
        EmailHolder = intent.getStringExtra("EmailHolder");

        TeachHolder = intent.getStringExtra("TeachHolder");

        LearnHolder = intent.getStringExtra("LearnHolder");


        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);



        //mTextViewResult = (TextView) rootView.findViewById(R.id.textView_main_result);
        sRecyclerView = (RecyclerView) findViewById(R.id.streaming_main_list);
        sRecyclerView.setLayoutManager(new LinearLayoutManager(this));





        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        sArrayList = new ArrayList<>();

        sAdapter = new StreamingAdapter(this, sArrayList);
        sRecyclerView.setAdapter(sAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        //sRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));





        //button_Streaming = (Button) rootView.findViewById(R.id.button_Streaming);

        //button_play = (Button) rootView.findViewById(R.id.button_play);


    }


    //생명주기 활용 필요

    @Override
    public void onResume() {
        super.onResume();

        sArrayList.clear();
        sAdapter.notifyDataSetChanged();


        page_no=0;

        GetData task = new GetData();
        task.execute(String.valueOf(page_no));



        sRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView bRecyclerView, int dx, int dy) {
                super.onScrolled(bRecyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) bRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                int itemTotalCount = bRecyclerView.getAdapter().getItemCount() - 1;


                // 스크롤 최하단 감지 시에 반응
                if (lastVisibleItemPosition == itemTotalCount) {
                    //Toast.makeText(getContext(), "Last Position", Toast.LENGTH_SHORT).show();
                    //bAdapter.notifyDataSetChanged();

                    page_no=page_no+6;
                    GetData task = new GetData();
                    task.execute(String.valueOf(page_no));
                    Log.d("페이지 번호", String.valueOf(page_no));

                }
            }
        });




        //해당 리사이클러뷰 아이템을 클릭 했을 때 스트리밍을 볼 수 있는 방으로 이동
        //자신의 이메일(채팅 시 필요), 스트리밍 방번호(채팅방 나눌 떄 필요), 스트림네임(스트리밍볼 때 필요)
        sRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, sRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                StreamingData streamingData = sArrayList.get(position);
                Toast.makeText(Streaming_ListActivity.this, streamingData.getstream_name(), Toast.LENGTH_SHORT).show();

                //getActivity().finish();

                Intent intent = new Intent(Streaming_ListActivity.this, Player_Activity.class);

                intent.putExtra("EmailHolder",EmailHolder);

                intent.putExtra("Stream_name",streamingData.getstream_name());
                Log.d("스트리밍 스트림네임 받기", streamingData.getstream_name());
                intent.putExtra("streaming_no",streamingData.getstreaming_no());
                Log.d("스트리밍 방번호 받기", streamingData.getstreaming_no());

                intent.putExtra("TeachHolder",TeachHolder);

                intent.putExtra("LearnHolder",LearnHolder);




                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


    }



    @Override
    public void onRefresh() {
        Log.d("MainActivity_", "onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //리사이클러뷰 초기화후, 재정렬
                sArrayList.clear();
                sAdapter.notifyDataSetChanged();

                page_no=0;

                GetData task = new GetData();
                task.execute(String.valueOf(page_no));

                swipeRefresh.setRefreshing(false);

            }
        }, 2000);
    }







    // RecyclerView 클릭 이벤트
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }




    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog = ProgressDialog.show(getActivity(), "데이터 로드 중", null, true, true);
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

                sJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            //String serverURL = params[0];
            //String postParameters = params[1];

            String page_no = params[0];
            //String page_size = params[1];

            //String serverURL = params[0];
            //String postParameters = params[1];

            String serverURL = "http://54.180.122.247/global_communication/streaming_list.php";
            String postParameters = "page_no=" + page_no;


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

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="webnautes";


        try {
            JSONObject jsonObject = new JSONObject(sJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);



                String stream_name = item.getString("stream_name");
                Log.d("스트림리스트 네임", stream_name);
                String room_name  = item.getString("room_name");
                Log.d("스트림리스트 방제", room_name);
                String streaming_no  = item.getString("streaming_no");
                Log.d("스트림리스트 방번호", streaming_no);



                //가져온 이메일 값을 넣어서 프로필 이미지 가져오기
                //String email = item.getString(TAG_EMAIL);


                StreamingData streamingData = new StreamingData();

                streamingData.setstream_name(stream_name);

                streamingData.setroom_name(room_name);
                streamingData.setstreaming_no(streaming_no);


                sArrayList.add(streamingData);

                sAdapter.notifyDataSetChanged();

                //bAdapter.addAll(itemList);

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }



    }



}
