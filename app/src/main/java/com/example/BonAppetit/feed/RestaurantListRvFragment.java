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
import com.example.BonAppetit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class RestaurantListRvFragment extends Fragment {
    RestaurantListRvViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RestaurantListRvViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_list,container,false);

        swipeRefresh = view.findViewById(R.id.restaurantlist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshRestaurantList());

        RecyclerView list = view.findViewById(R.id.restaurantlist_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String stId = viewModel.getData().getValue().get(position).getId();
            Navigation
                    .findNavController(v)
                    .navigate(RestaurantListRvFragmentDirections.actionRestaurantListRvFragmentToRestaurantReviewsFragment(stId));
        });

        setHasOptionsMenu(true);
        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getListLoadingState().getValue() == Model.LoadingStates.loading);
        Model.instance.getListLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            if (loadingState == Model.LoadingStates.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }

        });
        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageImv;
        TextView nameTv;
        TextView descTv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.listrow_name_tv);
            descTv = itemView.findViewById(R.id.listrow_desc_tv);
            imageImv = itemView.findViewById(R.id.restaurant_listrow_image_imv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v,pos);
                }
            });
        }

        void bind(Restaurant restaurant){
            nameTv.setText(restaurant.getName());
            descTv.setText(restaurant.getDescription());
            imageImv.setImageResource(R.mipmap.food_placeholder);
            if (restaurant.getImageUrl() != null) {
                Picasso.get()
                        .load(restaurant.getImageUrl())
                        .into(imageImv);
            }
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.restaurant_list_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Restaurant restaurant = viewModel.getData().getValue().get(position);
            holder.bind(restaurant);
        }

        @Override
        public int getItemCount() {
            if(viewModel.getData().getValue() == null){
                return 0;
            }
            return viewModel.getData().getValue().size();
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

                if (user.isAdmin() == true){
                    inflater.inflate(R.menu.restaurant_list_menu_admin,menu);
                } else {
                    inflater.inflate(R.menu.restaurants_list_menu,menu);
                }


            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addRestaurantFragment){
            Log.d("TAG","ADD...");
            return true;
        } else if (item.getItemId() == R.id.addReviewFragment1){
            Log.d("TAG","ADD...");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}