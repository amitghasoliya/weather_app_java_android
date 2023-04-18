package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String APP_ID = "faddaa9a263b90cbbab1702e7756b0f3";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView cityName, weatherCondition, temperature;
    ImageView weatherIcon;
    RelativeLayout cityFinder;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherCondition = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        cityName = findViewById(R.id.cityName);
        cityFinder = findViewById(R.id.cityFinder);
        weatherIcon = findViewById(R.id.weatherIcon);


        cityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cityFinder.class);
                startActivity(intent);
            }
        });
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        getWeatherForCurrentLocation();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String city = intent.getStringExtra("City");
        if (city!=null){
            getWeatherForNewCity(city);
        }
        else {
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city){
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());
                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                letsdoSomeNetworking(params);

            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Location Gets Successfully", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void letsdoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(MainActivity.this, "Data Get Success", Toast.LENGTH_SHORT).show();
                weatherData weatherD = weatherData.fromJson(response);
                updateUI(weatherD);
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    private void updateUI(weatherData weather){
        temperature.setText(weather.getTemperature());
        cityName.setText(weather.getCity());
        weatherCondition.setText(weather.getWeatherType());
        int resourceID=getResources().getIdentifier(weather.getIcon(), "drawable",getPackageName());
        weatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }


}