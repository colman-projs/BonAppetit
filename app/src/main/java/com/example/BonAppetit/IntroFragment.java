package com.example.BonAppetit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.BonAppetit.model.Model;

public class IntroFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Model.instance.executor.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Model.instance.mainThread.post(() -> navigate(Model.instance.isSignedIn()));
        });
    }

    private void navigate(boolean signedIn) {
        Navigation.findNavController(getView())
                .navigate(signedIn ?
                        R.id.action_introFragment_to_restaurantListRvFragment :
                        R.id.action_introFragment_to_loginFragment);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}