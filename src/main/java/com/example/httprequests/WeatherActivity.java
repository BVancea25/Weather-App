package com.example.httprequests;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    final OkHttpClient client = new OkHttpClient();

    List<Double> temperatures = new ArrayList<>();

    List<String> codes=new ArrayList<>();

    List<LocalDate> dates=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String selectedCity = intent.getStringExtra("selectedCity");

        TextView cityName=findViewById(R.id.cityName);
        cityName.setText(selectedCity);

        System.out.println(url);
        assert url != null;
        Request request=new Request.Builder().url(url).build();

       doRequest(request, new ApiResponseCallBack() {
           @Override
           public void onResponse(JSONObject data) {
               try{
                   JSONObject dailyData = data.getJSONObject("daily");

                   JSONArray temperatureArray = dailyData.getJSONArray("temperature_2m_max");
                   JSONArray datesArray = dailyData.getJSONArray("time");
                   JSONArray codesArray = dailyData.getJSONArray("weather_code");
                   for (int i = 0; i < temperatureArray.length(); i++) {
                       temperatures.add(temperatureArray.getDouble(i));
                       codes.add(interpretCode(codesArray.getInt(i)));
                       LocalDate date=LocalDate.parse(datesArray.getString(i));
                       dates.add(date);
                   }
                   displayTemperatures(temperatures,dates,codes);
               } catch (JSONException e) {
                   throw new RuntimeException(e);
               }
           }

           @Override
           public void onFailure(Exception e) {

           }
       });
    }

    private String interpretCode(int code) {
        String weatherState = "";
      if(code<=3){
          weatherState="cloudy";
      } else if (code<12) {
          weatherState="fog";
      } else if (code<=19) {
          weatherState="rain in sight";
      } else if (code<29) {
          weatherState="precipitation with fog";
      } else if (code<39) {
          weatherState="snow storm";
      } else if (code<49) {
          weatherState="fog";
      } else if (code<59) {
          weatherState="drizzle";
      } else if (code<79) {
          weatherState="rain";
      } else if (code<99) {
          weatherState="storm";
      }
        return weatherState;
    }

    public void doRequest(Request request, final ApiResponseCallBack callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final JSONObject responseData = new JSONObject(response.body().string());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onResponse(responseData);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure(e);
                        }
                    }
                }
            }
        });
    }

    private void displayTemperatures(List<Double> temperatures,List<LocalDate> dates,List<String> codes) {
        RecyclerView recyclerView = findViewById(R.id.temperatureRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        TemperatureAdapter adapter = new TemperatureAdapter(temperatures,dates,codes);
        recyclerView.setAdapter(adapter);
    }

}
