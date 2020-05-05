package com.tecnico.foodist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tecnico.foodist.MainActivity;
import com.tecnico.foodist.R;
import com.tecnico.foodist.models.Dish;
import com.tecnico.foodist.models.Restaurant;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class DishDetailsActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    private String currentPhotoPath;

    TextView nameDish;
    TextView priceDish;
    Button addPhoto;
    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    Uri imageUri;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_details);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        nameDish = findViewById(R.id.textDish);
        priceDish =  findViewById(R.id.textPrice);
        nameDish.setText(globalVariable.getCurrentDish().getName());
        String s = String.valueOf(globalVariable.getCurrentDish().getPrice());
        priceDish.setText(s);

        addPhoto = findViewById(R.id.addPhotoButton);


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalClass globalVariable = (GlobalClass) getApplicationContext();

                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();

                    imageUri = FileProvider.getUriForFile(DishDetailsActivity.this,"com.tecnico.foodist.fileprovider",imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        recyclerView = findViewById(R.id.photosRecycler);
        DishDetailsAdapter adapter = new DishDetailsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap photo = BitmapFactory.decodeFile(currentPhotoPath);

            firebaseUpdate(photo);

            //ImageView ivShowImage = findViewById(R.id.imageView3);
            //ivShowImage.setImageBitmap(photo);
        }
    }

    public void firebaseUpdate(Bitmap bitmap){
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();

        String collection =  "restaurantsTagus";
        if(globalVariable.isAtAlameda()){
            collection = "restaurants";
        }

        Log.d("Path   ", collection +"  " + globalVariable.getCurrentRestaurant() + "  " + "Menu");
        // Add a new document with a generated ID
        DocumentReference  restaurantsRef = rootRef.collection(collection).document(globalVariable.getCurrentRestaurant()).collection("Menu").document(globalVariable.getCurrentDish().getDocument());

        Random rand = new Random();
        String name = "p_" + globalVariable.getCurrentDish().getName() + rand.nextInt(1000);
        restaurantsRef.update("Photos", FieldValue.arrayUnion(name))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DishDetailFirebase ", "DocumentSnapshot successfully updated!");
                        globalVariable.getCurrentDish().addPhotos(name);
                        ArrayList<Restaurant> rest = globalVariable.getRestaurants();
                        for(Restaurant restaurant : rest) {
                            if(restaurant.getRestaurants_id() == globalVariable.getCurrentRestaurant()) {
                                for (Dish dish : restaurant.getMenu().getDishes()){
                                    if(dish.getName()== globalVariable.getCurrentDish().getName()){
                                        dish.addPhotos(name);
                                        break;
                                    }
                                }
                            }
                        }
                        globalVariable.setRestaurants(rest);
                        globalVariable.setBitmapCache(name,bitmap);

                        addPhotoStorage(bitmap,name);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("base", "Error adding document", e);
                    }
                });
    }

    public void addPhotoStorage(Bitmap bitmap, String name){
        StorageReference imgReference = mStorageRef
                .child("restaurantsProfilesPictures")  //image folder
                .child(name + ".jpg");
        imgReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.w("DishDetails Storage", "Sucess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.w("DishDetails Storage ", "Error");
                    }
                });

    }
}
