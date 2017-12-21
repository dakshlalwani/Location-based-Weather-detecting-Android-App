package com.dnc.dncproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String date="0",minTemp,maxTemp,lat,lon;
    Double lati,longi,minT,maxT,roundoffminT=0.0,roundoffmaxT=0.0;
    final String DEGREE  = "\u00b0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle b = getIntent().getExtras();
        minTemp = b.getString("mintemp");
        maxTemp = b.getString("maxtemp");
        date = b.getString("date");
        lat = b.getString("latitude");
        lon = b.getString("longitude");
        minT = Double.parseDouble(minTemp);
        maxT = Double.parseDouble(maxTemp);
        minT = ((minT-32.0)*5.0)/9.0;
        maxT = ((maxT-32.0)*5.0)/9.0;
        roundoffminT = Math.round(minT * 10.0)/10.0;
        roundoffmaxT = Math.round(maxT * 10.0)/10.0;
        date = date.substring(0,10);
        lati = Double.parseDouble(lat);
        longi = Double.parseDouble(lon);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng place = new LatLng(lati, longi);
//        LatLng place = new LatLng(24, 73);
        mMap.addMarker(new MarkerOptions().position(place).title("Date  :  "+date+"\nMinimum Temperature  :  "+roundoffminT+DEGREE+"C\nMaximum Temperature  :  "+roundoffmaxT+DEGREE+"C")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,5));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }
}
