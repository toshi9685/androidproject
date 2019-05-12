package com.example.dummylocation;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {
    // Fused Location Provider API.
    private FusedLocationProviderClient fusedLocationClient;
    //タイマー
    private Timer timer;
    //coordinateデータベース
    private String url = "http://boshigure96.php.xdomain.jp/Map/Android_Connector.php?action=getdata&table_name=coordinate";

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //gpsアクセスが許可されているか
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
        }
        //MockLocationをtrueに
        fusedLocationClient.setMockMode(true);


        //タイマー処理スタート
        timer = new Timer();
        TimerTask timerTask = new LocationTimerTask(this);
        timer.scheduleAtFixedRate(timerTask, 0, 5000);
        return START_NOT_STICKY;
    }
    //timer処理
    public void InvalidateScreen(){
        //通信(データベースから座標を持ってくる)
        rereadVolley("?action=getdata");
    }
    //サーバー通信
    private void rereadVolley(String action) {
        //queue
        final RequestQueue getQueue= Volley.newRequestQueue(this);
        //Volleyによる通信開始　（GETかPOST、サーバーのURL、受信メゾット、エラーメゾット）
        final JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, url,
                // 通信成功
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //json
                        GetJasonData(response);
                        getQueue.stop();
                    }
                },
                // 通信失敗
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getQueue.stop();
                    }
                }
        );
        getQueue.add(mRequest);
    }
    //jsonデータを解析する.
    private void GetJasonData(JSONObject response) {
        try {
            //Jsonデータを取得
            JSONArray count=  response.getJSONArray("Map");
            //Jsonデータからリストを作成
            for (int i=0;i<count.length();i++){
                JSONObject data=count.getJSONObject(i);
                //Log.i("json",data.get("title")+";"+data.get("body"));
                Log.i("json",data.get("lat")+";"+data.get("lng"));
                Log.i("json",data+"");
                //GPS情報の書き換え
                Log.d("setMockMode", "setMockLocation");
                Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
                mockLocation.setLatitude(Double.parseDouble(data.getString("lat")));
                mockLocation.setLongitude(Double.parseDouble(data.getString("lng")));
                mockLocation.setAltitude(0);
                mockLocation.setTime(System.currentTimeMillis());
                mockLocation.setAccuracy(1);
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                //gpsアクセスが許可されているか
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                fusedLocationClient.setMockLocation(mockLocation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Service終了
        stopSelf();
    }
    //タイマー
    public class LocationTimerTask extends TimerTask {
        private Handler handler;
        private Context context;
        public LocationTimerTask(Context context){
            handler = new Handler();
            this.context = context;
        }

        @Override
        public void run(){
            handler.post(new Runnable() {
                @Override
                public void run() { ((LocationService) context).InvalidateScreen();
                }
            });
        }
    }
}
