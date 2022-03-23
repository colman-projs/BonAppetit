package com.example.BonAppetit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.BonAppetit.R;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    NavController navCtl;
    ProgressBar progressBar;
    FragmentContainerView fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        progressBar = findViewById(R.id.progressBar);
        fragmentContainer = findViewById(R.id.base_navhost);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.restaurantListRvFragment, R.id.loginFragment)
                .build();

        NavHost navHost = (NavHost) getSupportFragmentManager().findFragmentById(R.id.base_navhost);
        navCtl = navHost.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navCtl, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    navCtl.navigateUp();
                    return true;
                case R.id.menu_logout:
                    FirebaseAuth.getInstance().signOut();
                    navCtl.navigate(R.id.action_global_loginFragment);
                    break;
                default:
                    NavigationUI.onNavDestinationSelected(item, navCtl);
            }
        } else {
            return true;
        }
        return false;
    }

    public void setLoading(boolean loading){
        if (loading){
            progressBar.setVisibility(View.VISIBLE);
            fragmentContainer.setAlpha(0.2f);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            fragmentContainer.setAlpha(1f);
        }
    }
}