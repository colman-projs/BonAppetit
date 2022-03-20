package com.example.BonAppetit.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.RestaurantType;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListRvViewModel extends ViewModel {
    private final ArrayList<RestaurantType> filters = new ArrayList<>();

    LiveData<List<Restaurant>> data;

    public RestaurantListRvViewModel() {
        data = Model.instance.getAll();
    }

    public LiveData<List<Restaurant>> getData() {
        ArrayList<Restaurant> filteredRestaurants = new ArrayList<>();

        if (data.getValue() != null) {
            for (Restaurant restaurant : data.getValue()) {
                for (RestaurantType type : filters) {
                    if (type.isChecked() && restaurant.getRestaurantTypeId() == type.getId()) {
                        filteredRestaurants.add(restaurant);
                    }
                }
            }
        }

//        data = restaurants;

        return data;
    }

    public ArrayList<RestaurantType> getFilters() {
        return filters;
    }

    public void updateFilter(RestaurantType filter) {
        boolean isExists = false;
        for (RestaurantType type : filters) {
            if (type.getId() == filter.getId()) {
                isExists = true;
            }
        }

        if (isExists) {
            // Add filter to filters
        } else {
            filters.add(filter);
        }
    }
}
