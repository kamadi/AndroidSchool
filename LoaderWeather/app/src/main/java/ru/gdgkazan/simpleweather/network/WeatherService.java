package ru.gdgkazan.simpleweather.network;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.gdgkazan.simpleweather.model.City;
import ru.gdgkazan.simpleweather.model.CityList;

/**
 * @author Artur Vasilov
 */
public interface WeatherService {

    @GET("data/2.5/weather?units=metric")
    Call<City> getWeather(@NonNull @Query("q") String query);

    @GET("data/2.5/group?units=metric")
    Call<CityList> getWeathers(@NonNull @Query("id") String ids);

}
