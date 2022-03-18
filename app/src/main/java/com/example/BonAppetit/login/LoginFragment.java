package com.example.BonAppetit.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.BonAppetit.R;
import com.example.BonAppetit.feed.BaseActivity;


public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment_to_studentListRvFragment);

        });

        Button registerBtn = view.findViewById(R.id.login_register_btn);
        registerBtn.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment));
        return view;
    }

    private void toFeedActivity() {
        Intent intent = new Intent(getContext(), BaseActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}