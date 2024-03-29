package com.tecnico.foodist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import androidx.collection.LruCache;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;
import com.tecnico.foodist.models.MenuIst;
import com.tecnico.foodist.models.Restaurant;
import com.tecnico.foodist.util.SimWifiP2pBroadcastReceiver;
import com.tecnico.foodist.util.SimpWifip2pBroadCastReceiver;
import com.tecnico.foodist.util.TCPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FoodISTActivity extends AppCompatActivity implements SimWifiP2pManager.PeerListListener {

    //recycler view elements
    private RecyclerView recyclerView;
    private FoodISTAdapter adapter;

    //Restaurant
    private ArrayList<Restaurant> restaurants= new ArrayList<>();

    //Bundle extras
    private Boolean atAlameda;
    private Boolean atTagus;
    private Double userLatitude;
    private Double userLongitude;

    //toolbar campus locaiton
    private Toolbar toolbar;
    private TextView location;

    //TTO DO
    String queueTime = "Queue time: XXX";
    private FirebaseStorage storage;
    private Bitmap image; // = R.drawable.food900x700;


    //Termite
    public static final String TAG = "peerscanner";
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;
    private SimpWifip2pBroadCastReceiver mReceiver;
    private static FoodISTActivity ins;


    //For calculating the duration
    GeoApiContext geoApiContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodist);

        //initiate the Google Directions API with he apiKey so we can calculate the durantion travel
        geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyBKj_1qFGu2CzxY18nYR-Zb-rxU-Xjhv2Y").build();


        // Termite - register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimpWifip2pBroadCastReceiver(this);
        registerReceiver(mReceiver, filter);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Information: This App uses Wifi Direct.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(FoodISTActivity.this, SimWifiP2pService.class);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                        mBound = true;
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();



        //set toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set current campus on toolbar
        setCampusOnToolbar();

        //add listener to the location button to change
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLocationSearch();
            }
        });

        //add the recycler view
        if (atAlameda){
            getRestaurantsAlameda();
        }else {
            getRestaurantsTagusPark();
        }

        //refresh the contents of a view via a vertical swipe gesture
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.refreshFood);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                restaurants.clear();

                //fetch the current data from database
                if (atAlameda){
                    getRestaurantsAlameda();
                }
                else {
                    getRestaurantsTagusPark();
                }
                Toast.makeText(FoodISTActivity.this, "Refresh successful!", Toast.LENGTH_SHORT).show();
                pullToRefresh.setRefreshing(false);

            }
        });

        ins = this;

        //set Cache
        int cacheSize = 100 * 1024 * 1024; // 100MB
        LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getByteCount();
            }
        };

        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setmCache(mCache);


    }

    private void setCampusOnToolbar() {
        Bundle bundle = getIntent().getExtras();
        atAlameda = bundle.getBoolean("atAlameda");
        atTagus = bundle.getBoolean("atTaguspark");
        userLatitude = bundle.getDouble("userLatitude");
        userLongitude = bundle.getDouble("userLongitude");
        location = findViewById(R.id.toolbarTitle);


        if (atAlameda){
            location.setText("Campus Alameda");
        }
        else if (atTagus){
            location.setText("Campus Taguspark");
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foodist_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCompose:

                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You need to create an Account and Log In to access this feature. Want to create one?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fadeIn(View view) {
        // Create an AlphaAnimation variable
        // 0.0f makes the view invisible
        // 1.0f makes the view fully visible
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // Set out long you want the animation to be. * Measured in milliseconds *
        // 1000 milliseconds = 1 second
        anim.setDuration(1500);
        // Start the animation on our passed in view
        view.startAnimation(anim);
        /*  After the animation is complete we want to make sure we set the visibility of the view
            to VISIBLE. Otherwise it will go back to being INVISIBLE due to our previous lines
            that set the view to INVISIBLE */
        view.setVisibility(View.VISIBLE);
    }

    private void changeLocationSearch(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select the campus that you want to change to.")
                .setCancelable(false)
                .setPositiveButton("Alameda", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atAlameda = true;
                        atTagus = false;
                        location.setText("Campus Alameda");
                        adapter.clear();
                        getRestaurantsAlameda();

                    }
                })
                .setNegativeButton("Taguspark", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        atTagus = true;
                        atAlameda = false;
                        location.setText("Campus Taguspark");
                        adapter.clear();
                        getRestaurantsTagusPark();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getRestaurantsAlameda(){
        String tcpMessage = "LRALL";
        TCPClient tcpClient = new TCPClient(getApplicationContext(), tcpMessage);
        tcpClient.execute();


        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference restaurantsRef = rootRef.collection("restaurants");

        //for each restaurante on the database
        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant = new Restaurant();

                        //get restaurant name
                        String name = document.getString("Name");
                        Log.w("Firebase-Name", name);
                        restaurant.setRestaurants_name(name);

                        //get restaurant location
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        restaurant.setRestaurants_geoPoint(geoPoint);

                        //Calculate restaurant time to get there walking
                        restaurant.setRestaurants_time_distance(getDuration(geoPoint.getLatitude(),geoPoint.getLongitude()));

                        String horario = document.getString("Horario");
                        Log.w("Firebase-Horario", horario);
                        restaurant.setHorario(horario);

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        restaurant.setRestaurants_id(id);
                        restaurants.add(restaurant);

                    }
                    getMenus();
                    addQueues("a");

                }
            }
        }).addOnFailureListener(e -> Log.w("Firebase", e));

    }

    private void getRestaurantsTagusPark() {
        String tcpMessage = "LRALL";
        TCPClient tcpClient = new TCPClient(getApplicationContext(), tcpMessage);
        tcpClient.execute();


        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference restaurantsRef = rootRef.collection("restaurantsTagus");

        restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant = new Restaurant();

                        //get restaurant name
                        String name = document.getString("Name");
                        Log.w("Firebase-Name", name);
                        restaurant.setRestaurants_name(name);

                        //get restaurant location
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        Log.w("Firebase-location", String.valueOf(geoPoint));
                        restaurant.setRestaurants_geoPoint(geoPoint);

                        String horario = document.getString("Horario");
                        Log.w("Firebase-Horario", horario);
                        restaurant.setHorario(horario);



                        //Calculate restaurant time to get there walking
                        restaurant.setRestaurants_time_distance(getDuration(geoPoint.getLatitude(),geoPoint.getLongitude()));

                        //get restaurante id
                        String id = document.getString("r_id");
                        Log.w("Firebase-Id", id);
                        restaurant.setRestaurants_id(id);

                        restaurants.add(restaurant);

                    }

                    //add Queues
                    GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                    String restQueues[] = globalVariable.getAllQueueTimeTagus().split(" ");

                    for(String rest: restQueues){
                        String[] nameTime = rest.split("-");
                        for(Restaurant restaurant: restaurants){
                            if (nameTime[0].equals(restaurant.getRestaurants_id())){
                                restaurant.setQueue(nameTime[1]);
                            }
                        }
                    }

                    getMenus();
                    addQueues("t");
                }

            }
        }).addOnFailureListener(e -> Log.w("Firebase-Tagus", e));

    }

    public Duration getDuration(double lat, double lon){

        try {
            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(new LatLng(38.736987, -9.138705))
                    .destination(new LatLng(lat, lon))
                    .await();

            Log.w("DirectionsResult", String.valueOf(result.routes[0].legs[0].duration));
            return result.routes[0].legs[0].duration;

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void getMenus(){

        String collection =  "restaurantsTagus";
        if(atAlameda){
            collection = "restaurants";
        }
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Log.w("SIZE", String.valueOf(restaurants.size()));
        for(Restaurant restaurant : restaurants){
            CollectionReference restaurantsRef = rootRef.collection(collection).document(restaurant.getRestaurants_id()).collection("Menu");

            restaurantsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        MenuIst menu = new MenuIst();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String name = document.getString("Name");
                            Double price = document.getDouble("Price");
                            ArrayList<String> photos = (ArrayList<String>) document.get("Photos");
                            String doc = document.getId();
                            Dish dish = new Dish(price,name,photos,doc);
                            //Log.w("TESTE", photos.get(0));
                            menu.addDish(dish);
                            GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ae);
                            globalVariable.setBitmapCache(name, bitmap);

                        }
                        restaurant.setMenu(menu);
                    }
                }
            }).addOnFailureListener(e ->Log.w("Problem",e));

        }
        setRecyclerViewRestaurants();
    }

    public void setRecyclerViewRestaurants(){
        // Global Class
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setAtAlameda(atAlameda);
        globalVariable.setRestaurants(restaurants);

        recyclerView = findViewById(R.id.recyclerView);

        if (atAlameda){
            adapter = new FoodISTAdapter(this, image, queueTime, restaurants);
        }
        else{
            adapter = new FoodISTAdapter(this, image, queueTime, restaurants);

        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fadeIn(toolbar);

    }

    //-------------------------------------------------------------------------------------------
    //Termite
    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {

        StringBuilder peersStr = new StringBuilder();
        String beaconName="none";

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            beaconName = device.deviceName;
            peersStr.append(devstr);
        }

        // display list of devices in range
        new android.app.AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

        //If user has a loggin done then it register that the current user is now ate the restaurant
        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String tcpMessage = "ARU"+"-"+user.getUid()+"-"+beaconName+"-"+currentDateTimeString;
            TCPClient tcpClient = new TCPClient(getApplicationContext(), tcpMessage);
            tcpClient.execute();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        mReceiver = new SimpWifip2pBroadCastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimpWifip2pBroadCastReceiver(this);
        registerReceiver(mReceiver, filter);

        super.onResume();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Information: This App uses Wifi Direct.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(FoodISTActivity.this, SimWifiP2pService.class);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                        mBound = true;
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    public static FoodISTActivity  getInstace(){
        return ins;
    }


    public void contactSV(final String t) {
        FoodISTActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mManager.requestPeers(mChannel, FoodISTActivity.this);
            }
        });
    }

    //-------------------------------------------------------------------------------------------

    public void addQueues(String campus){

        if(campus.equals("a")){
            GlobalClass globalVariable = (GlobalClass) getApplicationContext();

            String restQueues[] = globalVariable.getAllQueueTime().split("!")[0].split(" ");

            for(String rest: restQueues){
                String[] nameTime = rest.split("-");
                for(Restaurant restaurant: restaurants){
                    if (nameTime[0].equals(restaurant.getRestaurants_id())){
                        restaurant.setQueue(nameTime[1]);
                    }
                }
            }
        }

        if(campus.equals("t")){
            GlobalClass globalVariable = (GlobalClass) getApplicationContext();

            String restQueues[] = globalVariable.getAllQueueTime().split("!")[1].split(" ");

            Log.w("aquiaqui",globalVariable.getAllQueueTime().split("!")[1]);


            for(String rest: restQueues){
                String[] nameTime = rest.split("-");
                for(Restaurant restaurant: restaurants){
                    if (nameTime[0].equals(restaurant.getRestaurants_id())){
                        Log.w("aqui","aqui");
                        restaurant.setQueue(nameTime[1]);
                    }
                }
            }
        }

    }

}