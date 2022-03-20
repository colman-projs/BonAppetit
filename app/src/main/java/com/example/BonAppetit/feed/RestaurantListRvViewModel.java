package com.example.BonAppetit.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;

import java.util.List;

public class RestaurantListRvViewModel extends ViewModel {
    LiveData<List<Restaurant>> data;

    public RestaurantListRvViewModel() {
        data = Model.instance.getAll();
    }

    public LiveData<List<Restaurant>> getData() {
        return data;
    }
}
