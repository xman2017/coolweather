package com.xman.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xman.coolweather.gson.Forecast;
import com.xman.coolweather.gson.Weather;
import com.xman.coolweather.service.AutoUpdateService;
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

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView tvTitleCity;

    private TextView tvUpdateTime;

    private TextView tvDegreeNow;

    private TextView tvCondNow;

    private LinearLayout forecastLayout;

    private TextView tvAqiInfo;

    private TextView tvPM25Info;

    private TextView tvComfortInfo;

    private TextView tvCarWashInfo;

    private TextView tvSportInfo;

    private ImageView imgBg;

    public SwipeRefreshLayout refreshLayout;

    public String weatherId;

    public DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initWindow();

        initViews();

        checkCache();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 检查是否有缓存
     */
    private void checkCache() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherResponse = sp.getString("weather", null);
        if (weatherResponse != null) {
            Weather weather = Utility.handleWeatherResponse(weatherResponse);
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.GONE);
            requestWeather(weatherId);
        }

        String bingImg = sp.getString("bing_img", null);
        if (bingImg != null) {
            Glide.with(this).load(bingImg).into(imgBg);
        } else {
            loadImage();
        }

    }

    /**
     * 加载图片
     */
    private void loadImage() {
        HttpUtil.sendOkhttpRequest(Constants.API_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgUrl = response.body().string();
                if (imgUrl != null) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("bing_img",imgUrl);
                    edit.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(imgUrl).into(imgBg);
                            refreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求天气数据
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {

        String url = Constants.API_WEATHER_API + weatherId + "&key=" + Constants.API_KEY;
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "请求数据失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseContent = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseContent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("ok".equals(weather.status) && weather != null) {

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("weather", responseContent);
                            edit.apply();

                            showWeatherInfo(weather);
                            //每次请求完数据后就要启动后台跟新服务
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);

                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });


    }

    /**
     * 展示天气数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degreeNow = weather.now.temperature + "℃";
        String CondNow = weather.now.more.info;
        tvTitleCity.setText(cityName);
        tvUpdateTime.setText(updateTime);
        tvDegreeNow.setText(degreeNow);
        tvCondNow.setText(CondNow);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.daily_forecast) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView tvDate = (TextView) view.findViewById(R.id.date_text);
            TextView tvInfo = (TextView) view.findViewById(R.id.info_text);
            TextView tvMaxTemp = (TextView) view.findViewById(R.id.max_temperature);
            TextView tvMinTemp = (TextView) view.findViewById(R.id.min_temperature);

            tvDate.setText(forecast.date);
            tvInfo.setText(forecast.more.info);
            tvMaxTemp.setText(forecast.temperature.max);
            tvMinTemp.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            tvAqiInfo.setText(weather.aqi.city.aqi);
            tvPM25Info.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.confort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sports = "运动建议：" + weather.suggestion.sport.info;

        tvComfortInfo.setText(comfort);
        tvCarWashInfo.setText(carWash);
        tvSportInfo.setText(sports);

        weatherLayout.setVisibility(View.VISIBLE);
        weatherId = weather.basic.weatherId;
    }

    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        tvTitleCity = (TextView) findViewById(R.id.title_city);
        tvUpdateTime = (TextView) findViewById(R.id.title_update_time);
        tvDegreeNow = (TextView) findViewById(R.id.tmp_now_info);
        tvCondNow = (TextView) findViewById(R.id.cond_now_info);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        tvAqiInfo = (TextView) findViewById(R.id.aqi_info);
        tvPM25Info = (TextView) findViewById(R.id.pm25_info);
        tvComfortInfo = (TextView) findViewById(R.id.comfort_info);
        tvCarWashInfo = (TextView) findViewById(R.id.car_wash_info);
        tvSportInfo = (TextView) findViewById(R.id.sport_info);
        imgBg = (ImageView) findViewById(R.id.bing_pic);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));


    }
}
