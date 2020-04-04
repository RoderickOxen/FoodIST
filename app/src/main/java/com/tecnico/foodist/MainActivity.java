package com.tecnico.foodist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.tecnico.foodist.models.User;
import com.tecnico.foodist.ui.FoodISTActivity;

import java.util.ArrayList;
import java.util.List;

import static com.tecnico.foodist.util.Constans.ERROR_DIALOG_REQUEST;
import static com.tecnico.foodist.util.Constans.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.tecnico.foodist.util.Constans.PERMISSIONS_REQUEST_ENABLE_GPS;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<LatLng> polygonAlameda = new ArrayList<>();
    private List<LatLng> polygonTagusPark = new ArrayList<>();
    private boolean atAlameda = false;
    private boolean atTaguspark = false;
    private ProgressDialog dialog;
    private User user;
    private boolean firstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (firstTime){
            dialog.setMessage("Please wait.");
            dialog.show();
            polygonConstructer();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            firstTime = false;
        }


    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //STEP 1
    public boolean isServicesOK() {

        String TAG = "isServiceOk";
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //STEP 2
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    //STEP 3
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "onActivityResult";

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    Log.d(TAG, "Locaiton granted.");
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    //STEP 4 if not granted
    private void getLocationPermission() {
        String TAG = "getLocationPermission";
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(TAG, "Locaiton permission.");
            getCampus();
            //startFoodIST();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //STEP 5 runs after they give or not permission and its the final so maps is ready to go
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    //Any Activity that restarts has its onResume() method executed first.
    @Override
    public void onResume() {
        String TAG = "onResume";
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Log.d(TAG, "END");
                //startFoodIST();
                getCampus();

            } else {
                getLocationPermission();
            }
        }
    }

    private void polygonConstructer() {
        // Construct a polygon around alameda
        polygonAlameda.add(new LatLng(38.737882, -9.140786));
        polygonAlameda.add(new LatLng(38.738183, -9.139081));
        polygonAlameda.add(new LatLng(38.737453, -9.136701));
        polygonAlameda.add(new LatLng(38.736219, -9.136476));
        polygonAlameda.add(new LatLng(38.735167, -9.138837));
        polygonAlameda.add(new LatLng(38.736715, -9.140543));

        // Construct a polygon around taguspark
        polygonTagusPark.add(new LatLng(38.738174, -9.303666));
        polygonTagusPark.add(new LatLng(38.737069, -9.303419));
        polygonTagusPark.add(new LatLng(38.736224, -9.301745));
        polygonTagusPark.add(new LatLng(38.737404, -9.301327));
        polygonTagusPark.add(new LatLng(38.737772, -9.302239));
        polygonTagusPark.add(new LatLng(38.738241, -9.302722));
    }

    private void getCampus() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.d("latitude", "latitude: " + location.getLatitude());
                    Log.d("longitude", "longitude: " + location.getLongitude());

                    //checks if is inside polygon
                    user = new User(latLng);
                    isInside(latLng);
                }
            }
        });
    }

    private void isInside(LatLng latLng) {
        //creates a new anon with the locations

        boolean isInsideAlameda = PolyUtil.containsLocation(latLng, polygonAlameda, true);
        boolean isInsideTaguspark = PolyUtil.containsLocation(latLng, polygonTagusPark, true);

        Log.d("al", String.valueOf(isInsideAlameda));
        Log.d("tg", String.valueOf(isInsideTaguspark));




        if (isInsideAlameda) {
            atAlameda = true;
            startFoodIST();
        } else if (isInsideTaguspark) {
            atTaguspark = true;
            startFoodIST();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("We can't detect your current location. Please insert your campus.")
                    .setCancelable(false)
                    .setPositiveButton("Alameda", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            atAlameda = true;
                            startFoodIST();
                        }
                    })
                    .setNegativeButton("Taguspark", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            atTaguspark = true;
                            startFoodIST();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    private void startFoodIST() {

        Log.w("atTagus", String.valueOf(atTaguspark));
        Log.w("atAlameda", String.valueOf(atAlameda));


        Intent intent = new Intent(MainActivity.this, FoodISTActivity.class);
        intent.putExtra("atAlameda", atAlameda);
        intent.putExtra("atTaguspark", atTaguspark);
        intent.putExtra("userLatitude", user.getUserLocation().latitude);
        intent.putExtra("userLongitude", user.getUserLocation().longitude);
        startActivity(intent);
    }


}