package com.example.httprequests;

import static com.example.httprequests.LocationService.ACTION_REQUEST_LOCATION_PERMISSION;
import static com.example.httprequests.LocationService.LOCATION_UPDATE;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService extends Service {
    final OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("WeatherService","created!!!");
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(LOCATION_UPDATE));
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            Log.d("WeatherService",latitude+" "+longitude);
            new WeatherApiCallTask().execute(latitude,longitude);

        }
    };




    private class WeatherApiCallTask extends AsyncTask<Double, Void, Response> {

        @Override
        protected Response doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];
            Log.d("WeatherServiceAsyncTask",latitude+" "+longitude);

            BigDecimal roundedLat=BigDecimal.valueOf(latitude).setScale(2, RoundingMode.HALF_UP);
            BigDecimal roundedLong=BigDecimal.valueOf(longitude).setScale(2, RoundingMode.HALF_UP);

            String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude="+roundedLat+"&longitude="+roundedLong+"&minutely_15=precipitation&forecast_minutely_15=1";
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                //Log.d("Response",response.body().string());
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);

            // Handle the API response here
            if (response != null && response.body() != null) {
                try {
                    String responseBody = response.body().string();

                    if (!responseBody.isEmpty()) {
                        JSONObject responseData = new JSONObject(responseBody);
                        JSONObject obj=responseData.getJSONObject("minutely_15");
                        Double precipitation=obj.getJSONArray("precipitation").getDouble(0);

                        if(precipitation>=0.2){
                            Intent notificationIntent=new Intent(WeatherService.this,NotificationService.class);
                            notificationIntent.putExtra("precipitation", precipitation);
                            startService(notificationIntent);
                        }


                        Log.d("PrecipitationVolume", String.valueOf(precipitation));
                    } else {
                        Log.e("WeatherService", "Empty response body");
                    }
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e("WeatherService", "API call failed");
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
