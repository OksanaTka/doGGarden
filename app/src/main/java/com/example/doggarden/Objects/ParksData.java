package com.example.doggarden.Objects;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.doggarden.CallBacks.CallBack_UploadParks;
import com.example.doggarden.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParksData {
    private List<Park> parksList = new ArrayList<>();
    private Context context;
    private CallBack_UploadParks callBack_uploadParks;

    public void setCallBack_UploadParks(CallBack_UploadParks callBack_uploadParks) {
        this.callBack_uploadParks = callBack_uploadParks;
    }


    public ParksData() {

        // init the all the parks
        //createParks();
    }

    public ParksData(Context context) {
        this.context = context;

        // init the all the parks
        //createParks();
    }

    public List<Park> getParksList() {
        return parksList;
    }

    public ParksData setParksList(List<Park> parksList) {
        this.parksList = parksList;
        return this;
    }

    public void getParks() {
        DatabaseReference mDatabas = FirebaseDatabase.getInstance().getReference().child("Parks");
        mDatabas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot key : snapshot.getChildren()) {
                    Park park = key.getValue(Park.class);
                    parksList.add(park);
                }
                addParksToMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addParksToMap() {
        callBack_uploadParks.UploadParks(parksList);
    }


    private void createParks() {
        DatabaseReference mDatabas = FirebaseDatabase.getInstance().getReference().child("Parks");
        Park park1 = new Park().setAddress(context.getString(R.string.park_1_address)).setLat(31.963844).setLng(34.775432).setPid("park_1")
                .setWater("yes").setShade("no").setLights("yes").setBenches("3")
                .setName(context.getString(R.string.park_1_name));

        Park park2 = new Park().setAddress(context.getString(R.string.park_2_address)).setLat(32.00199).setLng(34.733456).setPid("park_2")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("1")
                .setName(context.getString(R.string.park_2_name));

        Park park3 = new Park().setAddress(context.getString(R.string.park_3_address)).setLat(31.947294).setLng(34.816872).setPid("park_3")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("1")
                .setName(context.getString(R.string.park_3_name));

        Park park4 = new Park().setAddress(context.getString(R.string.park_4_address)).setLat(31.946297).setLng(34.820428).setPid("park_4")
                .setWater("yes").setShade("no").setLights("no").setBenches("2")
                .setName(context.getString(R.string.park_4_name));

        Park park5 = new Park().setAddress(context.getString(R.string.park_5_address)).setLat(31.968204).setLng(34.789413).setPid("park_5")
                .setWater("yes").setShade("no").setLights("yes").setBenches("5")
                .setName(context.getString(R.string.park_5_name));

        Park park6 = new Park().setAddress(context.getString(R.string.park_6_address)).setLat(31.978185).setLng(34.778971).setPid("park_6")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("1")
                .setName(context.getString(R.string.park_6_name));

        Park park7 = new Park().setAddress(context.getString(R.string.park_7_address)).setLat(31.982044).setLng(34.769945).setPid("park_7")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("2")
                .setName(context.getString(R.string.park_7_name));

        Park park9 = new Park().setAddress(context.getString(R.string.park_9_address)).setLat(31.98027).setLng(34.758626).setPid("park_9")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("2")
                .setName(context.getString(R.string.park_9_name));

        Park park10 = new Park().setAddress(context.getString(R.string.park_10_address)).setLat(31.959836).setLng(34.787417).setPid("park_10")
                .setWater("yes").setShade("no").setLights("yes").setBenches("4")
                .setName(context.getString(R.string.park_10_name));

        Park park11 = new Park().setAddress(context.getString(R.string.park_11_address)).setLat(31.99617).setLng(34.747013).setPid("park_11")
                .setWater("yes").setShade("yes").setLights("yes").setBenches("3")
                .setName(context.getString(R.string.park_11_name));


        mDatabas.child(park1.getPid()).setValue(park1);
        mDatabas.child(park2.getPid()).setValue(park2);
        mDatabas.child(park3.getPid()).setValue(park3);
        mDatabas.child(park4.getPid()).setValue(park4);
        mDatabas.child(park5.getPid()).setValue(park5);
        mDatabas.child(park6.getPid()).setValue(park6);
        mDatabas.child(park7.getPid()).setValue(park7);
        mDatabas.child(park9.getPid()).setValue(park9);
        mDatabas.child(park10.getPid()).setValue(park10);
        mDatabas.child(park11.getPid()).setValue(park11);


    }


}
