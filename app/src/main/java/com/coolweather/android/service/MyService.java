package com.coolweather.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private DownLoadBinder mBinder=new DownLoadBinder();
    class  DownLoadBinder extends Binder{
        public void startDownLoad(){
            Log.d("MyService:","startDownload executed!");
        }
        public int getProgress(){
            Log.d("MyService:","getProgress executed!");
            return 0;
        }
        public IBinder onBind(Intent intent){
            return mBinder;
        }
    }
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate(){
        super.onCreate();
        Log.d("MyService ","onCreate executed!");
    }

    @Override


    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("MyService ","onStrartCommand executed!");
        return  super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        Log.d("MyService ","onDestroy executed!");
        super.onDestroy();
    }
}
