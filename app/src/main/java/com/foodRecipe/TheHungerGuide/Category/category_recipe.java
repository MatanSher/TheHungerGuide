package com.foodRecipe.TheHungerGuide.Category;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.foodRecipe.TheHungerGuide.LoginRegisterProfile.myProfilePage;
import com.foodRecipe.TheHungerGuide.R;

public class category_recipe extends AppCompatActivity {

    ImageView starter, main, desert, drink;
    ImageView homeBtn, profileBtn;
    String nameUser, emailUser, usernameUser, passwordUser, profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_recipe);

        starter = findViewById(R.id.starterId);
        main = findViewById(R.id.mainId);
        desert = findViewById(R.id.desertId);
        drink = findViewById(R.id.drinksId);
        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        profileBtn = (ImageView)findViewById(R.id.profileBtn);
        ImageView leftIcon = (ImageView)findViewById(R.id.leftIcon);

        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");
        profileImage = intent.getStringExtra("profileImage");

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(category_recipe.this, category_recipe.class);

                intent.putExtra("name", nameUser);
                intent.putExtra("email", emailUser);
                intent.putExtra("username", usernameUser);
                intent.putExtra("password", passwordUser);
                intent.putExtra("profileImage", profileImage);

                startActivity(intent);*/
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(category_recipe.this, myProfilePage.class);

                intent.putExtra("name", nameUser);
                intent.putExtra("email", emailUser);
                intent.putExtra("username", usernameUser);
                intent.putExtra("password", passwordUser);
                intent.putExtra("profileImage", profileImage);

                startActivity(intent);
            }
        });


        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        desert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(category_recipe.this, MainActivityDessert.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", profileImage));
            }
        });

        starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(category_recipe.this, MainActivityBreakfast.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", profileImage));
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(category_recipe.this, MainActivityMain.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", profileImage));
            }
        });

        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(category_recipe.this, MainActivityExtras.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", profileImage));
            }
        });


    }
}