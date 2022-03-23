package com.example.BonAppetit.feed;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
    private static final int DEFAULT_ZOOM = 15;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        /*
         * Handle InfoWindowClick click event on MAP -> Navigate to current restaurant
         */
        String restaurantId = (String) marker.getTag();

        if (restaurantId != null) {
            Navigation
                    .findNavController(this.getView())
                    .navigate(MapsFragmentDirections.actionMapsFragmentToRestaurantReviewsFragment(restaurantId));

        }
    }

    private void setCurrentLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. If permission is granted so move map to current location
         */
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            map.setMyLocationEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(true);
                            MoveToCurrentLocation(location.getLatitude(), location.getLongitude());
                        }

                    });
        } else {
            //Request for location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void MoveToCurrentLocation(double latitude, double longitude) {
        /*
         * Move map to current device location
         */
        if (map != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void addMarkers(List<Restaurant> restaurants) {
        /*
         * Add markers in the map , based on restaurants locations
         */
        for (Restaurant restaurant : restaurants) {
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            String snippet = restaurant.getDescription() + " (" + restaurant.getAvgRate().floatValue() + ")";
            map.addMarker(new MarkerOptions().position(latLng)
                    .title(restaurant.getName())
                    .snippet(snippet))
                    .setTag(restaurant.getId());
        }
    }
}