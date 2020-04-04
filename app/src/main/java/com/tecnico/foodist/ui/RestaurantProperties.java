/*package com.tecnico.foodist.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tecnico.foodist.R;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//Nome - Horario - Queue Time - Distancia
public class RestaurantProperties extends RecyclerView.Adapter<RestaurantProperties.ViewHolder>{

    private ArrayList<String> propName;

    public RestaurantProperties(ArrayList<String> propName, Context mContext) {
        this.propName = propName;
        this.mContext = mContext;
    }

    private Context mContext;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_restaurant_element, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView.setText(propName.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 4){
                    Toast.makeText(mContext, propName.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return propName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.TitleName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}*/
