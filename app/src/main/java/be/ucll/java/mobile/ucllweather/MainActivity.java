package be.ucll.java.mobile.ucllweather;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.Collections;

import be.ucll.java.mobile.ucllweather.model.CitySearch;
import be.ucll.java.mobile.ucllweather.webservice.WeatherService;

public class MainActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener {
    private static final String TAG = "MainActivity";
    private static final String GEONAMES_API_URL_PREFIX = "http://api.geonames.org/searchJSON?q=";
    private static final String GEONAMES_API_URL_TRAILING = "&maxRows=1&username=jodieorourke";

    private TextView txtSearch;
    private TextView txtCity;
    private TextView txtCountry;
    private TextView txtTemperature;
    private TextView txtPressure;
    private TextView txtHumidity;

    private RequestQueue queue;
    private WeatherService weatherService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSearch = findViewById(R.id.txtSearch);
        txtCity = findViewById(R.id.txtCity);
        txtCountry = findViewById(R.id.txtCountry);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtPressure = findViewById(R.id.txtPressure);
        txtHumidity = findViewById(R.id.txtHumidity);

        weatherService = new WeatherService(this);

    }

    public void onBtnSearchClick(View view) {
        // Instantiate the RequestQueue for asynchronous operations
        queue = Volley.newRequestQueue(this);

        // Encode as UTF8 characters
        String searchterm = txtSearch.getText().toString();
        try {
            searchterm = URLEncoder.encode(searchterm, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }

        String url = GEONAMES_API_URL_PREFIX + searchterm + GEONAMES_API_URL_TRAILING;
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
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        CitySearch respo = new Gson().fromJson(jsono.toString(), CitySearch.class);

        if(respo.getGeonames()!=null||respo.getGeonames().size()!=0){
            if(respo.getGeonames().get(0).getToponymName()!=null) {
                txtCity.setText(respo.getGeonames().get(0).getToponymName());
            }else{
                txtCity.setText(R.string.unavailable);
            }
            if(respo.getGeonames().get(0).getCountryName()!=null) {
                txtCountry.setText(respo.getGeonames().get(0).getCountryName());
            }else{
                txtCountry.setText(R.string.unavailable);
            }

            weatherService.giveWeatherAtCoordinate(respo.getGeonames().get(0).getLng(),
                    respo.getGeonames().get(0).getLat());
        }else{
            Log.e(TAG,"Geonames is null!");
        }

    }

    public TextView getTxtTemperature() {
        return txtTemperature;
    }

    public TextView getTxtPressure() {
        return txtPressure;
    }

    public TextView getTxtHumidity() {
        return txtHumidity;
    }
}