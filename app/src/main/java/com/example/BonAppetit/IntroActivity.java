package com.example.BonAppetit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.BonAppetit.feed.BaseActivity;
import com.example.BonAppetit.login.LoginActivity;
import com.example.BonAppetit.model.Model;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Model.instance.executor.execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Model.instance.isSignedIn()){
                Model.instance.mainThread.post(() -> {
                    toFeedActivity();
                });
            }else{
                Model.instance.mainThread.post(() -> {
                    toLoginActivity();
                });
            }
        });

    }

    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toFeedActivity() {
        Intent intent = new Intent(this, BaseActivity.class);
        startActivity(intent);
        finish();
    }
}