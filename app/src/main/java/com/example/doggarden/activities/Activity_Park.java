package com.example.doggarden.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doggarden.Adapters.UserAdapter;
import com.example.doggarden.Objects.Distance;
import com.example.doggarden.Objects.Rating;
import com.example.doggarden.Objects.Park;
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.example.doggarden.utils.MyDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//todo when user exit the app he offline
//todo person has only one dog

public class Activity_Park extends BaseActivity {
    public static final String PARK = "PARK";

    public final int FAVORITE = 1;
    public final int UNFAVORITE = 0;
    public final String STAR_FILL = "ic_star_fill";
    public final String STAR_EMPTY = "ic_star_empty";

    private int parkStatus;
    private ImageButton park_IBTN_chat;
    private FrameLayout park_LAY_details;
    private ImageButton park_MBTN_favorite;
    private Park park, newActivityPark;
    private User currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private TextView park_LBL_status_address;
    private TextView park_LBL_name;
    private TextView park_LBL_features;
    private TextView park_LBL_status_shade;
    private TextView park_LBL_status_lights;
    private TextView park_LBL_status_benches;
    private TextView park_LBL_status_water;
    private RatingBar park_RB_rate;

    private MaterialButton park_MBTN_rate;

    private ImageButton park_NBTN_park;
    private ImageButton park_NBTN_map;
    private ImageButton park_NBTN_rules;
    private Button park_BTN_navigation;

    private RecyclerView list_RV_users;
    private List<User> users;
    private UserAdapter userAdapter;
    private Rating rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        isDoublePressToClose = true;

        //get park
        park = (Park) getIntent().getSerializableExtra(PARK);

        findViews();
        init();
        navigationBar();

        getCurrentUserFromDatabase();

