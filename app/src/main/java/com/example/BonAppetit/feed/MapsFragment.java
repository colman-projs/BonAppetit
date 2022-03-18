package com.example.BonAppetit.feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.BonAppetit.R;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    // below are the latitude and longitude
    // of 4 different locations.,,
    LatLng carmel_market = new LatLng(32.06964105711388, 34.76896914543392); // carmel market
    LatLng vitrina_tel_aviv = new LatLng(32.062233374685995, 34.77184962122501); // vitrina tel-aviv

    // creating array list for adding all our locations.
    private ArrayList<LatLng> locationArrayList;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            // in below line we are initializing our array list.
            locationArrayList = new ArrayList<>();

            // on below line we are adding our
            // locations in our array list.
            locationArrayList.add(carmel_market);
            locationArrayList.add(vitrina_tel_aviv);
//            locationArrayList.add(NewCastle);
//            locationArrayList.add(Brisbane);

//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            for (int i = 0; i < locationArrayList.size(); i++) {

                // below line is use to add marker to each location of our array list.
                googleMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Marker"));

                // below lin is use to zoom our camera on map.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

                // below line is use to move our camera to the specific location.
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}