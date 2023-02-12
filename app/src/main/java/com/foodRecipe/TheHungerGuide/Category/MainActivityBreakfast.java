package com.foodRecipe.TheHungerGuide.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.foodRecipe.TheHungerGuide.DisplayRecipe.FoodData;
import com.foodRecipe.TheHungerGuide.DisplayRecipe.MyAdapter;
import com.foodRecipe.TheHungerGuide.R;
import com.foodRecipe.TheHungerGuide.UploadUpdateRecipe.Upload_Breakfast_Recipe;
import com.foodRecipe.TheHungerGuide.LoginRegisterProfile.myProfilePage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivityBreakfast extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<FoodData> myFoodList;
    ProgressDialog progressDialog;
    MyAdapter myAdapter;
    EditText txt_Search;
    String nameUser, emailUser, usernameUser, passwordUser, imageUser;
    ImageView homeBtn, profileBtn,uploadRecipeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_breakfast);

        homeBtn = (ImageView)findViewById(R.id.homeBtn);
        profileBtn = (ImageView)findViewById(R.id.profileBtn);
        uploadRecipeBtn = (ImageView)findViewById(R.id.uploadRecipeBtn);


        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        usernameUser = intent.getStringExtra("username");
        passwordUser = intent.getStringExtra("password");
        imageUser = intent.getStringExtra("profileImage");


        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityBreakfast.this, category_recipe.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityBreakfast.this, myProfilePage.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));
            }
        });

        uploadRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityBreakfast.this, Upload_Breakfast_Recipe.class)
                        .putExtra("name", nameUser)
                        .putExtra("email", emailUser)
                        .putExtra("username", usernameUser)
                        .putExtra("password", passwordUser)
                        .putExtra("profileImage", imageUser));
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StartActivity.this, RecyclerView.VERTICAL,false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivityBreakfast.this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        txt_Search = (EditText)findViewById(R.id.txt_searchText);

        ImageView leftIcon = (ImageView)findViewById(R.id.btnBack);


        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items ....");

        myFoodList = new ArrayList<>();

        myAdapter  = new MyAdapter(MainActivityBreakfast.this,myFoodList,nameUser, emailUser, usernameUser, passwordUser, imageUser);
        mRecyclerView.setAdapter(myAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(usernameUser).child("RecipeStarter");

        progressDialog.show();
        ValueEventListener eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myFoodList.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                    FoodData foodData = itemSnapshot.getValue(FoodData.class);
                    if (foodData != null) {
                        foodData.setKey(itemSnapshot.getKey());
                    }
                    myFoodList.add(foodData);

                }

                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });



        txt_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());

            }
        });



    }
    private void filter(String text) {
        ArrayList<FoodData> filterList = new ArrayList<>();
        for(FoodData item: myFoodList){
            if((item.getItemName().toLowerCase()).contains(text.toLowerCase())){
                filterList.add(item);
            }
        }

        myAdapter.filteredList(filterList);

    }

}
