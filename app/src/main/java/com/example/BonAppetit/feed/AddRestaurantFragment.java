package com.example.BonAppetit.feed;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.RestaurantType;
import com.example.BonAppetit.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddRestaurantFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    EditText nameEt;
    ImageView imageImv;
    EditText descEt;
    EditText latitudeEt;
    EditText longitudeEt;

    Button saveBtn;
    Button cancelBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageButton camBtn;
    ImageButton galleryBtn;
    private Spinner spinner;
    SwipeRefreshLayout swipeRefresh;
    RestaurantListRvViewModel viewModel;
    RestaurantType[] restaurantTypes;
    HashMap<String ,String> hmLang = new HashMap<String,String>();

   // private LinkedHashMapAdapter<String, String> acadapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        RestaurantTypesViewModel viewModel = new ViewModelProvider(this).get(RestaurantTypesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_restaurant,container, false);
        nameEt = view.findViewById(R.id.main_name_et);
        descEt = view.findViewById(R.id.main_desc_et);
        latitudeEt = view.findViewById(R.id.main_lat_et);
        longitudeEt = view.findViewById(R.id.main_lng_et);
        saveBtn = view.findViewById(R.id.main_save_btn);
        cancelBtn = view.findViewById(R.id.main_cancel_btn);
        progressBar = view.findViewById(R.id.main_progressbar);
        progressBar.setVisibility(View.GONE);
        imageImv = view.findViewById(R.id.main_image_imv);
        spinner = (Spinner)view.findViewById(R.id.spinner);


        saveBtn.setOnClickListener(v -> save());

        camBtn = view.findViewById(R.id.main_cam_btn);
        galleryBtn = view.findViewById(R.id.main_gallery_btn);

        camBtn.setOnClickListener(v -> openCam());

        galleryBtn.setOnClickListener(v -> openGallery());


        //Get ALL types
        progressBar.setVisibility(View.VISIBLE);
        Model.instance.getAllTypes().observe(getViewLifecycleOwner(), list -> {

//            paths = ((MutableLiveData<List<RestaurantType>>) list).getValue().toArray(new RestaurantType[list.size()]);

            restaurantTypes = list.toArray(new RestaurantType[list.size()]);

            for (RestaurantType restaurant: restaurantTypes) {
                hmLang.put((String) restaurant.getId(), restaurant.getName());
            }


//            adapter = new LinkedHashMapAdapter<String, String>(this, android.R.layout.simple_spinner_item, hmLang);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner.setAdapter(adapter);


            progressBar.setVisibility(View.GONE);
        });











        spinner.setOnItemSelectedListener(this.spinner.getOnItemSelectedListener());
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
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String name = nameEt.getText().toString();
        String desc = descEt.getText().toString();
        Double latitude = Double.parseDouble(latitudeEt.getText().toString());
        Double longitude = Double.parseDouble(longitudeEt.getText().toString());
        String restaurantTypeId = ""; // typeIdEt.getText().toString();
        Restaurant restaurant = new Restaurant(name, restaurantTypeId, desc, 0.0, latitude, longitude);
        Model.instance.addRestaurant(restaurant,()->{
            if (imageBitmap != null){
                Model.instance.saveRestaurantImage(imageBitmap, restaurant.getId() + ".jpg", url -> {
                    restaurant.setImageUrl(url);
                    Model.instance.updateRestaurant(restaurant,
                            ()-> Navigation.findNavController(nameEt).navigateUp());
                });
            } else {
                Navigation.findNavController(nameEt).navigateUp();
            }
        });
    }
}