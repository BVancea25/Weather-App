package com.example.httprequests;

import android.health.connect.datatypes.units.Temperature;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TemperatureAdapter extends RecyclerView.Adapter<TemperatureAdapter.ViewHolder> {
    private List<Double> temperatures;
    private List<LocalDate> dates;

    private List<String> codes;


    public TemperatureAdapter(List<Double> temperatures, List<LocalDate> dates,List<String> codes) {
        this.temperatures = temperatures;
        this.dates=dates;
        this.codes=codes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temperature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("Adapter", "onBindViewHolder: " + position);
        Double temperature = temperatures.get(position);
        LocalDate date=dates.get(position);
        String weatherState=codes.get(position);
        holder.bind(temperature,date,weatherState);

    }

    @Override
    public int getItemCount() {
        return temperatures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView temperatureTextView;
        private TextView dateTextView;

        private TextView weatherState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            temperatureTextView = itemView.findViewById(R.id.temperatureTextView);
            dateTextView=itemView.findViewById(R.id.dateTextView);
            weatherState=itemView.findViewById(R.id.weaterState);
        }

        public void bind(Double temperature,LocalDate date,String code) {
            temperatureTextView.setText(String.valueOf(temperature)+"°C");
            dateTextView.setText(date.getDayOfWeek().toString());
            weatherState.setText(code);
        }
    }
}
