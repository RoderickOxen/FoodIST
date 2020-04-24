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
import com.tecnico.foodist.models.Dish;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    int images;
    Context context;
    String queueTime;
    ArrayList<Dish> menu ;

    public MenuAdapter(Context ct, ArrayList<Dish> dishes, int img , String queue){
        context = ct;
        menu = dishes;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Double temp = menu.get(position).getPrice();
        Log.w("Adapter menu",menu.get(position).getName());
        holder.textView.setText(menu.get(position).getName());
        holder.imageView.setImageResource(images);
        holder.textqueue.setText(queueTime);
        holder.textDistance.setText(temp.toString()+ " €");

        holder.theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DishDetailsActivity.class);

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textDistance;
        TextView textqueue;
        ImageView imageView;
        androidx.constraintlayout.widget.ConstraintLayout theLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textDistance = itemView.findViewById(R.id.distanceTime);
            textqueue = itemView.findViewById(R.id.queueTime);
            textView = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.imageView1);
            theLayout = itemView.findViewById(R.id.theLayout);
        }
    }
}
