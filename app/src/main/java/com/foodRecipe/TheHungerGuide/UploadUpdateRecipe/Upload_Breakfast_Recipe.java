package com.foodRecipe.TheHungerGuide.UploadUpdateRecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodRecipe.TheHungerGuide.Category.MainActivityBreakfast;
import com.foodRecipe.TheHungerGuide.DisplayRecipe.FoodData;
import com.foodRecipe.TheHungerGuide.R;
import com.foodRecipe.TheHungerGuide.Category.category_recipe;
import com.foodRecipe.TheHungerGuide.LoginRegisterProfile.myProfilePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class Upload_Breakfast_Recipe extends AppCompatActivity {

    ImageView recipeImage, home, profile;
    Uri uri;
    EditText txt_name, txt_ingredients, txt_preparation, txt_time;
    TextView category;
    String imageUrl;
    String nameUser, emailUser, usernameUser, passwordUser, imageUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_breakfast_recipe);

        recipeImage = (ImageView) findViewById(R.id.iv_foodImage);
        txt_name = (EditText) findViewById(R.id.text_recipe_name);
        txt_ingredients = (EditText) findViewById(R.id.text_ingredients);
        txt_preparation = (EditText) findViewById(R.id.text_preparation);
        txt_time = (EditText) findViewById(R.id.text_time);
        home = findViewById(R.id.homeBtn);
        profile = findViewById(R.id.profileBtn);
        ImageView leftIcon = (ImageView) findViewById(R.id.btnBack);
        category = findViewById(R.id.category);

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");
        imageUser = intent.getStringExtra("profileImage");

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Upload_Breakfast_Recipe.this, category_recipe.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Upload_Breakfast_Recipe.this, myProfilePage.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));

            }
        });


        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void btnSelectImage(View view) {

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

    public void uploadImage() {

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("RecipeImage").child(uri.getLastPathSegment());


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מעלה מתכון...");
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadRecipe();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


    public void btnUploadRecipe(View view) {
        uploadImage();
    }

    public void uploadRecipe() {

        FoodData foodData = new FoodData(
                txt_name.getText().toString(),
                txt_ingredients.getText().toString(),
                txt_preparation.getText().toString(),
                txt_time.getText().toString(),
                imageUrl,
                category.getText().toString()
        );


        String myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());


        FirebaseDatabase.getInstance().getReference("users")
                .child(usernameUser).child("RecipeStarter").child(myCurrentDateTime).setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(Upload_Breakfast_Recipe.this, "המתכון הועלה", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivityBreakfast.class)
                                        .putExtra("name", nameUser)
                                        .putExtra("email", emailUser)
                                        .putExtra("username", usernameUser)
                                        .putExtra("password", passwordUser)
                                        .putExtra("profileImage", imageUser));


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Upload_Breakfast_Recipe.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
    }
}
