package com.tecnico.foodist.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;

import java.util.ArrayList;


public class DishDetailsAdapter extends RecyclerView.Adapter<DishDetailsAdapter.ViewHolder> {

    //int images[] = {R.drawable.food900x700, R.drawable.food900x700, R.drawable.food900x700, R.drawable.food900x700};
    Context context;
    ArrayList<Bitmap> temp;


    public DishDetailsAdapter(Context ct){
        context = ct;
        temp = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ae);
        temp.add(bitmap);
        temp = firebaseStorage();

    }
    @NonNull
    @Override
    public DishDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dish_details_feed_row, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishDetailsAdapter.ViewHolder holder, int position) {
        Log.i("tamanho do temp", String.valueOf(temp.size()));
        GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
        // isto tem de mudar em breve

        holder.imageView.setImageBitmap(temp.get(position));
        //globalVariable.getBitmapCache(globalVariable.getCurrentDish().getName()));

        holder.theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Dish = ", globalVariable.getCurrentDish().getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return temp.size();
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


    public ArrayList<Bitmap> firebaseStorage(){

        //get the Firebase  storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
        ArrayList<String> allPhotos = globalVariable.getCurrentDish().getPhotos();

        ArrayList<Bitmap> temp = new ArrayList<>();

        for (String photo :  allPhotos ){
            if(globalVariable.getBitmapCache(photo) == null){

                StorageReference imgReference = storage.getReference()
                        .child("restaurantsProfilesPictures")  //image folder
                        .child(photo + ".jpg");

                //download files as bytes
                final long ONE_MEGABYTE = 1024 * 1024;
                imgReference.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                                globalVariable.setBitmapCache(photo,image);
                                temp.add(image);
                                Log.w("Sucess Storage foto =",photo);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("storage",e);
                    }
                });

            }
            else {
                temp.add(globalVariable.getBitmapCache(photo));
            }

        }

        return temp;

    }
}
