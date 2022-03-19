package com.example.BonAppetit.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.squareup.picasso.Picasso;


public class RestaurantReviewsFragment extends Fragment {
    TextView nameTv;
    TextView idTv;
    ImageView avatarImv;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.restaurant_reviews_menu,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_reviews, container, false);

//        String stId = RestaurantDetailsFragmentArgs.fromBundle(getArguments()).getStudentId();

//        Model.instance.getStudentById(stId, new Model.instance.() {
//            @Override
//            public void onComplete(Restaurant restaurant) {
//                nameTv.setText(restaurant.getName());
//                idTv.setText(restaurant.getId());
//                if (restaurant.getImageUrl() != null) {
//                    Picasso.get().load(restaurant.getImageUrl()).into(avatarImv);
//                }
//            }
//        });

        nameTv = view.findViewById(R.id.details_name_tv);
        idTv = view.findViewById(R.id.details_id_tv);
        avatarImv = view.findViewById(R.id.details_avatar_img);

        Button backBtn = view.findViewById(R.id.details_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });
        return view;
    }
}