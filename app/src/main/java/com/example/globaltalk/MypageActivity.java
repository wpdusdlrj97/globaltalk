package com.example.globaltalk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MypageActivity extends AppCompatActivity {

    private static String TAG = "phpquerytest";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL ="email";
    private static final String TAG_IMAGE ="image";
    private static final String TAG_IMAGE1 ="image1";
    private static final String TAG_IMAGE2 ="image2";
    private static final String TAG_IMAGE3 ="image3";

    private static final String TAG_CONTENT = "content";

    private static final String TAG_AGE ="age";
    private static final String TAG_SEX ="sex";
    private static final String TAG_TEACH ="teach";
    private static final String TAG_LEARN ="learn";

    String Profile_image = "Profile_image" ;
    String Profile_email = "Profile_email" ;
    String Profile_password = "Profile_password" ;
    String Profile_name = "Profile_name" ;
    String Profile_age = "Profile_age" ;
    String Profile_sex = "Profile_sex" ;
    String Profile_teach = "Profile_teach" ;
    String Profile_learn = "Profile_learn" ;




    private ImageView pf_image;

    //constant
    final int PICTURE_REQUEST_CODE = 100;

    private ImageView img1;
    private ImageView img2;
    private ImageView img3;

    Bitmap bitmap1;
    Bitmap bitmap2;
    Bitmap bitmap3;

    String Profile_image1 = "Profile_image1" ;
    String Profile_image2 = "Profile_image2" ;
    String Profile_image3 = "Profile_image3" ;



    //로그인 시 받아온 이메일 값
    String EmailHolder;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Boolean isCamera = false;
    private File tempFile;
    boolean check = true;
    Bitmap bitmap;
    ProgressDialog progressDialog ;



    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListViewList;
    EditText mEditTextSearchKeyword1, mEditTextSearchKeyword2;

    String mJsonString;

    EditText mypage_intro;

    ImageView mypage_back;
    Button pf_intro_button;

    String HttpURL = "http://54.180.122.247/global_communication/mypage_intro.php";
    HttpParse httpParse = new HttpParse();
    String finalResult;
    String IntroHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        //mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        //mListViewList = (ListView) findViewById(R.id.listView_main_list);
        //mEditTextSearchKeyword1 = (EditText) findViewById(R.id.editText_main_searchKeyword1);
        //mEditTextSearchKeyword2 = (EditText) findViewById(R.id.editText_main_searchKeyword2);

        pf_image = (ImageView)findViewById(R.id.pf_image);

        mypage_intro = (EditText) findViewById(R.id.mypage_intro);

        img1 = (ImageView)findViewById(R.id.img1);
        img2 = (ImageView)findViewById(R.id.img2);
        img3 = (ImageView)findViewById(R.id.img3);

        mypage_back = (ImageView)findViewById(R.id.mypage_back);

        pf_intro_button = (Button)findViewById(R.id. pf_intro_button);



        //Tab5로부터 받아온 값
        Intent intent5 = getIntent();
        EmailHolder = intent5.getStringExtra(Tab5.UserEmail);




        tedPermission();


        GetData task = new GetData();
        //task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
        task.execute("http://54.180.122.247/global_communication/mypage.php", "");


        mypage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        //프로필 사진 누를 시
        pf_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                show();

            }
        });


        //자기소개
        pf_intro_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntroHolder = mypage_intro.getText().toString();

                if (TextUtils.isEmpty(IntroHolder)) {
                    Toast.makeText(MypageActivity.this, "내용을 입력해주세요", Toast.LENGTH_LONG).show();
                } else {
                    IntroFunction(EmailHolder,IntroHolder);
                }



            }
        });




        // 대표사진 고르기
        Button pf_images_button = (Button)findViewById(R.id.pf_images_button);
        pf_images_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICTURE_REQUEST_CODE);
            }
        });






    }


    void show()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("앨범에서 선택");
        ListItems.add("카메라");
        ListItems.add("기본이미지");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {

                //앨범에서 선택
                if(pos==0){
                    goToAlbum();

                    //Toast.makeText(getActivity(), "앨범에서 선택", Toast.LENGTH_SHORT).show();
                    //카메라
                }else if(pos==1){
                    takePhoto();
                    //Toast.makeText(getActivity(), "카메라", Toast.LENGTH_SHORT).show();
                }else{
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
                    pf_image.setImageBitmap(bitmap);
                    ImageUploadToServerFunction();
                }

            }
        });

        builder.show();
    }




    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }


    private void goToAlbum() {

        isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICTURE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {

                //기존 이미지 지우기
                //image1.setImageResource(0);
                //image2.setImageResource(0);
                //image3.setImageResource(0);

                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if(clipData!=null)
                {

                    for(int i = 0; i < 3; i++)
                    {
                        if(i<clipData.getItemCount()){
                            Uri urione =  clipData.getItemAt(i).getUri();
                            switch (i){
                                case 0:

                                    try {

                                        bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), urione);

                                        img1.setImageBitmap(bitmap1);
                                        Log.d("비트맵1",bitmap1.toString());


                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }

                                    break;

                                case 1:

                                    try {

                                        bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), urione);

                                        img2.setImageBitmap(bitmap2);
                                        Log.d("비트맵2",bitmap2.toString());


                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }


                                    break;

                                case 2:

                                    try {

                                        bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), urione);

                                        img3.setImageBitmap(bitmap3);
                                        Log.d("비트맵3",bitmap3.toString());


                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }

                                    break;
                            }
                        }
                    }


                    ImageUploadToServerFunction9();


                }
                else if(uri != null)
                {
                    img1.setImageURI(uri);
                }
            }
        }


        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("갤러리와 카메라", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }


        switch (requestCode) {
            case PICK_FROM_ALBUM: {

                Uri photoUri = data.getData();

                cropImage(photoUri);

                break;
            }
            case PICK_FROM_CAMERA: {

                Uri photoUri = Uri.fromFile(tempFile);

                cropImage(photoUri);

                break;
            }
            case Crop.REQUEST_CROP: {

                setImage();
            }
        }


    }



    private void setImage() {


        //이미지 회전
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d("이미지세팅", "setImage : " + tempFile.getAbsolutePath());

        Log.d("비트맵",bitmap.toString());

        pf_image.setImageBitmap(bitmap);
        ImageUploadToServerFunction();

    }

    private void takePhoto() {

        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "{package name}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private void cropImage(Uri photoUri) {

        Log.d("이미지크롭", "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }





    public void ImageUploadToServerFunction9(){

        ByteArrayOutputStream byteArrayOutputStreamObject1 ;

        byteArrayOutputStreamObject1 = new ByteArrayOutputStream();

        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject1);

        byte[] byteArrayVar1 = byteArrayOutputStreamObject1.toByteArray();

        final String ConvertImage1 = Base64.encodeToString(byteArrayVar1, Base64.DEFAULT);


        ByteArrayOutputStream byteArrayOutputStreamObject2 ;

        byteArrayOutputStreamObject2 = new ByteArrayOutputStream();

        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject2);

        byte[] byteArrayVar2 = byteArrayOutputStreamObject2.toByteArray();

        final String ConvertImage2 = Base64.encodeToString(byteArrayVar2, Base64.DEFAULT);

        ByteArrayOutputStream byteArrayOutputStreamObject3 ;

        byteArrayOutputStreamObject3 = new ByteArrayOutputStream();

        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject3);

        byte[] byteArrayVar3 = byteArrayOutputStreamObject3.toByteArray();

        final String ConvertImage3 = Base64.encodeToString(byteArrayVar3, Base64.DEFAULT);







        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MypageActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(MypageActivity.this,string1,Toast.LENGTH_LONG).show();


                // Setting image as transparent after done uploading.
                // register_image.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass9 imageProcessClass = new ImageProcessClass9();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();


                HashMapParams.put(Profile_email, EmailHolder);
                //HashMapParams.put(Profile_password, Get_password);
                //HashMapParams.put(Profile_name, Get_name);
                //HashMapParams.put(Profile_age, Get_age);
                //HashMapParams.put(Profile_sex, Get_sex);
                //HashMapParams.put(Profile_teach, Get_teach);
                //HashMapParams.put(Profile_learn, Get_learn);

                HashMapParams.put(Profile_image1, ConvertImage1);
                Log.d("이미지1",ConvertImage1);
                HashMapParams.put(Profile_image2, ConvertImage2);
                Log.d("이미지2",ConvertImage2);
                HashMapParams.put(Profile_image3, ConvertImage3);
                Log.d("이미지3",ConvertImage3);


                String FinalData = imageProcessClass.ImageHttpRequest("http://54.180.122.247/global_communication/multi_image.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass9{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }



    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);




        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MypageActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(MypageActivity.this,string1,Toast.LENGTH_LONG).show();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (string1.equalsIgnoreCase("프로필사진 수정완료")) {

                    finish();

                    //Intent intent = new Intent(MypageActivity.this,MainActivity.class);

                    //startActivity(intent);

                }


                // Setting image as transparent after done uploading.
                // register_image.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();


                HashMapParams.put(Profile_email, EmailHolder);
                //HashMapParams.put(Profile_password, Get_password);
                //HashMapParams.put(Profile_name, Get_name);
                //HashMapParams.put(Profile_age, Get_age);
                //HashMapParams.put(Profile_sex, Get_sex);
                //HashMapParams.put(Profile_teach, Get_teach);
                //HashMapParams.put(Profile_learn, Get_learn);

                HashMapParams.put(Profile_image, ConvertImage);


                String FinalData = imageProcessClass.ImageHttpRequest("http://54.180.122.247/global_communication/mypage_image.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }





    class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MypageActivity.this,
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

            String serverURL = "http://54.180.122.247/global_communication/mypage.php";
            String postParameters = "email=" + EmailHolder;


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
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String email = item.getString(TAG_EMAIL);

                String content = item.getString(TAG_CONTENT);

                String image = item.getString(TAG_IMAGE);
                String image1 = item.getString(TAG_IMAGE1);
                String image2 = item.getString(TAG_IMAGE2);
                String image3 = item.getString(TAG_IMAGE3);

                if(content.equals("null")){
                    mypage_intro.setText("");
                }else{
                    mypage_intro.setText(content);
                }





                Glide.with(this)
                        .load(image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(pf_image);


                Glide.with(this)
                        .load(image1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(img1);

                Glide.with(this)
                        .load(image2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(img2);

                Glide.with(this)
                        .load(image3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(0.1f)
                        .fitCenter()
                        .into(img3);





                //mArrayList.add(hashMap);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    //자기소개 업로드 함수
    public void IntroFunction(final String email, final String intro) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MypageActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("자기소개 업로드 성공")) {

                    finish();

                } else {

                    Toast.makeText(MypageActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> hashMap = new HashMap<>();


                hashMap.put("email", params[0]);
                hashMap.put("intro", params[1]);


                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(email,intro);
    }



}
