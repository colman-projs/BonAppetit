package com.example.BonAppetit.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Review;

import java.util.List;

public class RestaurantReviewsViewModel extends ViewModel {
    LiveData<List<Review>> data;
    String restaurantId = "";

    public RestaurantReviewsViewModel() {
//        data = Model.instance.getAllReviews();
    }

    public void changeRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
        data = Model.instance.getReviewsByRestaurant(restaurantId);
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public LiveData<List<Review>> getData() {
        return data;
    }
}
