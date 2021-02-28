package com.example.doggarden.Objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Rating {

    private String pid = "";
    private int totNumOfRates = 0;
    private int star_1 = 0;
    private int star_2 = 0;
    private int star_3 = 0;
    private int star_4 = 0;
    private int star_5 = 0;
    private int totRating = 0;
    private List<String> userId;


    public Rating() {
        userId = new ArrayList<>();
    }


    public void calcRating() {
        int rates = star_1 * 1 + star_2 * 2 + star_3 * 3 + star_4 * 4 + star_5 * 5;
        if (totNumOfRates != 0) {
            totRating = rates / totNumOfRates;
        }
    }


    public String getPid() {
        return pid;
    }

    public Rating setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public int getTotNumOfRates() {
        return totNumOfRates;
    }

    public Rating setTotNumOfRates(int totNumOfRates) {
        this.totNumOfRates = totNumOfRates;
        return this;
    }

    public int getStar_1() {
        return star_1;
    }

    public Rating setStar_1(int star_1) {
        this.star_1 = star_1;
        return this;
    }

    public int getStar_2() {
        return star_2;
    }

    public Rating setStar_2(int star_2) {
        this.star_2 = star_2;
        return this;
    }

    public int getStar_3() {
        return star_3;
    }

    public Rating setStar_3(int star_3) {
        this.star_3 = star_3;
        return this;
    }

    public int getStar_4() {
        return star_4;
    }

    public Rating setStar_4(int star_4) {
        this.star_4 = star_4;
        return this;
    }

    public int getStar_5() {
        return star_5;
    }

    public Rating setStar_5(int star_5) {
        this.star_5 = star_5;
        return this;
    }

    public int getTotRating() {
        return totRating;
    }

    public Rating setTotRating(int totRating) {
        this.totRating = totRating;
        return this;
    }

    public void setRating(float rating) {
        if (rating == 1) {
            star_1++;
        }
        if (rating == 2) {
            star_2++;
        }
        if (rating == 3) {
            star_3++;
        }
        if (rating == 4) {
            star_4++;
        }
        if (rating == 5) {
            star_5++;
        }

        totNumOfRates++;

    }

    public List<String> getUserId() {
        return userId;
    }

    public Rating setUserId(List<String> userId) {
        this.userId = userId;
        return this;
    }

    public void addUserToRatingList(String uid) {
        if (!userId.contains(uid)) {
            userId.add(uid);
        }
    }

    public boolean checkIfUserExist(String uid) {
        if (!userId.isEmpty() && userId.contains(uid)) {

            return true;
        }

        return false;
    }
}
