package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.spec.ECField;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultText;
    public void loadToast(){
        //check if user's input is wrong
        Toast.makeText(getApplicationContext(), "Could not find city", Toast.LENGTH_SHORT).show();
    }
    //download content class
    public class DownloadClass extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... links) {
            String output = "";
            URL link;
            HttpURLConnection httpURLConnection = null;
            try{
                link = new URL(links[0]);
                httpURLConnection = (HttpURLConnection)link.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                int data = isr.read();
                while (data != -1){
                    char currChar= (char) data;
                    output +=currChar;
                    data = isr.read();
                }
                return output;

            }catch (Exception e){
                e.printStackTrace();
                loadToast();
                return null;
            }
        }
        //use JSON object
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //string to pupulate resultText
            String resultMessage ="";

//            //retrieve temperature data from API
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//                String tempString = jsonObject.getString("main");
//                JSONArray jsonArrayTemp = new JSONArray(tempString);
//                for(int j = 0; j < jsonArrayTemp.length(); j++){
//                    JSONObject jPart = jsonArrayTemp.getJSONObject(j);
//                    String temperature = jPart.getString("temp");
//                    if(!temperature.equals("")){
//                        resultMessage +=temperature;
//                    }
//                    if (!resultMessage.equals("")){
//                        resultText.setText(resultMessage);
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            //retrieve main data from API
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherString = jsonObject.getString("weather");

                Log.i("Weather info", weatherString);
                JSONArray jsonArray = new JSONArray(weatherString);
                //loop through array to get data needed
                for(int i =0; i < jsonArray.length(); i++){
                    JSONObject jPart = jsonArray.getJSONObject(i);
                    String mainString = jPart.getString("main");
                    String descString = jPart.getString("description");
                    if(!mainString.equals("")&& !descString.equals("")){
                        resultMessage += mainString + ": "  + descString + "\r\n";
                    }

                    //Log.i("main ", jPart.getString("main"));
                    //Log.i("description ", jPart.getString("description"));
                }
                if (!resultMessage.equals("")){
                    resultText.setText(resultMessage);
                } else{
                    //check if user's input is wrong
                    loadToast();
                }
            }
            catch (Exception e ){
                //check if user's input is wrong
                loadToast();
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //access edit text to check which city weather we need to get
        editText = findViewById(R.id.editText);
        resultText = findViewById(R.id.resultText);


    }
    public void getWeatherData(View view){
        //get rid of keyboard after clicking Weather button
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        //get city name from user's input in edittext
        String cityText = editText.getText().toString();

        //make sure spaces between words is handled properly
        try {
            String spacedCityName= URLEncoder.encode(cityText, "UTF-8");
            //concatenated string for link to be opened
            String link = "https://openweathermap.org/data/2.5/weather?q=" + spacedCityName + "&appid=b6907d289e10d714a6e88b30761fae22";
            //create Download class to download data
            DownloadClass downloadClass = new DownloadClass();
            downloadClass.execute(link);
        }catch (Exception e){
            loadToast();
            e.printStackTrace();
        }



    }
}
