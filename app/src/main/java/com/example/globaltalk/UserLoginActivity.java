package com.example.globaltalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class UserLoginActivity extends AppCompatActivity {

    EditText login_email,login_password;
    Button Login_button, Register_button;

    String PasswordHolder, EmailHolder;

    String finalResult;

    String HttpURL = "http://54.180.122.247/global_communication/login.php";
    HttpParse httpParse = new HttpParse();

    Boolean CheckEditText;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();

    public static final String UserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        Login_button = (Button) findViewById(R.id.Login_button);
        Register_button = (Button) findViewById(R.id.Register_button);

        //로그인 버튼  -> 빈칸이 모두 채워져있다면 UserLoginFunction 실행
        Login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckEditTextIsEmptyOrNot();

                if (CheckEditText) {

                    UserLoginFunction(EmailHolder, PasswordHolder);

                } else {

                    Toast.makeText(UserLoginActivity.this, "빈칸을 모두 채워주세요", Toast.LENGTH_LONG).show();

                }

            }
        });

        //회원가입 버튼 -> 회원가입 화면으로 이동
        Register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);

                startActivityForResult(intent, 1);


            }
        });


    }

    //빈칸확인 함수
    public void CheckEditTextIsEmptyOrNot() {

        EmailHolder = login_email.getText().toString();
        PasswordHolder = login_password.getText().toString();

        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            CheckEditText = false;
        } else {

            CheckEditText = true;
        }
    }

    //로그인 함수
    public void UserLoginFunction(final String email, final String password) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UserLoginActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                //php로부터 로그인 성공이라는 메시지가 오면
                if (httpResponseMsg.equalsIgnoreCase("로그인 성공")) {

                    finish();

                    Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);

                    //로그인 함수 시 괄호 안에 넣었던 변수 email
                    intent.putExtra(UserEmail, email);
                    startActivity(intent);


                } else {

                    Toast.makeText(UserLoginActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("email", params[0]);

                hashMap.put("password", params[1]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(email, password);
    }
}