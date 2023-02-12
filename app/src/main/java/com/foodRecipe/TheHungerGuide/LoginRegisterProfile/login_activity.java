package com.foodRecipe.TheHungerGuide.LoginRegisterProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.foodRecipe.TheHungerGuide.R;
import com.foodRecipe.TheHungerGuide.Category.category_recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login_activity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        ImageView leftIcon = (ImageView)findViewById(R.id.leftIcon);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_activity.this, StartActivity.class));
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()){
                } else {
                    pd = new ProgressDialog(login_activity.this);
                    checkUser();
                }
            }
        });

    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("שם משתמש ריק");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("סיסמא ריקה");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("Profile/username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("Profile").child("password").getValue(String.class);

                    if (passwordFromDB.equals(userPassword)) {
                        loginUsername.setError(null);
                        String nameFromDB = snapshot.child(userUsername).child("Profile").child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("Profile").child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("Profile").child("username").getValue(String.class);
                        String imageFromDB = snapshot.child(userUsername).child("Profile").child("profileImage").getValue(String.class);

                        Intent intentCategory = new Intent(login_activity.this, category_recipe.class);

                        intentCategory.putExtra("name", nameFromDB);
                        intentCategory.putExtra("email", emailFromDB);
                        intentCategory.putExtra("username", usernameFromDB);
                        intentCategory.putExtra("password", passwordFromDB);
                        intentCategory.putExtra("profileImage", imageFromDB);
                        startActivity(intentCategory);


                    } else {
                        loginPassword.setError("סיסמא לא נכונה");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("משתמש לא קיים");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
