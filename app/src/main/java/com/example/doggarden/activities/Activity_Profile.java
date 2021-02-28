package com.example.doggarden.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doggarden.Objects.Park;
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.example.doggarden.utils.MyDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Profile extends BaseActivity {
    private static String USER_PROFILE = "USER_PROFILE";

    private User user;
    private Park newActivityPark;
    private TextView profile_LBL_owner_about;
    private TextView profile_LBL_owner_gender;
    private TextView profile_LBL_breed;
    private TextView profile_LBL_owner_age;
    private TextView profile_LBL_owner_name;
    private TextView profile_LBL_dog_name;
    private ShapeableImageView profile_IMG_user;

    private ImageButton park_NBTN_park;
    private ImageButton park_NBTN_map;
    private ImageButton park_NBTN_rules;
    private ImageButton profile_BTN_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        isDoublePressToClose = true;

        //get user
        user = (User) getIntent().getSerializableExtra(USER_PROFILE);
        baseUser = user;

        initViews();
        init();
        navigationBar();

    }

    private void init() {
        newActivityPark = new Park();

        profile_LBL_owner_name.setText(user.getOwnerName());
        profile_LBL_dog_name.setText(user.getDogName());
        profile_LBL_owner_about.setText(user.getAbout());
        profile_LBL_owner_gender.setText(user.getDogGender());
        profile_LBL_breed.setText(user.getBreed());
        profile_LBL_owner_age.setText(user.getAge());
        showImage();

        //back button
        profile_BTN_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showImage() {
        if (user.getImageUrl() != null) {
            Glide.with(this /* context */)
                    .load(user.getImageUrl())
                    .into(profile_IMG_user);
        }
    }

    private void initViews() {
        profile_LBL_owner_about = findViewById(R.id.profile_LBL_owner_about);
        profile_LBL_owner_gender = findViewById(R.id.profile_LBL_owner_gender);
        profile_LBL_breed = findViewById(R.id.profile_LBL_breed);
        profile_LBL_owner_age = findViewById(R.id.profile_LBL_owner_age);
        profile_LBL_owner_name = findViewById(R.id.profile_LBL_owner_name);
        profile_LBL_dog_name = findViewById(R.id.profile_LBL_dog_name);
        profile_IMG_user = findViewById(R.id.profile_IMG_user);
        park_NBTN_map = findViewById(R.id.park_NBTN_map);
        park_NBTN_park = findViewById(R.id.park_NBTN_park);
        park_NBTN_rules = findViewById(R.id.park_NBTN_rules);
        profile_BTN_back = findViewById(R.id.profile_BTN_back);


    }


    private void navigationBar() {
        park_NBTN_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        park_NBTN_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        park_NBTN_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo change to current user
                getParkByPid(user.getFavoritePark());

            }

        });

    }

    //create and show Dialog
    public void openDialog() {
        MyDialog dialog = new MyDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");

    }

    private void openMainActivity() {
        Intent myIntent = new Intent(this, Activity_Main.class);
        startActivity(myIntent);
        finish();
    }


    private void openParkActivity() {
        Intent myIntent = new Intent(this, Activity_Park.class);
        myIntent.putExtra("PARK", newActivityPark);
        startActivity(myIntent);
        finish();

    }


    private void getParkByPid(String pid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parks");
        myRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newActivityPark = dataSnapshot.getValue(Park.class);
                openParkActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}