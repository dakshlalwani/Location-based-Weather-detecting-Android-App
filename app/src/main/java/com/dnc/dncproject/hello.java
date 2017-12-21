package com.dnc.dncproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class hello extends Activity{
    private EditText location;
    private TextView name;
    private MapReady mAuthTask = null;
    private BringOn mAuthTask2 = null;
    private String locn,username;
    private String key,lati,longi,date,mintemp,maxtemp;

//    private View view;
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
//        Log.d("start","hello");
//        view = inflater.inflate(R.layout.hello, container, false);
//        return view;//    {

//    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);
        Bundle b = getIntent().getExtras();
        username = b.getString("name");
        location = findViewById(R.id.edit);
        name = findViewById(R.id.name);
        name.setText(username);
        Button locEntered = findViewById(R.id.submit);
        locEntered.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });
    }

    private void attemptLogin()
    {
        // Reset errors.
        location.setError(null);

        // Store values at the time of the login attempt.
        locn = location.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid location address.
        if (TextUtils.isEmpty(locn))
        {
            location.setError(getString(R.string.error_field_required));
            focusView = location;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString("logged", "logged");
//            editor.commit();
            if(isOnline())
            {
                mAuthTask = new MapReady(locn);
                mAuthTask.execute((Void) null);
//                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                startActivity(intent);
            }
            else
            {
                Toast toast = Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }



    public class MapReady extends AsyncTask<Void, Void, Boolean> {
        private String mloc;

        MapReady(String loc) {
            mloc = loc;
        }

        @SuppressLint("Assert")
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpURLConnection conn;
            try {
                StringBuilder response = new StringBuilder();
                URL url = new URL("http://dataservice.accuweather.com/locations/v1/cities/search");
                Uri builtUri = Uri.parse(String.valueOf(url)).buildUpon().appendQueryParameter("apikey", "Es1ElBNY0tU1jV497Ite5jUWHTGKOoBT").appendQueryParameter("q", mloc).build();
                URL finalUrl = new URL(builtUri.toString());
                Log.d("beforefinalurl", String.valueOf(finalUrl));
                conn = (HttpURLConnection) finalUrl.openConnection();
                Log.d("before","in");
                InputStream in = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                Log.d("before","while1");
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    response.append(current);
                }
                String re = response.toString();
                JSONArray array = new JSONArray(re);
                try {
                    JSONObject ob = array.getJSONObject(0);
                    key = ob.getString("Key");
                    Log.d("beforekey",key);
                    String geopos = ob.getString("GeoPosition");
                    JSONObject geoarr = new JSONObject(geopos);
                    lati = geoarr.getString("Latitude");
                    longi = geoarr.getString("Longitude");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if(isOnline())
                {
                    mAuthTask2 = new BringOn();
                    mAuthTask2.execute((Void) null);
                }
            }
            else {
                location.setError("Enter a valid location");
                location.requestFocus();
            }

        }
    }

    public class BringOn extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection conni;
            try {
                StringBuilder response2 = new StringBuilder();
                String url2 = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/"+key;
                Log.d("beforekeys",key);
                URL urli = new URL(url2);
                Uri builtUri = Uri.parse(String.valueOf(urli)).buildUpon().appendQueryParameter("apikey", "Es1ElBNY0tU1jV497Ite5jUWHTGKOoBT").build();
                URL finalUrli = new URL(builtUri.toString());
                Log.d("beforefinalurli", String.valueOf(finalUrli));
                conni = (HttpURLConnection) finalUrli.openConnection();
                Log.d("before","ini");
                InputStream ini = conni.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(ini);
                int inputStreamData = inputStreamReader.read();
                Log.d("before","while2");
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    response2.append(current);
                }
                String re2 = response2.toString();
                JSONObject ob2 = new JSONObject(re2);
                JSONArray arr = ob2.getJSONArray("DailyForecasts");
                try {
                    JSONObject ob = arr.getJSONObject(0);
                    date = ob.getString("Date");
                    String Temp = ob.getString("Temperature");
                    JSONObject Temperature = new JSONObject(Temp);
                    String min = Temperature.getString("Minimum");
                    JSONObject minimum = new JSONObject(min);
                    mintemp = minimum.getString("Value");
                    String max = Temperature.getString("Maximum");
                    JSONObject maximum = new JSONObject(max);
                    maxtemp = maximum.getString("Value");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("latitude",lati);
                intent.putExtra("longitude",longi);
                intent.putExtra("date",date);
                intent.putExtra("mintemp",mintemp);
                intent.putExtra("maxtemp",maxtemp);
                startActivity(intent);
            }

        }
    }
    private boolean isOnline()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
