package com.example.BonAppetit.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonAppetit.R;
import com.example.BonAppetit.feed.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    TextView email;
    TextView password;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_login, container, false);


        email = view.findViewById(R.id.login_email_et);
        password = view.findViewById(R.id.login_password_et);

        Button loginBtn = view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(v -> login());

        Button registerBtn = view.findViewById(R.id.login_register_btn);
        registerBtn.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment));

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void login() {
        if (!validateForm()) {
            return;
        }

        ((BaseActivity) getActivity()).setLoading(true);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_loginFragment_to_restaurantListRvFragment);
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                        ((BaseActivity) getActivity()).setLoading(false);
                    } else {
                        ((BaseActivity) getActivity()).setLoading(false);
                        Toast.makeText(getContext(),
                                "Login failed, please check your credentials", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        if (email.getText().toString().isEmpty()) {
            email.setError("Enter a mail");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError("Enter a password");
            valid = false;
        }

        return valid;
    }
}