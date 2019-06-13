package com.example.globaltalk;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//회원가입 화면
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

public class RegisterActivity extends AppCompatActivity {

    Bitmap bitmap;

    boolean check = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private Boolean isCamera = false;
    private File tempFile;


    //프로필 사진
    ImageView register_image;

    // 입력사항 -> 이메일, 비밀번호, 비밀번호 확인, 이름, 나이
    EditText register_email, register_password, register_passwordcheck, register_name, register_age ;

    //성별, 가르칠 언어, 학습 언어
    String register_sex;
    String register_teach;
    String register_learn;

    //버튼  중복체크, 회원가입하기, 취소
    Button btn_email_check, register_finish, register_cancel;


    //빈칸 체크사항
    Boolean CheckEditText;
    Boolean CheckEditImage;
    //비밀번호 일치 불일치
    Boolean CheckEditPW;

    ProgressDialog progressDialog ;

    String Get_email;
    String Get_password;
    String Get_passwordcheck;
    String Get_name;
    String Get_age;
    String Get_sex;
    String Get_teach;
    String Get_learn;


    //String ImageName = "image_name" ;
    //String ImagePath = "image_path" ;

    String Profile_image = "Profile_image" ;
    String Profile_email = "Profile_email" ;
    String Profile_password = "Profile_password" ;
    String Profile_name = "Profile_name" ;
    String Profile_age = "Profile_age" ;
    String Profile_sex = "Profile_sex" ;
    String Profile_teach = "Profile_teach" ;
    String Profile_learn = "Profile_learn" ;

    //이메일 중복체크
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String finalResult ;
    String email_check_URL = "http://54.180.122.247/global_communication/emailcheck.php";
    String email_message;
    String email_success = "등록가능한 이메일입니다";

    //중복체크 클릭 했는지 안했는지
    Boolean CheckEmailClick;
    //이메일 중복체크
    Boolean CheckEditEmail;

    String ServerUploadPath ="http://54.180.122.247/global_communication/register.php" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tedPermission();

        //사진
        register_image = (ImageView)findViewById(R.id.register_image);

        // 입력사항 -> 이메일, 비밀번호, 비밀번호 확인, 이름, 나이
        register_email = (EditText)findViewById(R.id.register_email);
        register_password = (EditText)findViewById(R.id.register_password);
        register_passwordcheck = (EditText)findViewById(R.id.register_passwordcheck);
        register_name = (EditText)findViewById(R.id.register_name);
        register_age = (EditText)findViewById(R.id.register_age);

