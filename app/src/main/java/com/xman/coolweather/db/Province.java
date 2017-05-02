package com.xman.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by xman on 2017/4/6.
 */

public class Province extends DataSupport {

    private int id;
    private String provinceName;
    /**
     * 省的代号
     */
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
