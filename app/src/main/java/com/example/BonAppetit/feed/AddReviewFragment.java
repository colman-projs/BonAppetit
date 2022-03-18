package com.example.BonAppetit.feed;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import android.widget.TextView;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Student;


public class AddReviewFragment extends Fragment {
    EditText reviewEt;
    Button saveBtn;
    Button cancelBtn;
    ProgressBar progressBar;
    ImageView UserImV;
    TextView userNameTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_review,container, false);
        reviewEt = view.findViewById(R.id.review_review_txt);
        UserImV  = view.findViewById(R.id.review_user_img);
        progressBar = view.findViewById(R.id.review_progressbar);
        saveBtn = view.findViewById(R.id.review_save_btn);
        cancelBtn = view.findViewById(R.id.review_cancel_btn);
        userNameTv = view.findViewById(R.id.review_user_name);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        return view;
    }

    public void save(){
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

        String text = reviewEt.getText().toString();
        Log.d("TAG","saved review:" + text);


        //TODO: SAVE review in the DB..
//        Student student = new Student(name,id,flag);
//        if (imageBitmap == null){
//            Model.instance.addStudent(student,()->{
//                Navigation.findNavController(nameEt).navigateUp();
//            });
//        }else{
//            Model.instance.saveImage(imageBitmap, id + ".jpg", url -> {
//                student.setAvatarUrl(url);
//                Model.instance.addStudent(student,()->{
//                    Navigation.findNavController(nameEt).navigateUp();
//                });
//            });
//        }

    }

    //TODO : cancel review function
    public void cancel(){

    }
}