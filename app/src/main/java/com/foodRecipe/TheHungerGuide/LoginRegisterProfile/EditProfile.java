package com.foodRecipe.TheHungerGuide.LoginRegisterProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.foodRecipe.TheHungerGuide.R;
import com.foodRecipe.TheHungerGuide.Category.category_recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    EditText editName, editEmail, editPassword;
    TextView editUsername;
    CircleImageView profileImage;
    Button saveButton;
    String nameUser, emailUser, usernameUser, passwordUser, imageUser, imageUrl;;
    DatabaseReference reference;
    StorageReference storageReference;
    ImageView homeBtn;
    ImageView btnBack;
    Uri uri, uriImage;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        reference = FirebaseDatabase.getInstance().getReference("users");

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);
        homeBtn = findViewById(R.id.homeBtn);
        btnBack = findViewById(R.id.btnBack);
        profileImage = findViewById(R.id.edit_IMG_user);

        showData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, category_recipe.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(EditProfile.this);
                pd.setMessage("מעדכן את הפרופיל...");
                pd.show();
                updateImageStorage();
            }
        });

    }

    private void updateImageStorage() {
        storageReference = FirebaseStorage.getInstance()
                .getReference().child("ProfileImage").child(uri.getLastPathSegment().toLowerCase(Locale.ROOT));

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                uriImage = uriTask.getResult();
                imageUrl = uriImage.toString();
                updateUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public void showData(){
        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");
        imageUser = intent.getStringExtra("profileImage");

        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUsername.setText(usernameUser);
        editPassword.setText(passwordUser);
        Glide.with(EditProfile.this)
                .load(imageUser)
                .into(profileImage);
    }

    public void btnEditImage(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            profileImage.setImageURI(uri);
        }
        else Toast.makeText(this, "לא בחרת תמונה", Toast.LENGTH_SHORT).show();
    }

    public  void updateUser () {

        nameUser = editName.getText().toString();
        emailUser = editEmail.getText().toString();
        usernameUser = editUsername.getText().toString();
        passwordUser = editPassword.getText().toString();

        ProfileHolder helperClass = new ProfileHolder(nameUser , emailUser, usernameUser, passwordUser, imageUrl);
        FirebaseDatabase.getInstance().getReference("users")
                .child(usernameUser).child("Profile").setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(EditProfile.this, "הפרופיל עודכן", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, myProfilePage.class)
                                    .putExtra("name", nameUser)
                                    .putExtra("email", emailUser)
                                    .putExtra("username", usernameUser)
                                    .putExtra("password", passwordUser)
                                    .putExtra("profileImage", imageUrl));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}