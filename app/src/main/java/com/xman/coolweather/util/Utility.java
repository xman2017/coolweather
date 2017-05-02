package com.xman.coolweather.util;

import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.xman.coolweather.db.City;
import com.xman.coolweather.db.Country;
import com.xman.coolweather.db.Province;
import com.xman.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xman on 2017/4/6.
 * 解析返回数据
 */

public class Utility {

    /**
     * 处理省级数据,并且保存到数据库
     *
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObj = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObj.getInt("id"));
                    province.setProvinceName(provinceObj.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 处理市级返回数据,并且保存到数据库
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObj = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObj.getInt("id"));
                    city.setCityName(cityObj.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 解析处理返回的县级数据,并且保存到数据库
     *
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountryResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCountries = new JSONArray(response);
                for (int i = 0; i < allCountries.length(); i++) {
                    JSONObject countryObj = allCountries.getJSONObject(i);
                    Country country = new Country();
                    country.setCityId(cityId);
                    country.setCountryName(countryObj.getString("name"));
                    country.setWeatherId(countryObj.getString("weather_id"));
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * 处理返回的天气数据
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("HeWeather");
            String content = array.getJSONObject(0).toString();
            return new Gson().fromJson(content,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
