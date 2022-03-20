package com.example.BonAppetit.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Review;

import java.util.List;

public class RestaurantReviewsViewModel extends ViewModel {
    LiveData<List<Review>> data;

    public RestaurantReviewsViewModel() {
        data = Model.instance.getAllReviews();
    }

    public LiveData<List<Review>> getData() {
        return data;
    }
}