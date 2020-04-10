package com.tecnico.foodist.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.tecnico.foodist.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class FullMapActivity  extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView mMapView;
    private GoogleMap googleMap;

    private double rest_latitude;
    private double rest_longitude;
    private String rest_name;

    private double user_latitude;
    private double user_longitude;

    GeoApiContext geoApiContext;


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);
        mMapView = (MapView) findViewById(R.id.fullmap);

        Bundle bundle = getIntent().getExtras();
        rest_latitude = bundle.getDouble("rest_lat");
        rest_longitude = bundle.getDouble("rest_lon");
        rest_name = bundle.getString("rest_name");

        //get user current location
        getUserLocation();

        //so we can add trhreads to the main activity
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //initiate the Google Directions API with he apiKey so we can calculate the durantion travel
        geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyBKj_1qFGu2CzxY18nYR-Zb-rxU-Xjhv2Y").build();

        //init google map
        initGoogleMap(savedInstanceState);

    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(FullMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FullMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }
        map.setMyLocationEnabled(true);
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);
        setCameraView();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void setCameraView(){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(rest_latitude,rest_longitude), 18));
        getRoutes(rest_latitude,rest_longitude);

    }

    public void addPolylinesToMap (final DirectionsResult result){
        for (DirectionsRoute route: result.routes){

            List<com.google.maps.model.LatLng> decodePath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
            List<LatLng> newDecodePath = new ArrayList<>();
            for (com.google.maps.model.LatLng latLng: decodePath){
                newDecodePath.add(new LatLng(latLng.lat, latLng.lng));
            }

            Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(newDecodePath));
            polyline.setColor(ContextCompat.getColor(FullMapActivity.this,R.color.blueTec1));
            polyline.setClickable(true);

        }

    }


    public synchronized void getRoutes(double lat, double lon){

        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(new com.google.maps.model.LatLng(user_latitude, user_longitude))
                    .destination(new com.google.maps.model.LatLng(lat, lon))
                    .await();

            addPolylinesToMap(result);

            Marker marker = googleMap
                    .addMarker(new MarkerOptions()
                    .position(new LatLng(rest_latitude, rest_longitude))
                    .title(rest_name)
                    .snippet("Duration: "+result.routes[0].legs[0].duration));

            marker.showInfoWindow();
            Log.w("DirectionsResult", String.valueOf(result.routes[0].legs[0].duration));

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Location getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

        user_latitude = location.getLatitude();
        user_longitude = location.getLongitude();

        Log.d("latitude", String.valueOf(location.getLatitude()));
        Log.d("longitude", String.valueOf(location.getLongitude()));

        return location;

    }




































    public void calculateDistanceTime(double lat, double lon){
        Log.d("directions", "calculateDirections: calculating directions.");
        com.google.maps.model.LatLng destination =
                new com.google.maps.model.LatLng(lat, lon);

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.alternatives(true);

        directions.origin(
                new com.google.maps.model.LatLng(user_latitude, user_longitude)
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("routes", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("duration", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("distance", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("geocodedWayPoints", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("directions-failed", "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }


}
