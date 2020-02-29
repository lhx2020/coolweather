package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import  okhttp3.Callback;

public class HttpUtil {
    public static void sendHttpRequest(String address, okhttp3.Callback callback){

      OkHttpClient client =new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
