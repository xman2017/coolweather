package com.xman.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by xman on 2017/4/6.
 */

public class City extends DataSupport{

    private int id;
    private String cityName;
    /**
     * 城市的代号
     */
    private int cityCode;

    /**
     * 当前城市所属的省的id
     */
    private int provinceId;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }
}
