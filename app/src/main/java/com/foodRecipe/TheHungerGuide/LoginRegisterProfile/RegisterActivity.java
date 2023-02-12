package com.foodRecipe.TheHungerGuide.LoginRegisterProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.foodRecipe.TheHungerGuide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword;
    String name, email, username, password;
    Button signupButton;
    ImageView profileImage;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String imageUrl;
    Uri uri;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        profileImage = findViewById(R.id.profile_register_user);
        ImageView leftIcon = (ImageView)findViewById(R.id.leftIcon);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, StartActivity.class));
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("מעלה משתמש...");

                name = signupName.getText().toString();
                email = signupEmail.getText().toString();
                username = signupUsername.getText().toString();
                password = signupPassword.getText().toString();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                Query checkUserDatabase = reference.orderByChild("Profile/username").equalTo(username);

                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            signupUsername.setError("שם משתמש קיים");
                            signupUsername.requestFocus();
                        }
                        else {
                            pd.show();
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReference().child("ProfileImage").child(uri.getLastPathSegment());


                            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isComplete()) ;
                                    imageUrl = uriTask.getResult().toString();
                                    uploadUser();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public  void uploadUser () {

        ProfileHolder helperClass = new ProfileHolder(name, email, username, password, imageUrl);
        FirebaseDatabase.getInstance().getReference("users")
                .child(username).child("Profile").setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "ההרשמה בוצעה בהצלחה!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, login_activity.class);
                            startActivity(intent);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
            profileImage.setImageURI(uri);

        } else Toast.makeText(this, "לא נבחרה תמונה", Toast.LENGTH_SHORT).show();

    }

}