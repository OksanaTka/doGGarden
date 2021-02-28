package com.example.doggarden.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doggarden.Objects.User;
import com.example.doggarden.utils.MyScreenUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {

    protected boolean isDoublePressToClose = false;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    protected User baseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyScreenUtils.hideSystemUI2(this);

        //get current user to reset the location when turn off the app
        baseUser = new User();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            MyScreenUtils.hideSystemUI2(this);
        }
    }


    @Override
    public void onBackPressed() {
        if (isDoublePressToClose) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {

                //reset the location when turn off the app
                if (baseUser != null) {
                    baseUser.setStatus("offline");
                    baseUser.setLastLat(0);
                    baseUser.setLastLng(0);
                    updateUserDatabase();
                }
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Tap back button again to exit", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }


    private void updateUserDatabase() {
        DatabaseReference myRef = database.getReference("Users").child(baseUser.getUid());
        myRef.setValue(baseUser);
    }
}