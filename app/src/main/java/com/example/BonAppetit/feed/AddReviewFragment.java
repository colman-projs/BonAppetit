package com.example.BonAppetit.feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Review;

public class AddReviewFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    EditText nameEt;
    EditText idEt;
    CheckBox cb;
    Button saveBtn;
    Button cancelBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageView avatarImv;
    ImageButton camBtn;
    ImageButton galleryBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
<<<<<<<< HEAD:app/src/main/java/com/example/BonAppetit/feed/AddReviewFragment.java
        View view = inflater.inflate(R.layout.fragment_add_review, container, false);
        nameEt = view.findViewById(R.id.main_name_et);
========
        View view = inflater.inflate(R.layout.fragment_add_student,container, false);
        nameEt = view.findViewById(R.id.review_review_txt);
>>>>>>>> e985e66d6b01f304f13dd23d5b2ddadb01d026c5:app/src/main/java/com/example/BonAppetit/feed/AddStudentFragment.java
        idEt = view.findViewById(R.id.main_id_et);
        cb = view.findViewById(R.id.main_cb);
        saveBtn = view.findViewById(R.id.main_save_btn);
        cancelBtn = view.findViewById(R.id.main_cancel_btn);
        progressBar = view.findViewById(R.id.main_progressbar);
        progressBar.setVisibility(View.GONE);
        avatarImv = view.findViewById(R.id.review_user_img);

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
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
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
        String id = idEt.getText().toString();
        boolean flag = cb.isChecked();
        Log.d("TAG", "saved name:" + name + " id:" + id + " flag:" + flag);
        Review review = new Review(1, 1, name, 1);
        if (imageBitmap == null) {

            // TODO: Add review to restaurant
//            addReview(review,()->{
//                Navigation.findNavController(nameEt).navigateUp();
//            });
        } else {
            Model.instance.saveUserImage(imageBitmap, id + ".jpg", url -> {
                review.setImageUrl(url);

                // TODO: Add review to restaurant
//                Model.instance.addReview(review,()->{
//                    Navigation.findNavController(nameEt).navigateUp();
//                });
            });
        }
    }
}