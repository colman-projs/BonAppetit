package com.example.BonAppetit.feed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.BonAppetit.model.RestaurantType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RestaurantTypesFragment extends Fragment {
    RestaurantTypesViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    Button confirmButton;

    ArrayList<RestaurantType> filters;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        filters = new ArrayList<RestaurantType>();
    }

    public void updateFilter(RestaurantType filter) {
        if (filter.isChecked()) {
            filters.add(filter);
        } else {
            filters.remove(filter);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RestaurantTypesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_types, container, false);

        confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> confirm(v));

        swipeRefresh = view.findViewById(R.id.restauranttypes_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshRestaurantTypes());

        RecyclerView list = view.findViewById(R.id.restauranttypes_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, filter) -> {
            updateFilter(filter);
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

    private void confirm(View v) {
        ArrayList<String> restaurantTypes = new ArrayList<>();

        for (RestaurantType type : filters) {
            restaurantTypes.add(type.getId());
        }

        String[] types = restaurantTypes.toArray(new String[restaurantTypes.size()]);

        Navigation.
                findNavController(v).
                navigate(RestaurantTypesFragmentDirections.
                        actionRestaurantTypesFragmentToRestaurantListRvFragment(join(",", types)));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RestaurantType _restaurantType;
        ImageView image;
        TextView name;
        CheckBox checkbox;
        String id;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurant_typerow_name);
            image = itemView.findViewById(R.id.restaurant_typerow_image);
            checkbox = itemView.findViewById(R.id.restaurant_typerow_cb);

            itemView.setOnClickListener(v -> {
                checkbox.setChecked(!checkbox.isChecked());
                _restaurantType.setChecked(checkbox.isChecked());
                listener.onItemClick(v, _restaurantType);
            });

            checkbox.setOnClickListener(v -> {
                _restaurantType.setChecked(checkbox.isChecked());
                listener.onItemClick(v, _restaurantType);
            });
        }

        void bind(RestaurantType restaurantType) {
            _restaurantType = restaurantType;
            id = restaurantType.getId();
            checkbox.setChecked(restaurantType.isChecked());
            name.setText(restaurantType.getName());
            image.setImageResource(R.mipmap.food_placeholder);
            if (restaurantType.getImageUrl() != null) {
                Picasso.get()
                        .load(restaurantType.getImageUrl())
                        .into(image);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(View v, RestaurantType filter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.restaurant_type_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            RestaurantType filter = viewModel.getData().getValue().get(position);
            holder.bind(filter);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) {
                return 0;
            }
            return viewModel.getData().getValue().size();
        }
    }
}
