package com.example.torres.weatherforecastapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherNotification extends AppCompatActivity {

    TextView notification;
    String arrayNotification = "arrayNotification";
    String city = "cityName";
    String addAllDays = "";
    ArrayList<String> myWeatherArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        notification = (TextView) findViewById(R.id.notificationView);

        // Retrieve intent with an array of rain intensity
        Intent intent = getIntent();
        myWeatherArrayList = intent.getStringArrayListExtra(arrayNotification);
        String cityName = intent.getStringExtra(city);

        // Create the string that includes all of the rainy days (passed from the intent)
        for (int i =0; i < myWeatherArrayList.size(); i++) {
            addAllDays += "Day: " + myWeatherArrayList.get(i) + "\n";
        }

        // Display all the rainy days in a text view
        notification.setText("It will rain in " + cityName + ":\n" + addAllDays);
    }
}
