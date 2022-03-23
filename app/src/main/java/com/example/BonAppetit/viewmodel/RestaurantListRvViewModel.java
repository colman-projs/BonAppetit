package com.example.BonAppetit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantListRvViewModel extends ViewModel {
    LiveData<List<Restaurant>> data;

    public RestaurantListRvViewModel() {
//       data = Model.instance.getAll();
    }

    public LiveData<List<Restaurant>> getData(String typeFilterIds) {
        if (!typeFilterIds.isEmpty()) {
            ArrayList<String> typesList =
                    new ArrayList<String>(Arrays.asList(typeFilterIds.split(",")));
            data = Model.instance.getRestaurantsByTypes(typesList);
        } else {
            data = Model.instance.getAll();
        }

        return data;
    }
}
