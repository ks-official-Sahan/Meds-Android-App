package com.sahansachintha.meds.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.sahansachintha.meds.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Navigations */
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_home);

        Toolbar toolbar = findViewById(R.id.toolbar_user);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.open());

        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigation_view_user);
        navigationView
                //.setNavigationItemSelectedListener(item -> {
                .setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.i("App28Log", item.toString());
//                        if (item.getItemId() == R.id.menu_item_product) {
//                            FragmentManager fragmentManager = getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.fragmentContainerView1, ProductFragment.class, null)
//                                    .setReorderingAllowed(true)
//                                    .commit();
//                        } else if (item.getItemId() == R.id.menu_item_test) {
//                            FragmentManager fragmentManager = getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.fragmentContainerView1, TestFragment.class, null)
//                                    .setReorderingAllowed(true)
//                                    .commit();
//                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

        /* Navigations */
    }
}