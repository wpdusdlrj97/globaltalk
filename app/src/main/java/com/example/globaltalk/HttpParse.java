package com.example.globaltalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juned on 3/3/2017.
 */

public class HttpParse {

    String FinalHttpData = "";
    String Result ;
    BufferedWriter bufferedWriter ;
    OutputStream outputStream ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder = new StringBuilder();
    URL url;

    //01. HttpURLConnection 란?
    //
    //- URL 내용을 읽어 오거나, URL 주소에 GET / POST 로 데이터를 전달 할 때 사용합니다.
    //
    //- 웹 페이지나 서블릿에 데이터를 전달할 수 있습니다.

    //02. InputStreamReader 란?
    //
    //
    //- 인자로, InputStream을 취해서 Reader 스트림형태로 변환합니다.
    //
    //변환시 문자열인코딩을 줄수도있습니다.

    //03. BufferedReader란?
    //
    //- 인자로 취한 Reader 스트림에 버퍼링기능을 추가한 입력스트림 클래스 이다.
    //
    //- 버퍼를 사용함으로써 파일, 네트워크와 같은 물리적인 장치에서 데이터를 사용자가 요청할 때마다 매번 읽어오는것보단,
    // 일정량사이즈로 한번에 읽어온 후 버퍼에 보관,
    // 그리고 사용자가 요구시 버퍼에서 읽어 오게됩니다. 그러므로 속도를 향상시키고, 시간의 부하를 줄일수 있게 됩니다.



    public String postRequest(HashMap<String, String> Data, String HttpUrlHolder) {

        //HttpURLConnection을 통해 web의 데이터를 가져온다.
        try {

            // HttpURLConnection은 직접 생성자를 만들 수 없어 URL를 이용하여 생성자를 만들고 .openConnection()을 통하여 연결해주도록 합니다.
            url = new URL(HttpUrlHolder);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // [1]. HttpURLConnection 설정.

            // 10초안에 응답이 오지 않으면 예외가 발생합니다.
            httpURLConnection.setReadTimeout(10000);
            // 10초안에 연결이 안되면 예외가 발생합니다.
            httpURLConnection.setConnectTimeout(10000);
            // POST방식으로 요청( 기본값은 GET )
            httpURLConnection.setRequestMethod("POST");
            // 데이터를 읽어올지 설정
            // InputStream으로 응답 헤더와 메시지를 읽어들이겠다는 옵션을 정의한다.
            httpURLConnection.setDoInput(true);
            // 데이터를 쓸 지 설정
            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션을 정의한다.
            httpURLConnection.setDoOutput(true);



            // 새로운 OutputStream에 요청할 OutputStream을 넣는다.
            outputStream = httpURLConnection.getOutputStream();
            //응답받은 메시지의 길이만큼 버퍼를 생성하여 입력하고, "UTF-8"로 디코딩해서 입력한다.
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            //요청 파라미터를 입력 (밑에 있는 FinalDataParse 함수)
            bufferedWriter.write(FinalDataParse(Data));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();



            // 연결 요청 확인 조건문
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "UTF-8"로 디코딩해서 읽어들인다.
                bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                httpURLConnection.getInputStream()
                        )
                );
                FinalHttpData = bufferedReader.readLine();
            }
            else {
                FinalHttpData = "Something Went Wrong";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalHttpData;
    }




    public String FinalDataParse(HashMap<String,String> hashMap2) throws UnsupportedEncodingException {

        for(Map.Entry<String,String> map_entry : hashMap2.entrySet()){

            stringBuilder.append("&");

            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

        }

        Result = stringBuilder.toString();

        return Result ;
    }
}
