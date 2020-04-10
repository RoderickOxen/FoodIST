package com.tecnico.foodist.ui;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.GeoPoint;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;
import com.tecnico.foodist.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FoodISTAdapter extends RecyclerView.Adapter<FoodISTAdapter.ViewHolder> {

    ArrayList<String> rest_names = new ArrayList<String>();
    ArrayList<String> rest_id = new ArrayList<String>();
    ArrayList<Duration> rest_distance_time = new ArrayList<Duration>();
    ArrayList<GeoPoint> rest_location = new ArrayList<GeoPoint>();

    //TO DOO
    /*
    * Imagem do restaurant, scheeduele time, queue time,
    * */


    int images;
    Context context;
    String queueTime;

    public FoodISTAdapter(Context ct, ArrayList<String> id, ArrayList<String> name, int img, ArrayList<Duration> dist , String queue, ArrayList<GeoPoint> location){
        context =ct;
        rest_id = id;
        rest_names = name;
        rest_distance_time = dist;
        rest_location = location;

        //TO DOO
        images = img;
        queueTime = queue;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.foodist_row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textView.setText(rest_names.get(position));
        holder.imageView.setImageAlpha(images);
        holder.textqueue.setText(queueTime);
        holder.textDistance.setText(rest_distance_time.get(position).toString() + " walking");

        holder.theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RestaurantProfileActivity.class);
                intent.putExtra("rest_names", rest_names.get(position));
                intent.putExtra("rest_id", rest_id.get(position));
                intent.putExtra("latitude", rest_location.get(position).getLatitude());
                intent.putExtra("longitude", rest_location.get(position).getLongitude());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rest_names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textDistance;
        TextView textqueue;
        ImageView imageView;
        androidx.constraintlayout.widget.ConstraintLayout theLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textDistance =  itemView.findViewById(R.id.distanceTime);
            textqueue =  itemView.findViewById(R.id.queueTime);
            textView = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.imageView1);
            theLayout = itemView.findViewById(R.id.theLayout);
        }
    }

    public void clear() {
        rest_names.clear();
        rest_distance_time.clear();
        notifyDataSetChanged();
    }
}
