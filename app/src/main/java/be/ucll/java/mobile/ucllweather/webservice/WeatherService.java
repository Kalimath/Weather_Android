package be.ucll.java.mobile.ucllweather.webservice;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import be.ucll.java.mobile.ucllweather.MainActivity;
import be.ucll.java.mobile.ucllweather.R;
import be.ucll.java.mobile.ucllweather.model.CityWeather;

public class WeatherService implements Response.Listener, Response.ErrorListener {
    private static final String TAG = "WeatherService";

    private static final String GEONAMES_API_URL_WEATHER_PREFIX = "http://api.geonames.org/findNearByWeatherJSON?lat=";
    private static final String GEONAMES_API_URL_WEATHER_TRAILING = "&username=jodieorourke";
    private static final String GEONAMES_API_URL_WEATHER_CENTER = "&lng=";


    private RequestQueue queue;
    private MainActivity mainActivity;

    public WeatherService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void giveWeatherAtCoordinate(String lng, String lat){

        // Instantiate the RequestQueue for asynchronous operations
        queue = Volley.newRequestQueue(mainActivity);


        // Encode as UTF8 characters
        try {
            lng = URLEncoder.encode(lng, "UTF-8");
            lat = URLEncoder.encode(lat, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }

        String url = GEONAMES_API_URL_WEATHER_PREFIX + lat + GEONAMES_API_URL_WEATHER_CENTER + lng + GEONAMES_API_URL_WEATHER_TRAILING;
        Log.d(TAG, "URL: " + url);

        // Prepare the request to be send out towards the REST service Geonames
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        // Add the request to the RequestQueue for asynchronous retrieval on separate thread.
        queue.add(req);
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        // This is when the call upon the web service remains unanswered or in error
        Toast.makeText(mainActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, error.getMessage());
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        // Cast into Gson JSONObject
        JSONObject jsono = (JSONObject) response;

        // Log the output as debug information
        Log.d(TAG, jsono.toString());

        // Convert REST String to Pojo's using GSON libraries
        CityWeather respo = new Gson().fromJson(jsono.toString(), CityWeather.class);

        if(respo.getWeatherObservation().getTemperature()!=null) {
            mainActivity.getTxtTemperature().setText(respo.getWeatherObservation().getTemperature() + " °C");
        }else{
            mainActivity.getTxtTemperature().setText(R.string.unavailable);
        }
        if(respo.getWeatherObservation().getHectoPascAltimeter()!=null) {
            mainActivity.getTxtPressure().setText(respo.getWeatherObservation().getHectoPascAltimeter()+" Hp");
        }else{
            mainActivity.getTxtPressure().setText(R.string.unavailable);
        }
        if(respo.getWeatherObservation().getHumidity()!=null) {
            mainActivity.getTxtHumidity().setText(respo.getWeatherObservation().getHumidity()+" %");
        }else{
            mainActivity.getTxtHumidity().setText(R.string.unavailable);
        }
    }
}
