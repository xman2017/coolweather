package com.xman.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2/002.
 */

public class Weather {

    public String status;

    public Aqi aqi;

    public Basic basic;

    public Now now;

    public Suggestion suggestion;

    public List<Forecast> daily_forecast;
}
