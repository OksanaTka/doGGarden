package com.example.doggarden.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggarden.CallBacks.CallBack_Location;
import com.example.doggarden.CallBacks.CallBack_UploadParks;
import com.example.doggarden.CallBacks.CallBack_showPopUp;
import com.example.doggarden.fragments.Fragment_Map;
import com.example.doggarden.utils.MyLocation;
import com.example.doggarden.Objects.Distance;
import com.example.doggarden.Objects.Rating;
import com.example.doggarden.Objects.Park;
import com.example.doggarden.Objects.ParksData;
import com.example.doggarden.utils.PopUPWindow;
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.example.doggarden.utils.MyDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Activity_Main extends BaseActivity {


    private FrameLayout main_LAY_map;
    private Fragment_Map fragment_map;
    private MaterialButton main_MBTN_my_park;
    private ImageButton main_IBTN_profile;
    private ImageButton main_IBTN_log_out;
    private MyLocation myLocation;
    private ParksData parksData;
    private Park currentPark;
    private MaterialButton main_MBTN_rules;
    private User currentUser;
    private Park park;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private Distance distance;

    private TextView map_LBL_park_name;
    private TextView popup_LBL_online;
    private TextView popup_LBL_rating;
    private TextView popup_LBL_rate_users;
    private TextView popup_LBL_distance;
    private MaterialButton popup_BTN_goto;


    //pop up callback
    private CallBack_showPopUp callBack_showPopUp = new CallBack_showPopUp() {
        @Override
        public void PopUpWindowOnMap(String pid) {
            showPopUpWindowOnMap(pid);
        }
    };


    //get current location callback
    private CallBack_Location callBack_location = new CallBack_Location() {
        @Override
        public void startLocation(double lat, double lng) {
            // check if currentUser is not null and the distance is more than 20 meters from the previous location to update lan ,lng
            if (currentUser.getUid() != null && distance.checkIfDistanceChanged(lat, lng)) {
                currentUser.setLastLat(lat);
                currentUser.setLastLng(lng);
                updateUserDatabase();
            }
            fragment_map.showMarker(lat, lng);
        }
    };

    // upload parks to map callback
    private CallBack_UploadParks callBack_uploadParks = new CallBack_UploadParks() {
        @Override
        public void UploadParks(List<Park> parksList) {
            for (Park park : parksList) {
                fragment_map.addParkMarkers(park.getLat(), park.getLng(), park.getPid());

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isDoublePressToClose=true;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Intent myIntent = new Intent(Activity_Main.this, Activity_Login.class);
            startActivity(myIntent);
            finish();
        } else {
            findViews();
            init();
            locationService();
            addAndUpdateUser();
            showMap();
            readParksAndShowOnMap();

        }
    }

    private void init() {
        currentUser = new User();
        database = FirebaseDatabase.getInstance();
        distance = new Distance();

        //go to user`s favorite park
        main_MBTN_my_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //if currentUser favorite park is not null - get the park and open activity
                if (!currentUser.getFavoritePark().equals("")) {
                    updateParkByPid(currentUser.getFavoritePark(), true);
                } else {
                    Toast.makeText(Activity_Main.this, "No favorite park", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //edit profile
        main_IBTN_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileActivity();
            }
        });

        //open rules dialog
        main_MBTN_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        main_IBTN_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetLocationDetails();
                FirebaseAuth.getInstance().signOut();
                openLoginActivity();
            }
        });
    }

    private void resetLocationDetails(){
        baseUser.setStatus("offline");
        baseUser.setLastLat(0);
        baseUser.setLastLng(0);
        updateUserDatabase();
    }
    private void openLoginActivity() {
        Intent myIntent = new Intent(Activity_Main.this, Activity_Login.class);
        startActivity(myIntent);
        finish();
    }

    //create and show Dialog
    public void openDialog() {
        MyDialog dialog = new MyDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void openEditProfileActivity() {
        Intent myIntent = new Intent(Activity_Main.this, Activity_EditProfile.class);
        startActivity(myIntent);
    }


    //create and show pop up
    // the popup shows marker details on map
    public void showPopUpWindowOnMap(String pid) {
        PopUPWindow mPopupWindow = new PopUPWindow(this);
        updatePopUpView(pid, mPopupWindow.PopUpWindowOnMap());
    }


    private void updatePopUpView(String pid, View popUpView) {
        map_LBL_park_name = popUpView.findViewById(R.id.map_LBL_park_name);
        popup_LBL_online = popUpView.findViewById(R.id.popup_LBL_online);
        popup_LBL_rating = popUpView.findViewById(R.id.popup_LBL_rating);
        popup_LBL_rate_users = popUpView.findViewById(R.id.popup_LBL_rate_users);
        popup_LBL_distance = popUpView.findViewById(R.id.popup_LBL_distance);
         popup_BTN_goto = popUpView.findViewById(R.id.popup_BTN_goto);


         //look for current park
        //init view items
        for (Park park : parksData.getParksList()) {
            if (park.getPid() == pid) {
                currentPark = park;
                setDistanceFromCurrLocation();
                updateParkByPid(currentPark.getPid(), false);
                map_LBL_park_name.setText(currentPark.getName());
                countOnlineUsers();
            }
        }

        //go to button on the popup
        popup_BTN_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get park by pid and open park activity
                updateParkByPid(currentPark.getPid(), true);
            }
        });


    }

    //calc distance from current user location
    private void setDistanceFromCurrLocation() {
        if(currentPark.getLat() != 0.0 && currentPark.getLng() !=0.0){
            int dist = distance.calcDistance(currentPark.getLat(), currentPark.getLng());
            popup_LBL_distance.setText("" + dist + "m");
        }
    }


    //count online users in current park and update popup view
    private void countOnlineUsers() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numOfOnlineUsers = 0;
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    User user = key.getValue(User.class);
                    if (currentPark.getUsersUidList().contains(user.getUid()) && user.getStatus().equals("online")) {
                        numOfOnlineUsers++;
                    }
                }
                popup_LBL_online.setText("" + numOfOnlineUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void openParkActivity() {
        Intent myIntent = new Intent(this, Activity_Park.class);
        myIntent.putExtra("PARK", currentPark);
        startActivity(myIntent);
        finish();

    }


    private void readParksAndShowOnMap() {
        parksData = new ParksData(this);
        parksData.getParks();
        parksData.setCallBack_UploadParks(callBack_uploadParks);
    }


    private void showMap() {
        //show map
        fragment_map = new Fragment_Map();
        getSupportFragmentManager().beginTransaction().add(R.id.main_LAY_map, fragment_map).commit();
        fragment_map.setCallBack_showPopUp(callBack_showPopUp);
    }


    private void locationService() {
        myLocation = new MyLocation(this);
        myLocation.setCallBack_location(callBack_location);
    }

    private void updateUserDatabase() {
        myRef = database.getReference("Users").child(currentUser.getUid());
        myRef.setValue(currentUser);
    }


    private void addAndUpdateUser() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        myRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    addNewUser();
                }
                currentUser = dataSnapshot.getValue(User.class);
                baseUser = currentUser;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void updateParkByPid(String pid, boolean openParkActivity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parks");

        myRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                park = dataSnapshot.getValue(Park.class);

                if (openParkActivity) {
                    currentPark = park;
                    openParkActivity();
                } else {
                    getRatingByPid(park.getPid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    // get current park rating and update popup view
    private void getRatingByPid(String pid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Rating");

        myRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Rating rating = dataSnapshot.getValue(Rating.class);
                    popup_LBL_rate_users.setText("" + rating.getTotNumOfRates());
                    popup_LBL_rating.setText("" + rating.getTotRating());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void addNewUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        User user = new User()
                .setUid(firebaseAuth.getUid())
                .setStatus("offline");
        baseUser = user;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        myRef.child(user.getUid()).setValue(user);
    }


    private void findViews() {
        main_LAY_map = findViewById(R.id.main_LAY_map);
        main_MBTN_rules = findViewById(R.id.main_MBTN_rules);
        main_MBTN_my_park = findViewById(R.id.main_MBTN_my_park);
        main_IBTN_profile = findViewById(R.id.main_IBTN_profile);
        main_IBTN_log_out = findViewById(R.id.main_IBTN_log_out);
    }


    //Request for location Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == myLocation.PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocation.getLocation();
            }
        } else {
            myLocation.setLocationAccepted(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        parksData.setCallBack_UploadParks(callBack_uploadParks);
    }

    @Override
    protected void onStart() {
        super.onStart();
        readParksAndShowOnMap();
    }

}