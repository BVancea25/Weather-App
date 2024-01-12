package com.example.httprequests;

import static com.example.httprequests.LocationService.ACTION_REQUEST_LOCATION_PERMISSION;
import static com.example.httprequests.NotificationService.ACTION_REQUEST_NOTIFICATION_PERMISSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    HashMap<String, List<Double>> coordinatesCityMapping=new HashMap<>();

    private Intent locationServiceIntent;

    private Intent weatherServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        coordinatesCityMapping.put("Baia Mare",new ArrayList<>(List.of(47.65,23.58)));
        coordinatesCityMapping.put("Cluj-Napoca",new ArrayList<>(List.of(46.77,23.62)));
        coordinatesCityMapping.put("Bucuresti",new ArrayList<>(List.of(44.42,26.10)));
        coordinatesCityMapping.put("Oradea",new ArrayList<>(List.of(47.04, 21.91)));
        coordinatesCityMapping.put("Brasov",new ArrayList<>(List.of(45.64, 25.58)));
        coordinatesCityMapping.put("Iasi",new ArrayList<>(List.of(47.15, 27.60)));
        createNotificationChannel();


        ArrayList<String> keyList = new ArrayList<>(coordinatesCityMapping.keySet());//lista orase

        ListView listView=findViewById(R.id.localitati);//list view

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.item_layout,R.id.cityName,keyList);//adaptor
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = keyList.get(position);


                String url="https://api.open-meteo.com/v1/forecast?latitude="+coordinatesCityMapping.get(selectedCity).get(0)+"&longitude="+coordinatesCityMapping.get(selectedCity).get(1)+"&daily=weather_code,temperature_2m_max&timezone=auto";


                Intent intent=new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("selectedCity", selectedCity);
                startActivity(intent);
            }


        });

        locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);

        weatherServiceIntent=new Intent(this,WeatherService.class);
        startService(weatherServiceIntent);




    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                "weather_channel",
                "Weather Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private BroadcastReceiver notificationPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity","Got notification permissions intent");
            requestNotificationPermission();
        }
    };

    private void requestNotificationPermission() {
        Log.d("MainActivity","requested notification permissions");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},1
        );
    }

    private BroadcastReceiver locationPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity","Got permissions intent");
            requestLocationPermission();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(locationPermissionReceiver, new IntentFilter(ACTION_REQUEST_LOCATION_PERMISSION ));
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationPermissionReceiver,new IntentFilter(ACTION_REQUEST_NOTIFICATION_PERMISSION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationPermissionReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationPermissionReceiver);
    }

    private void requestLocationPermission() {
        Log.d("MainActivity","requested permissions");
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},1
                );
    }


}