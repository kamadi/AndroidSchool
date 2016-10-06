package ru.gdgkazan.simpleweather.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Madiyar on 06.10.2016.
 */

public class CityList {
    @SerializedName("list")
    private List<City>cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
