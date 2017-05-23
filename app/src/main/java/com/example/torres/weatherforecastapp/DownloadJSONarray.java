package com.example.torres.weatherforecastapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadJSONarray extends AsyncTask <String, Void, String> {

    private static final String TAG = "DownloadJSONarray";
    String downloadedJsonArray = "";
    String main;
    String description;
    JSONArray forecastJSONArray;
    URL jsonArrayUrl;
    HttpURLConnection httpConnection = null;
    private Context context;

    // Create a constructor to pass a context (for a Toast message)
    public DownloadJSONarray(Context context) {
        this.context = context;
    }

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

            //JSONArray mainJSONArrayOfDays = jsonObject.getJSONArray("list");
            //int lengthOfJSONArray = mainJSONArrayOfDays.length();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonTempObject = array.getJSONObject(i);
                    forecastJSONArray = jsonTempObject.getJSONArray("weather");
                    JSONObject jsonObject2 = forecastJSONArray.getJSONObject(0);
                    main = jsonObject2.getString("main");
                    description = jsonObject2.getString("description");

                        if ((!main.equals("")) && (!description.equals(""))) {
                            weatherMessage = info1 + main + " - " + description;
                            Log.i(TAG, "DownloadJSONarray: " + weatherMessage);
                        }
                }
            if(!weatherDetails.equals("")) {
                Toast.makeText(context, info2, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(context, info3, Toast.LENGTH_SHORT).show();
        }
    }
}
