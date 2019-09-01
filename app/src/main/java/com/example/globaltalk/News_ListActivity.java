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
import android.widget.ImageView;
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

public class News_ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String EmailHolder;
    private String TeachHolder;
    private String LearnHolder;

    private static String TAG = "phptest";

    private SwipeRefreshLayout swipeRefresh;

    private String sJsonString;


    private ArrayList<NewsData> nArrayList;
    private NewsAdapter nAdapter;
    private RecyclerView nRecyclerView;


    private ImageView refresh_button;

    int page_no;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news__list);

        //로그인할 때 받아온 이메일 값
        Intent intent = getIntent();


        //스트림 네임
        EmailHolder = intent.getStringExtra("EmailHolder");

        TeachHolder = intent.getStringExtra("TeachHolder");

        LearnHolder = intent.getStringExtra("LearnHolder");


        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);



        //mTextViewResult = (TextView) rootView.findViewById(R.id.textView_main_result);
        nRecyclerView = (RecyclerView) findViewById(R.id.news_main_list);
        nRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        refresh_button = findViewById(R.id.refresh_button);

        //mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        nArrayList = new ArrayList<>();

        nAdapter = new NewsAdapter(this, nArrayList);
        nRecyclerView.setAdapter(nAdapter);

        //List의 orientation이 vertical 일때 DividerItemDecoration 생성자의 두번째 인자를 1로 세팅해주면되고,
        //
        //Horizontal 인 경우 0으로 설정해주면 됩니다.
        //sRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 1));



        //button_Streaming = (Button) rootView.findViewById(R.id.button_Streaming);

        //button_play = (Button) rootView.findViewById(R.id.button_play);




        //해당 리사이클러뷰 아이템을 클릭 했을 때 스트리밍을 볼 수 있는 방으로 이동
        //자신의 이메일(채팅 시 필요), 스트리밍 방번호(채팅방 나눌 떄 필요), 스트림네임(스트리밍볼 때 필요)
        nRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, nRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                NewsData newsData = nArrayList.get(position);

                //Toast.makeText(Streaming_ListActivity.this, streamingData.getstream_name(), Toast.LENGTH_SHORT).show();

                //getActivity().finish();

                Intent intent = new Intent(News_ListActivity.this, News_watchActivity.class);

                intent.putExtra("EmailHolder",EmailHolder);

                intent.putExtra("news_thumbnail",newsData.getNews_thumbnail());

                intent.putExtra("news_title",newsData.getNews_title());

                intent.putExtra("news_content",newsData.getNews_content());

                intent.putExtra("TeachHolder",TeachHolder);

                intent.putExtra("LearnHolder",LearnHolder);

                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);


                Log.d("뉴스보기","뉴스보기 인텐트");



            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }


    //생명주기 활용 필요

    @Override
    public void onResume() {
        super.onResume();

        nArrayList.clear();
        nAdapter.notifyDataSetChanged();


        page_no=0;

        GetData task = new GetData();
        task.execute(String.valueOf(page_no),LearnHolder);



        nRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    task.execute(String.valueOf(page_no),LearnHolder);
                    Log.d("페이지 번호", String.valueOf(page_no));

                }
            }
        });








        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //리사이클러뷰 초기화후, 재정렬
                        nArrayList.clear();
                        nAdapter.notifyDataSetChanged();

                        page_no=0;

                        GetData task = new GetData();
                        task.execute(String.valueOf(page_no),LearnHolder);

                        swipeRefresh.setRefreshing(false);

                    }
                }, 2000);
            }
        });




    }



    @Override
    public void onRefresh() {
        Log.d("MainActivity_", "onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //리사이클러뷰 초기화후, 재정렬
                nArrayList.clear();
                nAdapter.notifyDataSetChanged();

                page_no=0;

                GetData task = new GetData();
                task.execute(String.valueOf(page_no),LearnHolder);

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
            String Learn_news = params[1];

            //String serverURL = params[0];
            //String postParameters = params[1];

            String serverURL = "http://54.180.122.247/global_communication/news_list.php";
            String postParameters = "page_no=" + page_no + "&Learnholder=" + Learn_news;
            //String postParameters = "teach=" + searchKeyword1 + "&learn=" + searchKeyword2;



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



                String news_thumbnail = item.getString("news_thumbnail");
                Log.d("뉴스 썸네일", news_thumbnail);
                String news_title  = item.getString("news_title");
                Log.d("뉴스 제목", news_title);
                String news_subtitle  = item.getString("news_subtitle");
                Log.d("뉴스 부제", news_subtitle);
                String news_writer  = item.getString("news_writer");
                Log.d("뉴스 작성자", news_writer);

                String news_link  = item.getString("news_link");
                Log.d("스뉴스 부제",  news_link);
                String news_content  = item.getString("news_content");
                Log.d("뉴스 작성자", news_content);



                //가져온 이메일 값을 넣어서 프로필 이미지 가져오기
                //String email = item.getString(TAG_EMAIL);


                NewsData newsData = new NewsData();

                newsData.setNews_thumbnail(news_thumbnail);
                newsData.setNews_title(news_title);
                newsData.setNews_subtitle(news_subtitle);
                newsData.setNews_writer(news_writer);
                newsData.setNews_link(news_link);
                newsData.setNews_content(news_content);


                nArrayList.add(newsData);

                nAdapter.notifyDataSetChanged();

                //bAdapter.addAll(itemList);

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }



    }



}
