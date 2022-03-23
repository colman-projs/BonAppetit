package com.example.BonAppetit.feed;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.Review;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RestaurantReviewsFragment extends Fragment {
    RestaurantReviewsViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    String restaurantId;
    ImageView restaurantImageImv;
    TextView restaurantNameTv;
    RatingBar resRateRb;
    TextView restaurantDescTv;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RestaurantReviewsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_reviews, container, false);

        restaurantId = RestaurantReviewsFragmentArgs.fromBundle(getArguments()).getRestaurantId();
        viewModel.changeRestaurantId(restaurantId);

        restaurantImageImv = view.findViewById(R.id.restaurant_detail_image_img);
        restaurantNameTv = view.findViewById(R.id.restaurant_detail_name_tv);
        resRateRb = view.findViewById(R.id.restaurant_detail_rate_bar);
        restaurantDescTv = view.findViewById(R.id.restaurant_detail_desc_tv);

        swipeRefresh = view.findViewById(R.id.restaurantreviews_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshRestaurantReviews(restaurantId));

        Model.instance.getRestaurantById(restaurantId, new Model.GetRestaurantById() {
            @Override
            public void onComplete(Restaurant restaurant) {
                restaurantNameTv.setText(restaurant.getName());
                resRateRb.setRating(restaurant.getAvgRate().floatValue());
                restaurantDescTv.setText(restaurant.getDescription());
                if (restaurant.getImageUrl() != null) {
                    Picasso.get().load(restaurant.getImageUrl()).into(restaurantImageImv);
                }
            }
        });

        RecyclerView list = view.findViewById(R.id.restaurantreviews);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String stId = viewModel.getData().getValue().get(position).getId();
//                Navigation.findNavController(v).navigate(RestaurantListRvFragmentDirections.actionRestaurantListRvFragmentToRestaurantDetailsFragment(stId));

            }
        });

        setHasOptionsMenu(true);
        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getListLoadingState().getValue() == Model.LoadingStates.loading);
        Model.instance.getListLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            if (loadingState == Model.LoadingStates.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }

        });
        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageImv;
        TextView nameTv;
        RatingBar rateRb;
        TextView dateTv;
        TextView descTv;
        ImageView imageImv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            userImageImv = itemView.findViewById(R.id.review_listrow_user_image_imv);
            nameTv = itemView.findViewById(R.id.review_listrow_name_tv);
            rateRb = itemView.findViewById(R.id.review_listrow_rate_bar);
            dateTv = itemView.findViewById(R.id.review_listrow_date_tv);
            descTv = itemView.findViewById(R.id.review_listrow_description_tv);
            imageImv = itemView.findViewById(R.id.review_listrow_image_imv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v, pos);
                }
            });
        }

        void bind(Review review) {
            descTv.setText(review.getDescription());
            Date reviewDate = new Date(review.getUpdateDate() * 1000);
            DateFormat df = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
            dateTv.setText(df.format(reviewDate));
            rateRb.setRating(review.getRating());
            if (review.getImageUrl() != null) {
                Picasso.get()
                        .load(review.getImageUrl())
                        .into(imageImv);
            }

            Model.instance.getUserById(review.getUserId(), user1 -> {
                nameTv.setText(user1.getFullName());
                userImageImv.setImageResource(R.drawable.avatar);
                if (user1.getAvatarUrl() != null) {
                    Picasso.get()
                            .load(user1.getAvatarUrl())
                            .into(userImageImv);
                }
            });

        }
    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.review_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Review review = viewModel.getData().getValue().get(position);
            holder.bind(review);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) {
                return 0;
            }
            return viewModel.getData().getValue().size();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.restaurant_reviews_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addRestaurantFragment) {
            Log.d("TAG", "ADD...");
            return true;
        } else if (item.getItemId() == R.id.addReviewFragment1) {
            String restaurantId = viewModel.getRestaurantId();

            Navigation.findNavController(this.getView()).navigate(RestaurantReviewsFragmentDirections.actionRestaurantReviewsFragmentToAddReviewFragment(restaurantId));

            Log.d("TAG", "ADD...");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}