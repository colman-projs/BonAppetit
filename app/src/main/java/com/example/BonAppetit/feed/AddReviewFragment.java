package com.example.BonAppetit.feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddReviewFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    ImageView imageImv;
    EditText descEt;
    RatingBar rateRb;

    Button saveBtn;
    Button cancelBtn;
    Button deleteBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageButton camBtn;
    ImageButton galleryBtn;

    boolean imageChanged = false;

    String restaurantId;

    Review review;
    Restaurant restaurant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        restaurantId = AddReviewFragmentArgs.fromBundle(getArguments()).getRestaurantId();
        Log.d("TAG", "ADD review to " + restaurantId);
        Model.instance.getRestaurantById(restaurantId, restaurant -> {
            this.restaurant = restaurant;
        });

        View view = inflater.inflate(R.layout.fragment_add_review, container, false);
        imageImv = view.findViewById(R.id.main_image_imv);
        descEt = view.findViewById(R.id.review_description_et);
        rateRb = view.findViewById(R.id.add_review_rate_bar);
        saveBtn = view.findViewById(R.id.main_save_btn);
        cancelBtn = view.findViewById(R.id.main_cancel_btn);
        deleteBtn = view.findViewById(R.id.review_delete_btn);
        progressBar = view.findViewById(R.id.main_progressbar);

        progressBar.setVisibility(View.VISIBLE);
        Model.instance.getReviewByUserRestaurant(FirebaseAuth.getInstance().getCurrentUser().getUid(), restaurantId, review -> {
            if (review != null) {
                this.review = review;
                descEt.setText(review.getDescription());
                rateRb.setRating(review.getRating());

                if (review.getImageUrl() != null) {
                    Picasso.get()
                            .load(review.getImageUrl())
                            .into(imageImv);
                }

                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }

            progressBar.setVisibility(View.GONE);
        });

        saveBtn.setOnClickListener(v -> save());
        deleteBtn.setOnClickListener(v -> delete());
        cancelBtn.setOnClickListener(v -> {
            Navigation.findNavController(descEt).navigateUp();
        });

        camBtn = view.findViewById(R.id.main_cam_btn);
        galleryBtn = view.findViewById(R.id.main_gallery_btn);

        camBtn.setOnClickListener(v -> openCam());

        galleryBtn.setOnClickListener(v -> openGallery());
        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_GALLERY);
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA){
            if (resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageImv.setImageBitmap(imageBitmap);

            }
        } else if (requestCode == REQUEST_GALLERY){
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                imageBitmap  = BitmapFactory.decodeStream(inputStream);
                imageImv.setImageBitmap(imageBitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        imageChanged = true;
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String description = descEt.getText().toString();
        int rating = (int)rateRb.getRating();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (this.review == null) {
            Review review = new Review(restaurantId, userId, description, rating);
            Model.instance.addRestaurantReview(review, () -> {
                if (restaurant != null) {
                    int newReviewCount = restaurant.getReviewsCount() + 1;
                    double avgRating = restaurant.getAvgRate();
                    restaurant.setAvgRate(avgRating + ((rating - avgRating)  / newReviewCount));
                    restaurant.setReviewsCount(newReviewCount);
                    Model.instance.updateRestaurant(restaurant, () -> {});
                }

                if (imageBitmap != null) {
                    Model.instance.saveReviewImage(imageBitmap, review.getId() + ".jpg", url -> {
                        review.setImageUrl(url);
                        Model.instance.updateReview(review, () ->
                                Navigation.findNavController(descEt).navigateUp()
                        );
                    });
                } else {
                    Navigation.findNavController(descEt).navigateUp();
                }
            });
        } else {
            int reviewCount = restaurant.getReviewsCount();
            double newAvgRate = ((restaurant.getAvgRate() * reviewCount) - this.review.getRating() + rating) / reviewCount;
            this.review.setDescription(description);
            this.review.setRating(rating);
            Model.instance.updateReview(this.review, () -> {
                if (restaurant != null) {
                    restaurant.setAvgRate(newAvgRate);
                    Model.instance.updateRestaurant(restaurant, () -> {});
                }

                if (imageChanged) {
                    if (imageBitmap != null) {
                        Model.instance.saveReviewImage(imageBitmap, review.getId() + ".jpg", url -> {
                            this.review.setImageUrl(url);
                            Model.instance.updateReview(this.review, () ->
                                    Navigation.findNavController(descEt).navigateUp()
                            );
                        });
                    } else {
                        Navigation.findNavController(descEt).navigateUp();
                    }
                } else {
                    Navigation.findNavController(descEt).navigateUp();
                }
            });
        }
    }

    private void delete() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        Model.instance.deleteReview(review, () -> {
            if (restaurant != null) {
                int newReviewCount = restaurant.getReviewsCount() - 1;
                double avgRating = restaurant.getAvgRate();
                restaurant.setAvgRate(newReviewCount == 0 ? 0 : avgRating + ((avgRating - this.review.getRating()) / newReviewCount));
                restaurant.setReviewsCount(newReviewCount);
                Model.instance.updateRestaurant(restaurant, () -> {});
            }

            Navigation.findNavController(descEt).navigateUp();
        });
    }
}