package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {


    public static final String URL = "https://www.metaweather.com/api/location/search/?query=";

    Context context;

    String cityId;

    public WeatherDataService(Context context){
        this.context = context;
    }

    public interface VolleyResponseListener{
        void onError(String message);

        void onResponse(String cityId);
    }


    public void getCityId(String cityName, VolleyResponseListener volleyResponseListener){

        // Instantiate the RequestQueue.
        //RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = URL + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null ,new Response.Listener<JSONArray>() {
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

    public List<WeatherReportModel> getCityForecastById(String cityId){
        List<WeatherReportModel> report = new ArrayList<>();

        //
    }

}
