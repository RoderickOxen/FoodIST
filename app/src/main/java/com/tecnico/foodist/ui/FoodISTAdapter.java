package com.tecnico.foodist.ui;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Restaurant;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FoodISTAdapter extends RecyclerView.Adapter<FoodISTAdapter.ViewHolder> {

    ArrayList<Restaurant> restaurants;


    //TO DOO
    /*
     * scheeduele time, queue time,
     * */


    Bitmap images;
    Context context;
    String queueTime;


    public FoodISTAdapter(Context ct, Bitmap img, String queue, ArrayList<Restaurant> r){
        context =ct;
        restaurants = r ;

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
        holder.textView.setText(restaurants.get(position).getRestaurants_name());
        holder.textDistance.setText(restaurants.get(position).getRestaurants_time_distance().toString() + " walking");

        //set restaruant profile picture
        switch(restaurants.get(position).getRestaurants_id()) {
            case "ae":
                holder.imageView.setImageResource(R.drawable.ae);
                break;
            case "civil":
                holder.imageView.setImageResource(R.drawable.civil);
                break;
            case "matematica":
                holder.imageView.setImageResource(R.drawable.matematica);
                break;
            case "mecanica":
                holder.imageView.setImageResource(R.drawable.mecanica);
                break;
            case "central":
                holder.imageView.setImageResource(R.drawable.central);
                break;
            case "quimica":
                holder.imageView.setImageResource(R.drawable.quimica);
                break;
            case "amarelo":
                holder.imageView.setImageResource(R.drawable.amarelo);
                break;
            case "maquinas":
                holder.imageView.setImageResource(R.drawable.maquinas);
                break;
            case "redbar":
                holder.imageView.setImageResource(R.drawable.redbar);
                break;
            case "cantina":
                holder.imageView.setImageResource(R.drawable.cantina);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.buffet1600x800);
        }


        //TO DO
        holder.textqueue.setText(queueTime);


        holder.theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RestaurantProfileActivity.class);
                intent.putExtra("rest_names", restaurants.get(position).getRestaurants_name());
                intent.putExtra("rest_id", restaurants.get(position).getRestaurants_id());
                intent.putExtra("latitude", restaurants.get(position).getRestaurants_geoPoint().getLatitude());
                intent.putExtra("longitude", restaurants.get(position).getRestaurants_geoPoint().getLongitude());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
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
        restaurants.clear();
        notifyDataSetChanged();
    }
}