        setAdapter();

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
                // openRulesActivity();
            }
        });
        park_NBTN_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentUser.getFavoritePark().equals("") && !currentUser.getFavoritePark().equals(park.getPid())) {
                    getParkByPid(currentUser.getFavoritePark());
                } else if (currentUser.getFavoritePark().equals(park.getPid())) {
                    Toast.makeText(Activity_Park.this, "You are already in your favorite park", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }


    //create and show Dialog
    public void openDialog() {
        MyDialog dialog = new MyDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");
    }


    private void openGoogleMap() {
        // Creates an Intent that will load a map of San Francisco
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + park.getLat() + "," + park.getLng() + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

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

    private void openProfileActivity(User user) {
        //todo open profile activity
        //todo send user to profile activity
        //todo create profile activity
        Intent myIntent = new Intent(Activity_Park.this, Activity_Profile.class);
        myIntent.putExtra("USER_PROFILE", user);
        startActivity(myIntent);
    }


    private void setAdapter() {
        list_RV_users.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, users);
        list_RV_users.setAdapter(userAdapter);

        userAdapter.setClickListener(new UserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openProfileActivity(users.get(position));
            }
        });
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        rating = new Rating();
        currentUser = new User();

        park_LBL_status_address.setText(park.getAddress());
        park_LBL_name.setText(park.getName());
        park_LBL_status_water.setText(park.getWater());
        park_LBL_status_shade.setText(park.getShade());
        park_LBL_status_lights.setText(park.getLights());
        park_LBL_status_benches.setText(park.getBenches());


        //open chat
        park_IBTN_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatActivity();
            }
        });

        //make the park - favorite
        park_MBTN_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePark();
            }
        });


        //rate the park
        park_MBTN_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!rating.checkIfUserExist(currentUser.getUid())) {
                    getUserRating();
                } else {
                    Toast.makeText(Activity_Park.this, "You have already rated the park !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //navigate to the park
        park_BTN_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMap();
            }
        });


    }

    //get rating
    //calc total rating
    //add user to rating list of the park
    //show rating
    //setIsIndicator = ture ( cant rate the park again)
    private void getUserRating() {
        float rate = park_RB_rate.getRating();
        rating.setRating(rate);
        rating.calcRating();
        park.setRating(rating.getTotRating());
        rating.addUserToRatingList(currentUser.getUid());
        park_RB_rate.setRating(rating.getTotRating());
        park_RB_rate.setIsIndicator(true);

        Toast.makeText(Activity_Park.this, "Thank`s for rating !", Toast.LENGTH_SHORT).show();
        updateRateDatabase();
        updateParkDatabase();
    }


    //user can have only one favorite park
    private void favoritePark() {
        //if user click to FAVORITE park
        if (parkStatus == UNFAVORITE) {
            // if user already has a favorite park
            if (!currentUser.getFavoritePark().equals("")) {
                Toast.makeText(this, "You already have a favorite park", Toast.LENGTH_SHORT).show();
            } else {
                //update favorite park
                userPreference(park.getPid(), STAR_FILL, FAVORITE);
                park.addUserToPark(currentUser.getUid());
                Toast.makeText(this, "You have successfully added the park to favorites", Toast.LENGTH_SHORT).show();
            }
        }
        //if user click to UNFAVORITE park
        else {
            userPreference("", STAR_EMPTY, UNFAVORITE);
            park.removeUserFromPark(currentUser.getUid());
        }
        //update park and user in FirebaseDatabase
        updateUserDatabase(currentUser);
        updateParkDatabase();
    }

    //update user Preference for current park
    private void userPreference(String pid, String img, int status) {
        currentUser.setFavoritePark(pid);
        park_MBTN_favorite.setImageResource(getImage(img));
        parkStatus = status;
    }

    //get img resource
    private int getImage(String imgName) {
        return getResources().getIdentifier(imgName, "drawable", getPackageName());
    }


    private void updateUserDatabase(User user) {
        myRef = database.getReference("Users").child(user.getUid());
        myRef.setValue(user);
    }


    private void updateParkDatabase() {
        myRef = database.getReference("Parks").child(park.getPid());
        myRef.setValue(park);
    }

    private void updateRateDatabase() {
        myRef = database.getReference("Rating").child(park.getPid());
        myRef.setValue(rating);
    }


    private void getCurrentUserFromDatabase() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                baseUser = currentUser;
                checkIfParkIsFavorite();
                getUsersInParkList();
                getRatesFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // check if user already rated the park
    private void checkRate() {
        String uid = currentUser.getUid();
        if (rating.checkIfUserExist(uid)) {
            park_RB_rate.setIsIndicator(true);
        }
    }

    private void getRatesFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Rating").child(park.getPid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    updateRateDatabase();
                } else {
                    rating = snapshot.getValue(Rating.class);
                    checkRate();
                    park_RB_rate.setRating((float) rating.getTotRating());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsersInParkList() {
        database.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    User user = key.getValue(User.class);

                    //init distance with user coordinates
                    Distance distance_temp = new Distance();
                    initDistance(distance_temp, user);

                    //if current park is user`s favorite park - add user to list
                    if (user.getFavoritePark().equals(park.getPid()) && checkIfUserInPark(user) == -1) {
                        updateUsersList(user);

                    }
                    //if the user removed a "favorite " from the park
                    else if (!user.getFavoritePark().equals(park.getPid()) && checkIfUserInPark(user) != -1) {
                        removeUserFromList(user);
                    }
                    //check if user is not at the park
                    if (checkIfUserInPark(user) != -1 && !check_distance(distance_temp) && user.getStatus().equals("online")) {
                        setNewStatus("offline", user);
                        updateUsersStatusList(user);
                    } else if (checkIfUserInPark(user) != -1 && check_distance(distance_temp) && user.getStatus().equals("offline")) {
                        setNewStatus("online", user);
                        updateUsersStatusList(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // check if the user is in the  users list
    private int checkIfUserInPark(User checkUser) {
        for (User user : users) {
            if (user.getUid().equals(checkUser.getUid())) {
                return users.indexOf(user);
            }
        }
        return -1;
    }


    private void removeUserFromList(User user) {
        int id = checkIfUserInPark(user);
        users.remove(id);
        userAdapter.notifyItemRemoved(id);
        userAdapter.notifyItemRangeChanged(id, users.size());
    }

    private void updateUsersList(User user) {
        users.add(user);
        userAdapter.notifyItemChanged(users.size());
    }

    private void updateUsersStatusList(User user) {
        int id = checkIfUserInPark(user);
        users.set(id, user);
        userAdapter.notifyItemChanged(id);
    }


    private void updateCurrentUserStatus() {
        Distance distance = new Distance();
        initDistance(distance, currentUser);

        boolean isNearThePark = check_distance(distance);

        if (isNearThePark) {
            setNewStatus("online", currentUser);
        } else {
            setNewStatus("offline", currentUser);

            Toast.makeText(this, "You not in the park", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDistance(Distance dis, User user) {
        dis.setUser_lat(user.getLastLat())
                .setUser_lng(user.getLastLng());
    }

    private boolean check_distance(Distance dis) {
        return dis.checkDistance(park.getLat(), park.getLng());
    }

    private void setNewStatus(String status, User user) {
        user.setStatus(status);
        updateUserDatabase(user);
    }


    private void checkIfParkIsFavorite() {
        if (currentUser.getFavoritePark().equals(park.getPid())) {
            int img = getResources().getIdentifier("ic_star_fill", "drawable", getPackageName());
            park_MBTN_favorite.setImageResource(img);
            parkStatus = FAVORITE;
        } else {
            parkStatus = UNFAVORITE;
        }
    }


    private void openChatActivity() {
        Intent myIntent = new Intent(this, Activity_Chat.class);
        myIntent.putExtra("PARK_PID", park.getPid());
        startActivity(myIntent);
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


    private void findViews() {
        park_IBTN_chat = findViewById(R.id.park_IBTN_chat);
        park_MBTN_favorite = findViewById(R.id.park_MBTN_favorite);
        park_LAY_details = findViewById(R.id.park_LAY_details);
        list_RV_users = findViewById(R.id.list_RV_users);
        park_LBL_status_address = findViewById(R.id.park_LBL_status_address);
        park_LBL_features = findViewById(R.id.park_LBL_features);
        park_LBL_status_shade = findViewById(R.id.park_LBL_status_shade);
        park_LBL_status_lights = findViewById(R.id.park_LBL_status_lights);
        park_LBL_status_benches = findViewById(R.id.park_LBL_status_benches);
        park_RB_rate = findViewById(R.id.park_RB_rate);
        park_LBL_status_water = findViewById(R.id.park_LBL_status_water);
        park_MBTN_rate = findViewById(R.id.park_MBTN_rate);
        park_LBL_name = findViewById(R.id.park_LBL_name);
        park_NBTN_map = findViewById(R.id.park_NBTN_map);
        park_NBTN_park = findViewById(R.id.park_NBTN_park);
        park_NBTN_rules = findViewById(R.id.park_NBTN_rules);
        park_BTN_navigation = findViewById(R.id.park_BTN_navigation);


    }

}