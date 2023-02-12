package com.foodRecipe.TheHungerGuide.UploadUpdateRecipe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.foodRecipe.TheHungerGuide.Category.MainActivityDessert;
import com.foodRecipe.TheHungerGuide.Category.MainActivityExtras;
import com.foodRecipe.TheHungerGuide.Category.MainActivityMain;
import com.foodRecipe.TheHungerGuide.Category.MainActivityBreakfast;
import com.foodRecipe.TheHungerGuide.Category.category_recipe;
import com.foodRecipe.TheHungerGuide.DisplayRecipe.FoodData;
import com.foodRecipe.TheHungerGuide.LoginRegisterProfile.myProfilePage;
import com.foodRecipe.TheHungerGuide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

public class Update_Recipe extends AppCompatActivity {

    ImageView recipeImage;
    Uri uri;
    EditText txt_name, txt_ingredients, txt_preparation, txt_time;
    String imageUrl;
    String key, oldImageUrl, category;
    ImageView homeBtn, profileBtn;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String recipeName, recipeIngredients, recipePreparation, recipeTime;
    String nameUser, emailUser, usernameUser, passwordUser, profileImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        recipeImage = (ImageView) findViewById(R.id.iv_foodImage);
        txt_name = (EditText) findViewById(R.id.txt_recipe_name);
        txt_ingredients = (EditText) findViewById(R.id.text_ingredients);
        txt_preparation = (EditText) findViewById(R.id.text_preparation);
        txt_time = (EditText) findViewById(R.id.text_time);
        ImageView leftIcon = (ImageView) findViewById(R.id.btnBack);
        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        profileBtn = (ImageView)findViewById(R.id.profileBtn);


        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Update_Recipe.this, category_recipe.class);

                intent.putExtra("name", nameUser);
                intent.putExtra("email", emailUser);
                intent.putExtra("username", usernameUser);
                intent.putExtra("password", passwordUser);
                intent.putExtra("profileImage", profileImage);

                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Update_Recipe.this, myProfilePage.class);

                intent.putExtra("name", nameUser);
                intent.putExtra("email", emailUser);
                intent.putExtra("username", usernameUser);
                intent.putExtra("password", passwordUser);
                intent.putExtra("profileImage", profileImage);

                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();

        Glide.with(Update_Recipe.this)
                .load(bundle.getString("oldImageUrl"))
                .into(recipeImage);
        txt_name.setText(bundle.getString("recipeNameKey"));
        txt_ingredients.setText(bundle.getString("ingredientsKey"));
        txt_preparation.setText(bundle.getString("preparationKey"));
        txt_time.setText(bundle.getString("timeKey"));
        key = bundle.getString("Key");
        oldImageUrl = bundle.getString("oldImageUrl");
        category = bundle.getString("category");
        nameUser = bundle.getString("name");
        emailUser = bundle.getString("email");
        usernameUser = bundle.getString("username");
        passwordUser = bundle.getString("password");
        profileImage = bundle.getString("profileImage");

        switch (category) {
            case "breakfast":
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(usernameUser).child("RecipeStarter").child(key);
                break;
            case "mainCourse":
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(usernameUser).child("RecipeMain").child(key);
                break;
            case "extras":
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(usernameUser).child("RecipeDrinks").child(key);
                break;
            case "dessert":
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(usernameUser).child("dessertRecipe").child(key);
                break;
        }
        //System.out.println(databaseReference);
    }


    public void btnUpdateImage(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            uri = data.getData();
            recipeImage.setImageURI(uri);

        } else Toast.makeText(this, "לא נבחרה תמונה", Toast.LENGTH_SHORT).show();

    }


    public void buttonUpdateRecipe(View view) {

        recipeName = txt_name.getText().toString().trim();
        recipeIngredients = txt_ingredients.getText().toString().trim();
        recipePreparation = txt_preparation.getText().toString().trim();
        recipeTime = txt_time.getText().toString();


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מעדכן מתכון...");
        progressDialog.show();


        storageReference = FirebaseStorage.getInstance()
                .getReference().child("RecipeImage").child(uri.getLastPathSegment().toLowerCase(Locale.ROOT));

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateRecipe();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });


    }

    public void updateRecipe() {

        FoodData foodData = new FoodData(
                recipeName,
                recipeIngredients,
                recipePreparation,
                recipeTime,
                imageUrl,
                category
        );

        databaseReference.setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                StorageReference storageReferenceNew = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                storageReferenceNew.delete();
                Toast.makeText(Update_Recipe.this, "המידע עודכן", Toast.LENGTH_SHORT).show();

                switch (category) {
                    case "breakfast":
                        startActivity(new Intent(Update_Recipe.this, MainActivityBreakfast.class)
                                .putExtra("name", nameUser)
                                .putExtra("email", emailUser)
                                .putExtra("username", usernameUser)
                                .putExtra("password", passwordUser)
                                .putExtra("profileImage", profileImage));
                        break;
                    case "mainCourse":
                        startActivity(new Intent(Update_Recipe.this, MainActivityMain.class)
                                .putExtra("name", nameUser)
                                .putExtra("email", emailUser)
                                .putExtra("username", usernameUser)
                                .putExtra("password", passwordUser)
                                .putExtra("profileImage", profileImage));
                        break;
                    case "extras":
                        startActivity(new Intent(Update_Recipe.this, MainActivityExtras.class)
                                .putExtra("name", nameUser)
                                .putExtra("email", emailUser)
                                .putExtra("username", usernameUser)
                                .putExtra("password", passwordUser)
                                .putExtra("profileImage", profileImage));
                        break;
                    case "dessert":
                        startActivity(new Intent(Update_Recipe.this, MainActivityDessert.class)
                                .putExtra("name", nameUser)
                                .putExtra("email", emailUser)
                                .putExtra("username", usernameUser)
                                .putExtra("password", passwordUser)
                                .putExtra("profileImage", profileImage));
                        break;
                }

            }
        });
    }
}



