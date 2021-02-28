package com.example.doggarden.Objects;

import android.location.Location;

public class Distance {
    private final int DISTANCE_FROM_PARK = 3000;
    private double user_lat;
    private double user_lng;

    public Distance() {

    }


    //check if user in  the park
    public boolean checkDistance(double park_lat, double park_lng) {
        float[] distance = new float[1];

        Location.distanceBetween(user_lat,
                user_lng, park_lat,
                park_lng, distance);

        if (distance[0] > DISTANCE_FROM_PARK) {
            return false;

        } else if (distance[0] < DISTANCE_FROM_PARK) {
            return true;

        }

        return false;
    }


    //check if the user distance has changed
    public boolean checkIfDistanceChanged(double curr_lat, double cur_lng) {
        float[] distance = new float[1];

        Location.distanceBetween(user_lat,
                user_lng, curr_lat,
                cur_lng, distance);
        if (distance[0] > 20) {
            user_lat = curr_lat;
            user_lng = cur_lng;
            return true;
        }
        return false;
    }

    //calc distance to show on popup view
    public int calcDistance(double curr_lat, double cur_lng) {
        float[] distance = new float[1];

        Location.distanceBetween(user_lat,
                user_lng, curr_lat,
                cur_lng, distance);

        return (int) distance[0];
    }


    public double getUser_lat() {
        return user_lat;
    }

    public Distance setUser_lat(double user_lat) {
        this.user_lat = user_lat;
        return this;
    }

    public double getUser_lng() {
        return user_lng;
    }

    public Distance setUser_lng(double user_lng) {
        this.user_lng = user_lng;
        return this;
    }
}
