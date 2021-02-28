package com.example.doggarden.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.doggarden.R;

import java.util.List;

public class PopUPWindow {

    private Activity activity;
    private PopupWindow mPopupWindow;


    public PopUPWindow() {
    }

    public PopUPWindow(Activity activity) {
        this.activity = activity;
    }

    //show marker details
    public View PopUpWindowOnMap() {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup_layout, null);

        mPopupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER, 0, 200);

        return popUpView;
    }

}
