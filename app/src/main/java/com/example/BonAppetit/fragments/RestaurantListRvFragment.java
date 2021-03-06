package com.example.BonAppetit.fragments;

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
import com.example.BonAppetit.viewmodel.RestaurantListRvViewModel;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.Restaurant;
import com.example.BonAppetit.model.RestaurantType;
import com.example.BonAppetit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RestaurantListRvFragment extends Fragment {
    RestaurantListRvViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<RestaurantType> restaurantTypes;
    String resturantTypeIds;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RestaurantListRvViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_list, container, false);

        restaurantTypes = Model.instance.getFilters();

        ArrayList<String> restaurantTypes1 = new ArrayList<>();

        for (RestaurantType type : restaurantTypes) {
            restaurantTypes1.add(type.getId());
        }

        String[] types = restaurantTypes1.toArray(new String[restaurantTypes1.size()]);

        resturantTypeIds = join("," , types);

        swipeRefresh = view.findViewById(R.id.restaurantlist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshRestaurantList());

        RecyclerView list = view.findViewById(R.id.restaurantlist_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String stId = viewModel.getData(resturantTypeIds).getValue().get(position).getId();
            Navigation
                    .findNavController(v)
                    .navigate(RestaurantListRvFragmentDirections.actionRestaurantListRvFragmentToRestaurantReviewsFragment(stId));
        });

        setHasOptionsMenu(true);
        viewModel.getData(resturantTypeIds).observe(getViewLifecycleOwner(), list1 -> refresh());
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

    private static String join(String separator, String[] input) {

        if (input == null || input.length <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length; i++) {

            sb.append(input[i]);

            // if not the last item
            if (i != input.length - 1) {
                sb.append(separator);
            }

        }

        return sb.toString();
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageImv;
        TextView nameTv;
        TextView descTv;
        RatingBar rateRb;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.listrow_name_tv);
            descTv = itemView.findViewById(R.id.listrow_desc_tv);
            imageImv = itemView.findViewById(R.id.restaurant_listrow_image_imv);
            rateRb = itemView.findViewById(R.id.restaurant_listrow_rate_bar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v, pos);
                }
            });
        }

        void bind(Restaurant restaurant) {
            nameTv.setText(restaurant.getName());
            descTv.setText(restaurant.getDescription());
            rateRb.setRating(restaurant.getAvgRate().floatValue());
            imageImv.setImageResource(R.mipmap.food_placeholder);
            if (restaurant.getImageUrl() != null) {
                Picasso.get()
                        .load(restaurant.getImageUrl())
                        .into(imageImv);
            }
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
            View view = getLayoutInflater().inflate(R.layout.restaurant_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Restaurant restaurant = viewModel.getData(resturantTypeIds).getValue().get(position);
            holder.bind(restaurant);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData(resturantTypeIds).getValue() == null) {
                return 0;
            }
            return viewModel.getData(resturantTypeIds).getValue().size();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Check if user isAdmin
        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Model.instance.getUserById(UserID, new Model.GetUserByIdListener() {
            @Override
            public void onComplete(User user) {

                if (user.isAdmin() == true) {
                    inflater.inflate(R.menu.restaurant_list_menu_admin, menu);
                } else {
                    inflater.inflate(R.menu.restaurants_list_menu, menu);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addRestaurantFragment) {
            return true;
        } else if (item.getItemId() == R.id.addReviewFragment1) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}