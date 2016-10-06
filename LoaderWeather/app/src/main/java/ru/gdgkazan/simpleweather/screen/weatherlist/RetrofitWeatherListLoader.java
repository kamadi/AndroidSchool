package ru.gdgkazan.simpleweather.screen.weatherlist;

import android.content.Context;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.gdgkazan.simpleweather.model.City;
import ru.gdgkazan.simpleweather.model.CityList;
import ru.gdgkazan.simpleweather.network.ApiFactory;

/**
 * Created by Madiyar on 29.09.2016.
 */

public class RetrofitWeatherListLoader extends Loader<List<City>> implements Callback<City> {
    private static final String TAG = RetrofitWeatherListLoader.class.getSimpleName();
    private String cityIds;
    private List<String> cityNames;
    private List<City> cities = new ArrayList<>();
    private List<Call<City>> calls = new ArrayList<>();

    public RetrofitWeatherListLoader(Context context, List<String> cityNames, String cityIds) {
        super(context);
        this.cityNames = cityNames;
        this.cityIds = cityIds;
        if (cityIds == null) {
            for (String cityName : cityNames) {
                calls.add(ApiFactory.getWeatherService().getWeather(cityName));
            }
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (!cities.isEmpty()) {
            deliverResult(cities);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        if (cityIds == null) {
            for (Call<City> call : calls) {
                call.enqueue(this);
            }
        } else {
            Call<CityList> call = ApiFactory.getWeatherService().getWeathers(cityIds);
            call.enqueue(new Callback<CityList>() {
                @Override
                public void onResponse(Call<CityList> call, Response<CityList> response) {
                    deliverResult(response.body().getCities());
                }

                @Override
                public void onFailure(Call<CityList> call, Throwable t) {
                    deliverResult(null);
                }
            });
        }
    }

    @Override
    public void onResponse(Call<City> call, Response<City> response) {
        cities.add(response.body());
        if (cityNames.size() == cities.size()) {
            deliverResult(cities);
        }
    }

    @Override
    public void onFailure(Call<City> call, Throwable t) {
        deliverResult(null);
    }
}
