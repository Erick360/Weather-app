package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {


    public static final String URL = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";


    Context context;

    String cityId;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityId);
    }


    public void getCityId(String cityName, VolleyResponseListener volleyResponseListener) {

        // Instantiate the RequestQueue.
        //RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = URL + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityId = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityId = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //Toast.makeText( context, "City ID = " + cityId, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("SOMETHING WRONG");
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
        //queue.add(request);
                /*
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.MethoSd.GET, url,
                        response -> Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT ),
                        error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT ));

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                */

        //return cityId;

    }

    public interface ForecastByIdResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);

    }

    public void getCityForecastById(String cityId,final ForecastByIdResponse forecast) {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();

        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityId;
        // get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather_list");



                    for(int i = 0; i<consolidated_weather_list.length(); i++){

                        // get the first item in the array
                        WeatherReportModel one_day_weather = new WeatherReportModel();

                        JSONObject first_day_api = (JSONObject) consolidated_weather_list.get(i);
                        one_day_weather.setId(first_day_api.getInt("id"));
                        one_day_weather.setWeather_state_name(first_day_api.getString("Weather_state_name"));
                        one_day_weather.setWeather_state_abbr(first_day_api.getString("Weather_state_abbr"));
                        one_day_weather.setWind_direction_compass(first_day_api.getString("wind_direction_compass"));
                        one_day_weather.setCreated(first_day_api.getString("created"));
                        one_day_weather.setApplicable_date(first_day_api.getString("applicable_date"));
                        one_day_weather.setMin_temp(first_day_api.getLong("min_temp"));
                        one_day_weather.setMax_temp(first_day_api.getLong("max_temp"));
                        one_day_weather.setThe_temp(first_day_api.getLong("the_temp"));
                        one_day_weather.setWind_speed(first_day_api.getLong("wind_direction"));
                        one_day_weather.setAir_pressure(first_day_api.getInt("air_pressure"));
                        one_day_weather.setHumidity(first_day_api.getInt("humidity"));
                        one_day_weather.setVisibility(first_day_api.getLong("visibility"));
                        one_day_weather.setPredictability(first_day_api.getInt("predictability"));
                        weatherReportModels.add(one_day_weather);

                    }

                forecast.onResponse(weatherReportModels);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface GetCityForecastByNameCallback{
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);

    }


    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback){
        // fetch the city id given the city name
        getCityId(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityId) {
                getCityForecastById(cityId, new ForecastByIdResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
        // fetch the city forecast given the city id.

    }

}