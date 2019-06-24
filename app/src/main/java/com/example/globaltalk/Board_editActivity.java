package com.example.globaltalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    ImageView board_edit_image;
    ImageView board_edit_upload;

    TextView edit_maximage;

    List<Uri> mSelected = new ArrayList<>();

    List<Uri> photos = new ArrayList<>();

    List<Uri> totalList = new ArrayList<Uri>();

    Bitmap bitmap;
    Bitmap bitmap9;

    String num;

    int matisse_size;

    String totalListarray;



    public static final int PICKER_REQUEST_CODE = 1;

    private UriAdapter mAdapter;



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
        board_edit_text = findViewById(R.id.board_edit_text);

        //이미지 수정 및 추가
        edit_maximage = findViewById(R.id.edit_maximage);

        // 이미지 선택 버튼
        board_edit_image = (ImageView)findViewById(R.id.board_edit_image);

        // 수정 완료 버튼
        board_edit_upload = (ImageView)findViewById(R.id.board_edit_upload);




        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.edit_recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(mAdapter = new UriAdapter(this));


        //글이 있을 경우
        board_edit_text.setText(ContentHolder);


        if(img0Holder.equals("null")){

        }else if(img1Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));

        }else if(img2Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));

        }else if(img3Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));

        }else if(img4Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));

        }else if(img5Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));
            photos.add(Uri.parse(img4Holder));

        }else if(img6Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));
            photos.add(Uri.parse(img4Holder));
            photos.add(Uri.parse(img5Holder));

        }else if(img7Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));
            photos.add(Uri.parse(img4Holder));
            photos.add(Uri.parse(img5Holder));
            photos.add(Uri.parse(img6Holder));

        }else if(img8Holder.equals("null")){
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));
            photos.add(Uri.parse(img4Holder));
            photos.add(Uri.parse(img5Holder));
            photos.add(Uri.parse(img6Holder));
            photos.add(Uri.parse(img7Holder));

        }else{
            photos.add(Uri.parse(img0Holder));
            photos.add(Uri.parse(img1Holder));
            photos.add(Uri.parse(img2Holder));
            photos.add(Uri.parse(img3Holder));
            photos.add(Uri.parse(img4Holder));
            photos.add(Uri.parse(img5Holder));
            photos.add(Uri.parse(img6Holder));
            photos.add(Uri.parse(img7Holder));
            photos.add(Uri.parse(img8Holder));

        }

        //mAdapter.setData(photos);
        Log.d("사진들", String.valueOf(photos));

        //배열 합치기
        totalList.addAll(photos);



    }

    @Override
    public void onResume() {
        super.onResume();




        //사진 추가 선택
        board_edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] PERMISSIONS = {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if (hasPermissions(Board_editActivity.this, PERMISSIONS)) {
                    ShowPicker();
                    //mAdapter.setData(null);
                } else {
                    ActivityCompat.requestPermissions(Board_editActivity.this, PERMISSIONS, PICKER_REQUEST_CODE);
                }

            }
        });

        board_edit_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UploadFunction();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });




        mAdapter.setData(totalList);
        Log.d("마티세 전",totalList.toString());
        //Log.d("토탈배열 크기", String.valueOf(totalList.size()));



    }

    public void ShowPicker() {
        Matisse.from(Board_editActivity.this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(matisse_size)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(PICKER_REQUEST_CODE);
    }


    // 액티비티에 URI 모음을 가져온다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

            mSelected = Matisse.obtainResult(data);
            Log.d("마티세 선택 ", mSelected.toString());

            totalList.addAll(mSelected);
            Log.d("마티세 토탈 ", totalList.toString());


            mAdapter.setData(totalList);


        }
    }

    //액티비티로 가져온 mSelect를  배열 -> [,] 제거한뒤 , 단위로 분할, 비트맵
    public void UploadFunction() throws ExecutionException, InterruptedException {

        //배열로 URI 모음 가져오기
        totalListarray = totalList.toString();
        Log.d("마티세 선택1 ", totalListarray);

        //앞에 [ 제거
        totalListarray = totalListarray.substring(1);
        Log.d("마티세 선택2 ", totalListarray);

        //뒤에 ] 제거
        totalListarray = totalListarray.replaceAll("]", "");
        Log.d("마티세 선택3 ", totalListarray);

        // , 단위로 나누기
        String[] array = totalListarray.split(",");


        for (int i = 0; i < array.length; i++) {
            //System.out.println(array[i]);

            array[i] = array[i].trim();
            Log.d("마티세  URI", array[i]);



            if(array[i].contains("http")){
                bitmap=new ConvertUrlToBitmap().execute(array[i]).get();
                //여기에 AsyncTask의 결과값 받아오기
                Log.d("업로드용 비트맵 포지션", String.valueOf(array[i]));
                Log.d("업로드용 비트맵" + i, bitmap.toString());
            }else{
                try {
                    bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(array[i]));
                    Log.d("업로드용 비트맵 포지션", String.valueOf(array[i]));
                    Log.d("업로드용 비트맵" + i, bitmap.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            // 가져온 array[i]를 URI로 파싱 - > 비트맵으로 만들기
            try {
                bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(array[i]));
                Log.d("마티세 비트맵" + i, bitmap.toString());

                //num=String.valueOf(i);
                //uploadimg=getStringImage(bitmap);

                //ImageUploadToServerFunction(num, uploadimg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */



        }
    }


    /**
     * Helper method that verifies whether the permissions of a given array are granted or not.
     *
     * @param context
     * @param permissions
     * @return {Boolean}
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback that handles the status of the permissions request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICKER_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                            Board_editActivity.this,
                            "Permission granted! Please click on pick a file once again.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            Board_editActivity.this,
                            "Permission denied to read your External storage :(",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return;
            }
        }
    }


    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //Log.d("업로드용 비트맵", bitmap.toString());
                return bitmap;
            } catch (Exception e) {
                Log.e("오류 태그", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            //Log.d("에이싱크 비트맵", String.valueOf(bitmap));

        }
    }

    //URI 모음을 어댑터로 전달해 하나하나 Glide로 넣어준다
    private class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private final Activity context;
        private List<Uri> mUris;
        //private List<String> mPaths;


        public UriAdapter(Activity context) {
            this.context = context;
            //this.mList = list;
        }


        void setData(List<Uri> uris) {
            mUris = uris;
            //mPaths = paths;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_uri_item, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {


            // 가져온 URI Glide를 통해 ImageVIew에 삽입
            Glide.with(context)
                    .load(mUris.get(position).toString())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .fitCenter()
                    .into(holder.mUri);

            Log.d("사진 URI", mUris.get(position).toString());
            Log.d("배열 크기", String.valueOf(mUris.size()));
            matisse_size=9-totalList.size();

            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 배열에서 삭제 및 데이터 갱신
                    //bList.remove(viewholder.getAdapterPosition());
                    //notifyItemRemoved(viewholder.getAdapterPosition());
                    //notifyItemRangeChanged(viewholder.getAdapterPosition(), bList.size());
                    mUris.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), mUris.size());

                    //totallist에서 배열 삭제안해줘도 개수 알아서 삭제?

                    Log.d("배열 삭제 시",mUris.toString());
                    Log.d("배열 크기", String.valueOf(mUris.size()));
                    matisse_size=9-totalList.size();
                }
            });

            // URL to Bitmap

            //holder.mPath.setText(bitmap9.toString());
            //Log.d("사진 비트맵", bitmap.toString());





        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        class UriViewHolder extends RecyclerView.ViewHolder {

            private ImageView mUri;
            private Button mButton;


            UriViewHolder(View contentView) {
                super(contentView);
                mUri = (ImageView) contentView.findViewById(R.id.edit_uri);
                mButton = (Button) contentView.findViewById(R.id.delete_uri);
                //mPath = (TextView) contentView.findViewById(R.id.path);
            }
        }


    }


}
