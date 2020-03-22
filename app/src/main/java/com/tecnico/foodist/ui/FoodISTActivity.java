package com.tecnico.foodist.ui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.tecnico.foodist.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class FoodISTActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mChatMessageRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foodist);
        mChatMessageRecyclerView = findViewById(R.id.chatmessage_recycler_view);

        inflateUserListFragment();

        //get Campus
        Bundle bundle = getIntent().getExtras();
        Boolean atAlameda = bundle.getBoolean("atAlameda");
        Boolean atTagus = bundle.getBoolean("atTagus");

        Log.d("alameda", String.valueOf(atAlameda));


    }

    private void inflateUserListFragment(){
        hideSoftKeyboard();
        MapViewFragment fragment = MapViewFragment.newInstance();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list));
        transaction.addToBackStack(getString(R.string.fragment_user_list));
        transaction.commit();

       }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    @Override
    public void onClick(View v) {

    }




}
