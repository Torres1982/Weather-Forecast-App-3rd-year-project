package com.example.torres.weatherforecastapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
//import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    Button sendRequest;
    Button getCurrentLocation;
    String spinnerValue = "";
    EditText city;
    EditText days;
    String cityName = "";
    String weatherDays = "";
    Integer numberOfDays = 0;

    double currentlongitude;
    double currentlatitude;
    Location myLocation;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.spinner);
        sendRequest = (Button) findViewById(R.id.button3);
        // Disable Send Request Button at start
        //sendRequest.setEnabled(false);
        getCurrentLocation = (Button) findViewById(R.id.button);
        city = (EditText) findViewById(R.id.editText);
        days = (EditText) findViewById(R.id.editNumberOfDays);
        //cityName = city.getText().toString();
        //Toast.makeText(MainActivity.this, "Selected city: " + cityName, Toast.LENGTH_LONG).show();

        addListenerOnSendRequestButton();
        addListenerOnGetCurrentLocationButton();

        //DownloadJSONarray downloadJsonArray = new DownloadJSONarray(getBaseContext());
        //downloadJsonArray.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + cityName + "&units=metric&cnt=16&appid=cf4691cab83f56ebe2c0b293b7f74fbd");
    }

    // Get current user location
    public void getUserLocation() {

        try {
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            currentlatitude = myLocation.getLatitude();
            currentlongitude = myLocation.getLongitude();
            //city.setText(cityName, TextView.BufferType.EDITABLE);
        }
        catch (Exception exception) {
            String exceptionMessage = "Error while getting current location!";
            Toast.makeText(MainActivity.this, exceptionMessage, Toast.LENGTH_LONG).show();
        }
    }

    // Button Listener (Get Current Location)
    public void addListenerOnGetCurrentLocationButton() {
        getCurrentLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getUserLocation();
                chooseCityFromCurrentLocation();
                city.setText(cityName);
                Toast.makeText(MainActivity.this, "Coordinates: " + currentlongitude +", " + currentlatitude, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Button Listener (Send Request)
    public void addListenerOnSendRequestButton() {
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the value from the selected dropdown list
                spinnerValue = String.valueOf(spinner.getSelectedItem());

                // Get the number of days from the edit text input field
                weatherDays = days.getText().toString();
                numberOfDays = Integer.parseInt(weatherDays);
                //Toast.makeText(MainActivity.this, "Value from the spinner: " + spinnerValue + ", Number of days: " + numberOfDays, Toast.LENGTH_LONG).show();

                // Get the city name from the edit text input box
                cityName = city.getText().toString();
                //selectCity();
                Toast.makeText(MainActivity.this, "Selected city: " + cityName, Toast.LENGTH_LONG).show();

                // Set up Calendar - time for alarm
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 22);
                calendar.set(Calendar.MINUTE, 42);
                //calendar.set(Calendar.SECOND, 00);

                // Create and send intent
                Intent myIntent = new Intent(getApplicationContext(), WeatherService.class);
                myIntent.putExtra("rainIntensity", spinnerValue);
                myIntent.putExtra("cityName", cityName);
                myIntent.putExtra("numberOfDays", weatherDays);
                //startService(myIntent);

                // Set up Pending Intent and Alarm Manager
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarm.INTERVAL_DAY, pendingIntent);
            }
        });
    }

//    public void selectCity() {
//        if (!cityName.equals("")) {
//            cityName = city.getText().toString();
//        }
//        else if(cityName.equals("")) {
//            chooseCityFromCurrentLocation();
//        }
//        else {
//            String message = "Choose your current location or type in the city name!";
//            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
//        }
//    }

    // Assign the city name from current location coordinates
    public void chooseCityFromCurrentLocation() {
        if((currentlongitude > -6.5 && currentlongitude < -6.0) && (currentlatitude > 53.0 && currentlatitude < 53.9)) {
            cityName = "Dublin";
            //city.setText("Dublin");
        }
    }
//    public void enableButton() {
//        if ((!weatherDays.equals("")) && (!cityName.equals(""))) {
//            sendRequest.setEnabled(true);
//        }
//    }
}
