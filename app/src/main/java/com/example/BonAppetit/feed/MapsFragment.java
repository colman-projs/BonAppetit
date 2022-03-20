package com.example.BonAppetit.feed;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;


import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.example.BonAppetit.R;

import com.example.BonAppetit.model.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private View view;
    private GoogleMap map;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private RestaurantListRvViewModel viewModel;
    private LiveData<List<Restaurant>> liveData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(RestaurantListRvViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        liveData = viewModel.getData();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(this);
        setCurrentLocation();

        liveData.observe(getViewLifecycleOwner() , (restaurants) -> {
            addMarkers(restaurants);
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Restaurant restaurant = (Restaurant) marker.getTag();

//        if (restaurant != null) {
//            NavDirections direction = ProblemsOnMapFragmentDirections.actionProblemsOnMapFragmentToProblemDetailsFragment(problem);
//            Navigation.findNavController(view).navigate(direction);
//        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            map.setMyLocationEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(true);
                            moveMap(location.getLatitude(), location.getLongitude());
                        }

                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void moveMap(double latitude, double longitude) {
        if (map != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void addMarkers(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            map.addMarker(new MarkerOptions().position(latLng)
                    .title(restaurant.getName())
                    .snippet(restaurant.getDescription()))
                    .setTag(restaurant.getId());
        }
    }
}