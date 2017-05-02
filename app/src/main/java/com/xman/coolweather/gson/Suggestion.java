package com.xman.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/2/002.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort confort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;


    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
