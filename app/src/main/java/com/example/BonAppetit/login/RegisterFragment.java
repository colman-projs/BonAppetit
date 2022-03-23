package com.example.BonAppetit.login;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.BonAppetit.R;
import com.example.BonAppetit.feed.BaseActivity;
import com.example.BonAppetit.model.Model;
import com.example.BonAppetit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class RegisterFragment extends Fragment {
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    ImageButton editPicButton;
    ImageView userPicture;
    boolean picUploaded = false;
    boolean isEdit = false;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isEdit = RegisterFragmentArgs.fromBundle(getArguments()).getEdit();

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button registerButton = view.findViewById(R.id.buttonRegister);

        firstName = view.findViewById(R.id.editTextFirstname);
        lastName = view.findViewById(R.id.editTextLastname);
        email = view.findViewById(R.id.editTextEmail);
        password = view.findViewById(R.id.editTextPassword);
        editPicButton = view.findViewById(R.id.register_profile_pic_edit);
        userPicture = view.findViewById(R.id.register_profile_pic);

        registerButton.setOnClickListener(v -> registerOrUpdate());

        editPicButton.setOnClickListener(view1 -> uploadPicture());

        mAuth = FirebaseAuth.getInstance();

        if (isEdit) {
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.update);
            registerButton.setText(R.string.update);
            password.setVisibility(View.INVISIBLE);
            email.setVisibility(View.INVISIBLE);
            loadUserData();
        }

        return view;
    }

    private void loadUserData() {
        ((BaseActivity) getActivity()).setLoading(true);

        Model.instance.getUserById(mAuth.getCurrentUser().getUid(), (user -> {
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());

            Picasso.get().load(user.getAvatarUrl()).into(userPicture);

            ((BaseActivity) getActivity()).setLoading(false);
        }));
    }

    private void registerOrUpdate() {
        if (!validateForm()) {
            return;
        }

        ((BaseActivity) getActivity()).setLoading(true);

        if (isEdit) {
            updateUser();
        } else {
            createUser();
        }
    }

    private void createUser() {
        User user = new User();

        String mail = email.getText().toString();
        String pass = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser userAuth = mAuth.getCurrentUser();

                BitmapDrawable drawable = (BitmapDrawable) userPicture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                Model.instance.saveUserImage(bitmap, userAuth.getUid() + ".jpg", url -> {
                    user.setId(userAuth.getUid());
                    user.setFirstName(firstName.getText().toString());
                    user.setLastName(lastName.getText().toString());
                    user.setMail(email.getText().toString());
                    user.setAvatarUrl(url);

                    Model.instance.registerUser(user, () -> {
                        ((BaseActivity) getActivity()).setLoading(false);

                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_LONG).show();

                        Navigation.findNavController(getView())
                                .navigate(R.id.action_registerFragment_to_restaurantListRvFragment);
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    });
                });
            } else {
                ((BaseActivity) getActivity()).setLoading(false);
                if (task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(getContext(), "An account with this email address already exists", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Failed to register, please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateUser() {
        FirebaseUser userAuth = mAuth.getCurrentUser();

        BitmapDrawable drawable = (BitmapDrawable) userPicture.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Model.instance.saveUserImage(bitmap, userAuth.getUid() + ".jpg", url -> {
            Model.instance.getUserById(mAuth.getCurrentUser().getUid(), (user) -> {
                user.setFirstName(firstName.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setAvatarUrl(url);

                Model.instance.updateUser(user, () -> {
                    ((BaseActivity) getActivity()).setLoading(false);

                    Toast.makeText(getActivity(), "Update successful", Toast.LENGTH_LONG).show();

                    Navigation.findNavController(getView()).popBackStack();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                });
            });
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

        if (!isEdit) {
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

            if (!picUploaded) {
                Toast.makeText(getContext(), "Take a profile picture", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        return valid;
    }

    private void uploadPicture() {
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) return;

        if (resultCode == RESULT_OK && data != null) {
            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

            userPicture.setImageBitmap(selectedImage);
            picUploaded = true;
        }

    }
}