package com.example.BonAppetit.login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BonAppetit.R;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

public class RegisterFragment extends Fragment {
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phone;
    EditText password;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.buttonRegister);
        firstName = view.findViewById(R.id.editTextFirstname);
        lastName = view.findViewById(R.id.editTextLastname);
        email = view.findViewById(R.id.editTextEmail);
        phone = view.findViewById(R.id.editTextPhone);
        password = view.findViewById(R.id.editTextPassword);

        registerButton.setOnClickListener(v -> register());

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private void register() {
        if (!validateForm()) {
            return;
        }

        User user = new User();

        String mail = email.getText().toString();
        String pass = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser userAuth = mAuth.getCurrentUser();
                user.setId(userAuth.getUid());
                user.setFirstName(firstName.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setMail(email.getText().toString());
                user.setPassword(password.getText().toString());

                Model.instance.registerUser(user, () -> {
                    Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_LONG).show();

                    Navigation.findNavController(getView())
                            .navigate(R.id.action_registerFragment_to_restaurantListRvFragment);
                });
            } else {
                Toast.makeText(getContext(), "Failed to register, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        if (firstName.getText().toString().isEmpty()) {
            firstName.setError("Enter a first name");
            valid = false;
        }

        if (lastName.getText().toString().isEmpty()) {
            lastName.setError("Enter a last name");
            valid = false;
        }

        if (phone.getText().toString().isEmpty()) {
            lastName.setError("Enter a phone number");
            valid = false;
        }

        if (email.getText().toString().isEmpty()) {
            email.setError("Enter an email address");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError("Enter a password");
            valid = false;
        } else if (password.getText().toString().length() < 8) {
            password.setError("Password must contain at least 8 characters");
            valid = false;
        }

        return valid;
    }
}