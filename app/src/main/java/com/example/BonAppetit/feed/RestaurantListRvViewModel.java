package com.example.BonAppetit.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.RestaurantType;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListRvViewModel extends ViewModel {
    ArrayList<RestaurantType> filters = new ArrayList<>();
    LiveData<List<Restaurant>> data;

    public RestaurantListRvViewModel() {
//        data = Model.instance.getAll();
    }

    public LiveData<List<Restaurant>> getData() {
        if (filters.size() > 0) {
            ArrayList<String> types = new ArrayList<>();

            for (RestaurantType filter : filters) {
                if(filter.isChecked()) types.add(filter.getId());
            }

            data = Model.instance.getRestaurantsByTypes(types);
        } else {
            data = Model.instance.getAll();
        }

        return data;
    }

    public ArrayList<RestaurantType> getFilters() {
        return filters;
    }

    public void updateFilter(RestaurantType filter) {
//        filters.removeIf(restaurantType -> filter.getId().equals(restaurantType.getId()));

        filters.add(filter);
    }
}
