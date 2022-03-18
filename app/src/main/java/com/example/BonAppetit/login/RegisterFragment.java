package com.example.BonAppetit.login;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.BonAppetit.R;

public class RegisterFragment extends Fragment {
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phone;
    EditText password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(v -> {
            register();
        });
        return view;
    }

    private void register(){
        //TODO: registration
    }
}