package com.coolweather.android;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.coolweather.android.service.MyService;

import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity{
  TextView responseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /*  Button startService=this.findViewById(R.id.start_service);
      Button stopService=this.findViewById(R.id.stop_service);
      startService.setOnClickListener(this);
      stopService.setOnClickListener(this);
      //测试数据库
     //   Connector.getDatabase();
        SQLiteStudioService.instance().start(this);*/
    }

   /* @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.start_service:
                Intent startIntent=new Intent(this, MyService.class);
                startService(startIntent);
                break;
            case R.id.stop_service:
               Intent stopIntent =new Intent(this,MyService.class);
               stopService(stopIntent);
               break;
               default:
                   break;

                }
        //sendRequestWithHttpURLConnection();
      //  sendRequestWithOkHttp();

    }*/

    private void sendRequestWithOkHttp(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               try{
                   OkHttpClient client=new OkHttpClient();
                   Request request=new Request.Builder().url("https://www.baidu.com").build();
                   Response response= client.newCall(request).execute();
                   String responseData= response.body().string();
                   showResponse(responseData);
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       }).start();
    }
    private  void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpsURLConnection connection=null;
                BufferedReader reader=null;
                try{

                    URL url=new URL("https://www.jianshu.com/p/8e404d9c160f");
                    connection=(HttpsURLConnection)  url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    reader= new BufferedReader(new InputStreamReader(in));
                    StringBuffer reponse=new StringBuffer();
                    String line;
                    while((line=reader.readLine())!=null){
                        reponse.append(line);
                    }

                    showResponse(reponse.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }
    private void showResponse(final  String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });

    }

    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
    }
}
