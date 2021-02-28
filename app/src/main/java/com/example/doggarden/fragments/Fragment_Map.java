package com.example.doggarden.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doggarden.CallBacks.CallBack_showPopUp;
import com.example.doggarden.R;
import com.example.doggarden.Objects.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fragment_Map extends Fragment {
    public static GoogleMap mMap;
    private boolean markerChanged = false;
    private boolean zoomOnce = false;
    private Marker marker;
    private Marker prevMarker;


    private CallBack_showPopUp callBack_showPopUp;

    public void setCallBack_showPopUp(CallBack_showPopUp callBack_showPopUp) {
        this.callBack_showPopUp = callBack_showPopUp;
    }


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            showParkDetails();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


        return view;
    }


    //show current user marker
    public void showMarker(double lat, double lng) {
        if (markerChanged == true) {
            marker.remove();
            markerChanged = false;
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng)));
        marker.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerChanged = true;
        if (!zoomOnce) {
            zoomOnMarker(marker);
            zoomOnce = true;

        }

    }

    public void zoomOnMarker(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13f));
    }

    // add park marker on map
    public void addParkMarkers(double lat, double lng, String pid) {
        Marker parkMarker;
        parkMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng)));
        parkMarker.setTag(pid);
    }


    public void showParkDetails() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getTag() != null) {
                    if (prevMarker != null && prevMarker != marker) {
                        prevMarker.setIcon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    prevMarker = marker;
                    marker.setIcon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    zoomOnMarker(marker);
                    callBack_showPopUp.PopUpWindowOnMap((String) marker.getTag());
                    return true;
                }
                return false;
            }
        });

    }

}
