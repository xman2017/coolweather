package com.xman.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.xman.coolweather.gson.Weather;
import com.xman.coolweather.util.Constants;
import com.xman.coolweather.util.HttpUtil;
import com.xman.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/2/002.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int updateTime = 8 * 60 * 60 * 1000;
        long triggerAtTime = updateTime + SystemClock.elapsedRealtime();
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        String weather = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).getString("weather", null);
        if (weather != null) {
            final String weatherId = Utility.handleWeatherResponse(weather).basic.weatherId;
            String url = Constants.API_WEATHER_API + weatherId + Constants.API_KEY;
            HttpUtil.sendOkhttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseContent = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseContent);
                    if (weather != null && "ok".equals(weather.status) ){
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("weather",responseContent);
                        edit.apply();

                    }
                }
            });
        }
    }

    /**
     * 加载图片
     */
    private void updateBingPic() {
        HttpUtil.sendOkhttpRequest(Constants.API_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgUrl = response.body().string();
                if (imgUrl != null) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("bing_img", imgUrl);
                    edit.apply();
                }
            }
        });
    }
}
