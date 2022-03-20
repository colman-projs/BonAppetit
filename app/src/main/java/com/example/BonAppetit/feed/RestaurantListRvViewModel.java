package com.example.BonAppetit.feed;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.RestaurantType;

import java.util.List;
import java.util.Set;

public class RestaurantListRvViewModel extends ViewModel {
    private final MutableLiveData<Set<RestaurantType>> filters = new MutableLiveData<>();

    LiveData<List<Restaurant>> data;
    LiveData<List<Restaurant>> filteredList;

    public RestaurantListRvViewModel() {
        data = Model.instance.getAll();
    }

    public LiveData<List<Restaurant>> getData() {
        return data;
    }

    public LiveData<Set<RestaurantType>> getFilters() {
        return filters;
    }

    public void updateFilter(RestaurantType filter) {
        // If filter exists
        
        // Remove Filter

        // Else

        // Add filter
    }
}
