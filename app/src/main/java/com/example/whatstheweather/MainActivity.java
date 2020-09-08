package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String apikey;
    String cityName;
    EditText editText;
    TextView textView;
    public class DownloadWeather extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection = null;
            String result = null;
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int data = inputStreamReader.read();
                while(data!=-1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }



            }catch(FileNotFoundException e){
                return "City not found....try again!";
            } catch (Exception e) {
                Log.i("info",e.toString());
            }

            return result;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String crappyPrefix = "null";

            if(s.startsWith(crappyPrefix)){
                s= s.substring(crappyPrefix.length());
            }
            if(s !="City not found....try again!"){try {
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");
                Log.i("info",weather);
                JSONArray jsonArray = new JSONArray(weather);
                JSONObject jsonPart=null;
                String message = "";
                for(int i=0;i<jsonArray.length();i++) {
                    jsonPart = jsonArray.getJSONObject(i);

                    Log.i("info", jsonPart.getString("main"));
                    Log.i("info", jsonPart.getString("description"));
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")) {
                      message +=  main + " : "+ description + "\r\n";
                    }
                }
                if (message!=""){
                    textView.setText(message);
                }


            }  catch (Exception e) {
                Log.i("info",e.toString());
            }}
            else {
                textView.setText(s);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apikey = "enterkey";//Key is changed here for privacy
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView2);





    }
    public void getWeather(View view){
        cityName = editText.getText().toString();
        String result = null;
        DownloadWeather downloadWeather = new DownloadWeather();
        try {
            result = downloadWeather.execute("https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&appid="+apikey).get();


        } catch (Exception e) {
            Log.i("info",e.toString());}

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);


    }
}
