package com.xman.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xman.coolweather.gson.Weather;
import com.xman.coolweather.util.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkCache();
    }

    private void checkCache() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherResponse = sp.getString("weather", null);
        if (weatherResponse != null) {
            startActivity(new Intent(this,WeatherActivity.class));
            finish();
        }
    }
}
