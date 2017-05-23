package com.example.torres.weatherforecastapp;

//import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WeatherService extends Service {

    String rainIntensity;
    String cityName;
    String numberOfDays;
    String itRains = "";

    ArrayList<String> descriptionArrayList = new ArrayList<>();
    ArrayList<String> chosenIntensityRain = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId ) {

        // Receive information from a bundle
        Bundle receiveBundle = intent.getExtras();

        rainIntensity = receiveBundle.getString("rainIntensity");
        cityName = receiveBundle.getString("cityName");
        numberOfDays = receiveBundle.getString("numberOfDays");

        //Toast.makeText(WeatherService.this, rainIntensity + ", " + cityName + ", " + numberOfDays, Toast.LENGTH_SHORT).show();

        WeatherArrayDownloader downloadJson = new WeatherArrayDownloader();
        downloadJson.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + cityName + "&units=metric&cnt=" + numberOfDays + "&appid=cf4691cab83f56ebe2c0b293b7f74fbd");

        return START_STICKY;
    }

    // Subclass to download JSON array
    class WeatherArrayDownloader extends AsyncTask <String, Void, String> {

        private static final String TAG = "WeatherArrayDownloader";
        String downloadedJsonArray = "";
        String main;
        String description;
        JSONArray forecastJSONArray;
        URL jsonArrayUrl;
        HttpURLConnection httpConnection = null;

//      private Context context;
        // Create a constructor to pass a context (for a Toast message)
//        public DownloadJSONarray(Context context) {
//            this.context = context;
//        }

        @Override
        protected String doInBackground(String... stringUrl) {

            try {
                jsonArrayUrl = new URL(stringUrl[0]);
                httpConnection = (HttpURLConnection)jsonArrayUrl.openConnection();

                InputStream inputStream = httpConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int dataToRead = inputStreamReader.read();

                while (dataToRead != -1) {
                    char currentData = (char)dataToRead;
                    downloadedJsonArray = downloadedJsonArray + currentData;
                    dataToRead = inputStreamReader.read();
                }
                return downloadedJsonArray;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String downloadedJsonArray) {
            super.onPostExecute(downloadedJsonArray);

            String weatherMessage;
            String info1 = "Weather Condition: ";
            String info2 = "Cannot identify weather conditions!";
            String info3 = "Exception: weather not found!";

            try {
                JSONObject jsonObject = new JSONObject(downloadedJsonArray);
                String weatherDetails = jsonObject.getString("list");
                //Log.i(TAG, "TESTING: " + weatherDetails.toString());
                //Toast.makeText(context, "TESTING: " + weatherDetails.toString(), Toast.LENGTH_SHORT).show();
                JSONArray array = new JSONArray(weatherDetails);
                //Log.i("***********************", array.toString());
                //JSONArray mainJSONArrayOfDays = jsonObject.getJSONArray("list");
                //int lengthOfJSONArray = mainJSONArrayOfDays.length();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonTempObject = array.getJSONObject(i);
                    forecastJSONArray = jsonTempObject.getJSONArray("weather");
                    //Log.i("$$$$$$$$$$$$$$$$$$$$", forecastJSONArray.toString());
                    JSONObject jsonObject2 = forecastJSONArray.getJSONObject(0);
                    main = jsonObject2.getString("main");
                    description = jsonObject2.getString("description");

                    // Add all details from the JSON array to a new array
                    descriptionArrayList.add(description);
                    Log.i("^^^^^^^^^^^^^^^^^^^", descriptionArrayList.toString());

                    if ((!main.equals("")) && (!description.equals(""))) {
                        weatherMessage = info1 + main + " - " + description;
                        Log.i(TAG, "WeatherArrayDownloader: " + weatherMessage);
                    }
                }
                if (weatherDetails.equals("")) {
                    Toast.makeText(WeatherService.this, info2, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(WeatherService.this, info3, Toast.LENGTH_SHORT).show();
            }
            //Log.i(TAG, forecastJSONArray.toString());
            checkRainyDays();

            if (itRains.equals("rainyDays")) {
                setUpNotification();
            }
//            else if (itRains.equals("")) {
//                Toast.makeText(WeatherService.this, "No Rainy Days Found!", Toast.LENGTH_LONG).show();
//            }
        }

        // Rainy Weather Notification
        public void setUpNotification() {

            final Integer ID = 1;
            long [] vibrationPattern = {600, 600, 600, 600, 600, 600};
            String title = "Weather Notification";
            String information = "It is supposed to be raining!";
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(WeatherService.this);
            builder.setSmallIcon(R.drawable.weather_icon);
            //builder.setAutoCancel(true);
            builder.setVibrate(vibrationPattern);
            //builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setSound(alarmSound);
            builder.setContentTitle(title);
            builder.setContentText(information);

            Intent weatherIntent = new Intent(getApplicationContext(), WeatherNotification.class);

            // Add the rainy days array to intent (level of intensity of rain)
            weatherIntent.putExtra("arrayNotification", chosenIntensityRain);
            weatherIntent.putExtra("cityName", cityName);

            TaskStackBuilder tsb = TaskStackBuilder.create(WeatherService.this);
            tsb.addParentStack(WeatherNotification.class);
            tsb.addNextIntent(weatherIntent);

            PendingIntent pi = tsb.getPendingIntent(ID, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pi);

            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(ID, builder.build());

            itRains = "";
        }

        public void checkRainyDays() {
            for (int i = 0; i < descriptionArrayList.size(); i++) {
               if ((descriptionArrayList.get(i).equals("light rain") && rainIntensity.equals("Light Rain")) ||
                  (descriptionArrayList.get(i).equals("moderate rain")) ||
                       (descriptionArrayList.get(i).equals("very heavy rain")) ||
                       (descriptionArrayList.get(i).equals("extreme rain"))) {

                   chosenIntensityRain.add(descriptionArrayList.get(i));
                   itRains = "rainyDays";
               }
               else if ((descriptionArrayList.get(i).equals("moderate rain") && rainIntensity.equals("Moderate Rain")) ||
                       (descriptionArrayList.get(i).equals("very heavy rain")) ||
                       (descriptionArrayList.get(i).equals("extreme rain"))) {

                   chosenIntensityRain.add(descriptionArrayList.get(i));
                   itRains = "rainyDays";
               }
               else if ((descriptionArrayList.get(i).equals("very heavy rain") && rainIntensity.equals("Very Heavy Rain")) ||
                       (descriptionArrayList.get(i).equals("extreme rain"))) {

                   chosenIntensityRain.add(descriptionArrayList.get(i));
                   itRains = "rainyDays";
               }
               else if ((descriptionArrayList.get(i).equals("extreme rain") && rainIntensity.equals("Extreme Rain"))) {

                   chosenIntensityRain.add(descriptionArrayList.get(i));
                   itRains = "rainyDays";
               }
            }
        }
    }
}