        //성별 선택
        RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                register_sex = ((RadioButton)findViewById(checkedId)).getText().toString() ;
            }
        });



        //가르칠 언어
        Spinner spinner_teach =findViewById(R.id.spinner_teach);

        spinner_teach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                register_teach = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //배울 언어
        Spinner spinner_learn =findViewById(R.id.spinner_learn);

        spinner_learn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                register_learn = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // 버튼
        btn_email_check = (Button)findViewById(R.id.btn_email_check);

        register_finish = (Button)findViewById(R.id.register_finish);
        register_cancel = (Button)findViewById(R.id.register_cancel);




        //프로필 사진 선택
        register_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                show();
            }
        });


        // 이메일 중복체크
        btn_email_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 입력하는 것에 대해 값 지정 필요
                Get_email = register_email.getText().toString();
                Get_password = register_password.getText().toString();

                //값이 없을때 토스트 출력
                if(TextUtils.isEmpty(Get_email))
                {

                    Toast.makeText(RegisterActivity.this,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    register_email.requestFocus();

                }else{//값이 있을 떄 토스트 출력


                    //값이 이메일 형식에 맞을 때
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(Get_email).matches())
                    {

                        //이메일 중복체크
                        Email_Check_Function(Get_email, Get_password);




                    }else{ //이메일 형식이 아닐때

                        Toast.makeText(RegisterActivity.this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                        register_email.requestFocus();
                    }
                }





            }
        });


        //회원가입 완료
        register_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //빈칸 및 비밀번호 일치 불일치
                CheckEditTextIsEmptyOrNot();

                //TEXT 입력사항
                //register_email, register_password, register_passwordcheck, register_name, register_age

                Get_email = register_email.getText().toString();
                Get_password = register_password.getText().toString();
                Get_name = register_name.getText().toString();
                Get_age = register_age.getText().toString();
                Get_sex = register_sex;
                Get_teach = register_teach;
                Get_learn = register_learn;

                Get_passwordcheck = register_passwordcheck.getText().toString();

                // 프로필 사진이 등록되어 있다면
                if(CheckEditImage){

                    //프로필사진이 등록되어 있다면 빈칸을 체크한다다

                    // 빈칸이 없다면
                    if(CheckEditText){


                        //이메일 중복체크 아예 안할 시
                        if(CheckEmailClick==null) {


                            Toast.makeText(RegisterActivity.this, "이메일 중복체크를 해주세요", Toast.LENGTH_SHORT).show();



                        }else{ // 중복체크 버튼은 눌렀다

                            if(CheckEmailClick){//등록가능한 이메일일 경우

                                // 비밀번호가 일치한다면
                                if (CheckEditPW) {


                                    //비밀번호가 형식에 맞을 경우
                                    if (Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,15}$", Get_password)) {


                                        // 배우는 언어와 가르치는 언어가 같을 때 가입 불가
                                        if(Get_teach.equals(Get_learn)) {

                                            Toast.makeText(RegisterActivity.this, "가르칠 언어와 학습언어는 다르게 설정해주세요", Toast.LENGTH_SHORT).show();
                                            Log.d("가르칠 언어",Get_teach);
                                            Log.d("배울 언어",Get_learn);

                                        }else{ // 다를 경우

                                            // 모든 양식이 맞을 경우 회원가입하기
                                            ImageUploadToServerFunction();

                                        }


                                    } else {
                                        Toast.makeText(RegisterActivity.this, "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show();
                                    }


                                } else {

                                    // 불일치하면 토스트 띄워주기
                                    Toast.makeText(RegisterActivity.this, "비밀번호가 불일치합니다", Toast.LENGTH_LONG).show();

                                    register_password.requestFocus();

                                }


                            }else{//등록불가능한 이메일일 경우
                                Toast.makeText(RegisterActivity.this, "이메일 중복체크를 확인해주세요", Toast.LENGTH_SHORT).show();

                                register_email.requestFocus();
                            }

                        }












                    }
                    else {

                        // 빈칸이 있다면 토스트 띄워주기
                        Toast.makeText(RegisterActivity.this, "입력사항을 모두 입력해주세요", Toast.LENGTH_LONG).show();

                    }
                }else{

                    // 프로필사진이 없다면 토스트 띄워주기
                    Toast.makeText(RegisterActivity.this, "프로필 사진을 등록해주세요", Toast.LENGTH_LONG).show();
                }



            }
        });

        //취소 버튼
        register_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);

                startActivityForResult(intent, 1);
            }
        });



    }


    // 빈칸 확인 함수
    public void CheckEditTextIsEmptyOrNot(){

        //프로필사진이 없을 시
        if(bitmap == null) {
            CheckEditImage = false;
        } else {

            CheckEditImage = true;
        }

        //모든 칸이 채워지지 않았을 때
        if(TextUtils.isEmpty(Get_email) || TextUtils.isEmpty(Get_password) || TextUtils.isEmpty(Get_passwordcheck) || TextUtils.isEmpty(Get_name) || TextUtils.isEmpty(Get_age) || TextUtils.isEmpty(Get_sex) || TextUtils.isEmpty(Get_teach) || TextUtils.isEmpty(Get_learn))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;

            // 비밀번호가 일치했을 때
            if(Get_password.equals(Get_passwordcheck) ) {

                CheckEditPW = true;

            }else{

                CheckEditPW = false;

            }
        }




    }


    void show()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("앨범에서 선택");
        ListItems.add("카메라");
        ListItems.add("기본이미지");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

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
                    register_image.setImageBitmap(bitmap);
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

        register_image.setImageBitmap(bitmap);

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

                progressDialog = ProgressDialog.show(RegisterActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(RegisterActivity.this,string1,Toast.LENGTH_LONG).show();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (string1.equalsIgnoreCase("회원가입 완료")) {

                    finish();

                    Intent intent = new Intent(RegisterActivity.this, UserLoginActivity.class);

                    startActivity(intent);

                }


                // Setting image as transparent after done uploading.
                // register_image.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();


                HashMapParams.put(Profile_email, Get_email);
                Log.d("이메일",Get_email);
                HashMapParams.put(Profile_password, Get_password);
                HashMapParams.put(Profile_name, Get_name);
                HashMapParams.put(Profile_age, Get_age);
                HashMapParams.put(Profile_sex, Get_sex);
                HashMapParams.put(Profile_teach, Get_teach);
                HashMapParams.put(Profile_learn, Get_learn);

                HashMapParams.put(Profile_image, ConvertImage);


                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }



    //이메일 중복 체크
    public void Email_Check_Function(final String email, final String password){

        class Email_Check_FunctionClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterActivity.this,"Loading Data",null,true,true);
            }



            @Override
            protected void onPostExecute(String httpResponseMsg) {

                //PHP 파일에 있는 예외처리
                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(RegisterActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();

                // 리턴값으로 true를 한다  중복체크 안할시 회원가입에서 토스트 띄우기
                Log.d("응답",httpResponseMsg);

                email_message = httpResponseMsg;

                // 이메일이 겹치지 않을 때
                if(email_message.equals(email_success) ) {

                    CheckEditEmail = true;

                    CheckEmailClick = true;

                }else{

                    CheckEditEmail = false;

                    CheckEmailClick = false;

                }


            }

            @Override
            protected String doInBackground(String... params) {
                //1. HashMap이란?  : HashMap은 Map을 구현한다. Key와 value를 묶어 하나의 entry로 저장한다는 특징을 갖는다.
                // 그리고 hashing을 사용하기 때문에 많은양의 데이터를 검색하는데 뛰어난 성능을 보인다.

                hashMap.put("email",params[0]);
                hashMap.put("password",params[1]);

                finalResult = httpParse.postRequest(hashMap, email_check_URL);

                return finalResult;
            }



        }

        Email_Check_FunctionClass email_Check_FunctionClass = new Email_Check_FunctionClass();

        email_Check_FunctionClass.execute(email, password);
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
}
