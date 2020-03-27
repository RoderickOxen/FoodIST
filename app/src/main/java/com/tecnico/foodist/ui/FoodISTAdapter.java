package com.tecnico.foodist.ui;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecnico.foodist.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FoodISTAdapter extends RecyclerView.Adapter<FoodISTAdapter.ViewHolder> {

    String data1[];
    int images;
    Context context;
    String distance;
    String queueTime;

    public FoodISTAdapter(Context ct, String s1[], int img, String dist , String queue){
        context =ct;
        data1 = s1;
        images = img;
        distance = dist;
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
        holder.textView.setText(data1[position]);
        holder.imageView.setImageResource(images);
        holder.textqueue.setText(queueTime);
        holder.textDistance.setText(distance);

        holder.theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //REVER ESTA MERDA BASICAMENTE VAMOS TER DE TER UM IF CONSOANTE O CLICK DO RESTAURANTE
                Intent intent = new Intent(context, RestaurantActivity.class);
                //intent.putExtra("data1", data1[position]);
                //intent.putExtra("image", images);
                //intent.putExtra("distance", distance);
                //intent.putExtra("queueTime", queueTime);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return data1.length;
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
            //Log.i("LAYOUT",theLayout +"-----------------");
        }
    }
}
