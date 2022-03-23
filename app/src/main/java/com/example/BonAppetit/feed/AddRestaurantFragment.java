package com.example.BonAppetit.feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddRestaurantFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    EditText nameEt;
    ImageView imageImv;
    EditText descEt;
    EditText latitudeEt;
    EditText longitudeEt;
    Button saveBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageButton camBtn;
    ImageButton galleryBtn;
    boolean picUploaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_restaurant, container, false);
        nameEt = view.findViewById(R.id.main_name_et);
        descEt = view.findViewById(R.id.main_desc_et);
        latitudeEt = view.findViewById(R.id.main_lat_et);
        longitudeEt = view.findViewById(R.id.main_lng_et);
        saveBtn = view.findViewById(R.id.main_save_btn);
        progressBar = view.findViewById(R.id.main_progressbar);
        progressBar.setVisibility(View.GONE);
        imageImv = view.findViewById(R.id.main_image_imv);

        saveBtn.setOnClickListener(v -> save());
        cancelBtn.setOnClickListener(v -> {
            Navigation.findNavController(nameEt).navigateUp();
        });

        camBtn = view.findViewById(R.id.main_cam_btn);
        galleryBtn = view.findViewById(R.id.main_gallery_btn);

        camBtn.setOnClickListener(v -> openCam());

        galleryBtn.setOnClickListener(v -> openGallery());
        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageImv.setImageBitmap(imageBitmap);
                picUploaded = true;
            }
        } else if (requestCode == REQUEST_GALLERY) {
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                imageImv.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (nameEt.getText().toString().isEmpty()) {
            nameEt.setError("Enter a name");
            valid = false;
        }

        if (descEt.getText().toString().isEmpty()) {
            descEt.setError("Enter a description");
            valid = false;
        }

        if (longitudeEt.getText().toString().isEmpty()) {
            longitudeEt.setError("Enter a longitude");
            valid = false;
        }

        if (latitudeEt.getText().toString().isEmpty()) {
            latitudeEt.setError("Enter a latitude");
            valid = false;
        }

        try {
            Double.parseDouble(latitudeEt.getText().toString());
        } catch (Exception e) {
            latitudeEt.setError("latitude must be a double");
        }

        try {
            Double.parseDouble(longitudeEt.getText().toString());
        } catch (Exception e) {
            longitudeEt.setError("longitude must be a double");
        }

        if (!picUploaded) {
            Toast.makeText(getContext(), "Upload a restaurant logo", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void save() {
        if (!validateForm()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String name = nameEt.getText().toString();
        String desc = descEt.getText().toString();
        Double latitude = Double.parseDouble(latitudeEt.getText().toString());
        Double longitude = Double.parseDouble(longitudeEt.getText().toString());
        String restaurantTypeId = ""; // typeIdEt.getText().toString();
        Restaurant restaurant = new Restaurant(name, restaurantTypeId, desc, 0.0, latitude, longitude);
        Model.instance.addRestaurant(restaurant, () -> {
            if (imageBitmap != null) {
                Model.instance.saveRestaurantImage(imageBitmap, restaurant.getId() + ".jpg", url -> {
                    restaurant.setImageUrl(url);
                    Model.instance.updateRestaurant(restaurant,
                            () -> Navigation.findNavController(nameEt).navigateUp());
                });
            } else {
                Navigation.findNavController(nameEt).navigateUp();
            }
        });
    }
}