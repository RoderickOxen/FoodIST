package com.tecnico.foodist.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.tecnico.foodist.R;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {
    private MapView mMapView;
    private GoogleMap googleMap;

    private double position_lat;
    private double position_lon;
    private String rest_name;

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    public MapViewFragment(double pos_latitude, double pos_longitude, String name) {
        this.position_lat = pos_latitude;
        this.position_lon = pos_longitude;
        this.rest_name = name;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view,container,false);
        mMapView = (MapView) view.findViewById(R.id.user_list_map);
        initGoogleMap(savedInstanceState);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

    }

    public static MapViewFragment newInstance(double pos_latitude, double pos_longitude, String name){
        return new MapViewFragment(pos_latitude, pos_longitude, name);
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
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }
        map.setMyLocationEnabled(true);
        googleMap = map;
        googleMap.setOnMarkerClickListener(this);
        setCameraView();
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
        LatLng marker = new LatLng(position_lat, position_lon);
        googleMap.addMarker(new MarkerOptions().position(marker)
                .title(rest_name));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position_lat,position_lon), 18));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.w("marker", "clicked");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(marker.getSnippet())
                .setMessage("want to directions to: " + rest_name)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDirections();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

        return true;
    }

    private void getDirections() {
    }


    @Override
    public void onClick(View v) {
        Log.w("click", "but");

        switch (v.getId()){
            //TO DOOO
            case R.id.btn_full_screen_map:{
                //go full
                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    Log.w("1", "MAP_LAYOUT_STATE_CONTRACTED");

                }
                //go 50
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    Log.w("2", "MAP_LAYOUT_STATE_EXPANDED");
                }
                break;
            }

        }

    }
}
