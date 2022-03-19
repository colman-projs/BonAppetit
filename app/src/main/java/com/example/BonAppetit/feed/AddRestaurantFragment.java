package com.example.BonAppetit.feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;

public class AddRestaurantFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    EditText nameEt;
    ImageView avatarImv;
    EditText descEt;

    Button saveBtn;
    Button cancelBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageButton camBtn;
    ImageButton galleryBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_restaurant,container, false);
        nameEt = view.findViewById(R.id.main_name_et);
        descEt = view.findViewById(R.id.main_desc_et);
        saveBtn = view.findViewById(R.id.main_save_btn);
        cancelBtn = view.findViewById(R.id.main_cancel_btn);
        progressBar = view.findViewById(R.id.main_progressbar);
        progressBar.setVisibility(View.GONE);
        avatarImv = view.findViewById(R.id.main_image_imv);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        camBtn = view.findViewById(R.id.main_cam_btn);
        galleryBtn = view.findViewById(R.id.main_gallery_btn);

        camBtn.setOnClickListener(v -> {
            openCam();
        });

        galleryBtn.setOnClickListener(v -> {
            openGallery();     
        });
        return view;
    }

    private void openGallery() {
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
                avatarImv.setImageBitmap(imageBitmap);

            }
        }
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String name = nameEt.getText().toString();
        String desc = descEt.getText().toString();
        Double latitude = 0.0,
                longitude = 0.0;
        Restaurant restaurant = new Restaurant(name, "", desc, 0.0, latitude, longitude);
        Model.instance.addRestaurant(restaurant,()->{
            if (imageBitmap != null){
                Model.instance.saveRestaurantImage(imageBitmap, restaurant.getId() + ".jpg", url -> {
                    restaurant.setImageUrl(url);
                    Model.instance.addRestaurant(restaurant,()->{
                        Navigation.findNavController(nameEt).navigateUp();
                    });
                });
            } else {
                Navigation.findNavController(nameEt).navigateUp();
            }
        });
    }
}