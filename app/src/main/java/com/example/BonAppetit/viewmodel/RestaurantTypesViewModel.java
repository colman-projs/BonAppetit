package com.example.BonAppetit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.RestaurantType;

import java.util.List;

public class RestaurantTypesViewModel extends ViewModel {
    LiveData<List<RestaurantType>> data;

    public RestaurantTypesViewModel() {
        data = Model.instance.getAllTypes();
    }

    public LiveData<List<RestaurantType>> getData() {
        return data;
    }
}
