package ru.gdgkazan.simpleweather.screen.weatherlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.gdgkazan.simpleweather.R;
import ru.gdgkazan.simpleweather.model.City;
import ru.gdgkazan.simpleweather.screen.general.LoadingDialog;
import ru.gdgkazan.simpleweather.screen.general.LoadingView;
import ru.gdgkazan.simpleweather.screen.general.SimpleDividerItemDecoration;
import ru.gdgkazan.simpleweather.screen.weather.WeatherActivity;

/**
 * @author Artur Vasilov
 */

/**
 * TODO : task
 *
 * 1) Load all cities forecast using one or multiple loaders
 * 2) Try to run these requests as most parallel as possible
 * or better do as less requests as possible
 * 3) Show loading indicator during loading process
 * 4) Allow to update forecasts with SwipeRefreshLayout
 * 5) Handle configuration changes
 *
 * Note that for the start point you only have cities names, not ids,
 * so you can't load multiple cities in one request.
 *
 * But you should think how to manage this case. I suggest you to start from reading docs mindfully.
 */
public class WeatherListActivity extends AppCompatActivity implements CitiesAdapter.OnItemClick, SwipeRefreshLayout.OnRefreshListener {

    private static final String CITIES = "ru.gdgkazan.simpleweather.screen.weatherlist.WeatherListActivity.CITIES";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty)
    View mEmptyView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private CitiesAdapter mAdapter;

    private LoadingView mLoadingView;
    private List<String> cityNames;
    private List<City> cities;
    private LoaderManager.LoaderCallbacks<List<City>> callbacks;
    private String cityIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        setSupportActionBar(mToolbar);

        cities = new ArrayList<>();
        cityNames = Arrays.asList(getResources().getStringArray(R.array.initial_cities));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this, false));

        mAdapter = new CitiesAdapter(cities, this);
        mRecyclerView.setAdapter(mAdapter);

        mLoadingView = LoadingDialog.view(getSupportFragmentManager());

        callbacks = new WeatherListCallbacks();

        if (savedInstanceState == null) {
            getSupportLoaderManager().initLoader(R.id.weather_loader_id, Bundle.EMPTY, callbacks);
        }else{
            cities = (ArrayList<City>) savedInstanceState.getSerializable(CITIES);
            showWeatherList(cities);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CITIES,new ArrayList<City>(cities));
    }

    @Override
    public void onItemClick(@NonNull City city) {
        startActivity(WeatherActivity.makeIntent(this, city.getName()));
    }

    private void showWeatherList(List<City> cities) {
        swipeRefreshLayout.setRefreshing(false);
        if (cities != null) {
            this.cities = cities;
            mAdapter.changeDataSet(this.cities);
            if (cityIds == null) {
                cityIds = "";
                for (City city : cities) {
                    cityIds += city.getId() + ",";
                }
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(R.id.weather_loader_id, Bundle.EMPTY, callbacks);
    }

    private class WeatherListCallbacks implements LoaderManager.LoaderCallbacks<List<City>> {

        @Override
        public Loader<List<City>> onCreateLoader(int id, Bundle args) {
            return new RetrofitWeatherListLoader(WeatherListActivity.this, cityNames, cityIds);
        }

        @Override
        public void onLoadFinished(Loader<List<City>> loader, List<City> data) {
            showWeatherList(data);
        }

        @Override
        public void onLoaderReset(Loader<List<City>> loader) {

        }
    }

}
