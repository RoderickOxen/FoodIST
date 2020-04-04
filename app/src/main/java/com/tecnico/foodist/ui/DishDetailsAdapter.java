package com.tecnico.foodist.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tecnico.foodist.R;


public class DishDetailsAdapter extends RecyclerView.Adapter<DishDetailsAdapter.ViewHolder> {

    int images[] = {R.drawable.food900x700, R.drawable.food900x700, R.drawable.food900x700, R.drawable.food900x700};
    Context context;


    public DishDetailsAdapter(Context ct){
        context = ct;
    }
    @NonNull
    @Override
    public DishDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.foodist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishDetailsAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);

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
        Log.i("LAYOUT",images.length+"-----------------");
        if(Math.round((images.length+1)/3) == (images.length+1)/3){
            Log.i("LAYOUT",Math.round((images.length+1)/3) +"-----------------");
            return Math.round((images.length+1)/3);
        }
        else{
            Log.i("LAYOUT",Math.round((images.length+1)/3 - 1/2) + 1 +"-----------------");
            return Math.round((images.length+1)/3 - 1/2) + 1;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        androidx.constraintlayout.widget.ConstraintLayout theLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView1);
            theLayout = itemView.findViewById(R.id.theLayout);
        }
    }
}
