package com.sahansachintha.meds.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.sahansachintha.meds.R;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Navigations */
        drawerLayout = findViewById(R.id.drawer_layout_home);
        MaterialToolbar toolbar = findViewById(R.id.toolbar_user);
        navigationView = findViewById(R.id.navigation_view_user);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view_user);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        toolbar.setNavigationOnClickListener(v -> drawerLayout.open());
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

//        showFragment(ProductFragment.class);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        /* Handle Drawer Navigation */
        navigationView.setNavigationItemSelectedListener(item -> {
            Log.i("MyMedsLog", item.toString());
            drawerLayout.closeDrawers();
            return runNavigation(item.getItemId());
        });

        /* Handle Bottom Navigation */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.i("MyMedsLog", item.toString());
            return runNavigation(item.getItemId());
        });
        /* Navigations */

        getOnBackPressedDispatcher().addCallback(this, new HomeOnBackPressedCallback());
    }

    private boolean runNavigation(int itemID) {
        if (itemID == R.id.nav_home || itemID == R.id.menu_item_home) {
            //showFragment(HomeFragment.class);
        } else if (itemID == R.id.menu_item_product) {
            //showFragment(ProductFragment.class);
        } else if (itemID == R.id.menu_item_test) {
            //showFragment(TestFragment.class);
        } else {
            return false;
        }
        return true;
    }

    private void showFragment(Class<? extends Fragment> fragmentClass) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            Fragment fragment = fragmentClass.newInstance();
            fragmentTransaction.replace(R.id.fragmentContainerViewHome, fragment, fragmentClass.getSimpleName())
                    .setReorderingAllowed(true)
                    .addToBackStack(fragmentClass.getSimpleName())
                    .commit();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e("HomeActivity", "Error creating fragment", e);
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(navigationView)) {
//            drawerLayout.closeDrawers();
//        } else if (fragmentManager.getBackStackEntryCount() > 1) {
//            fragmentManager.popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HomeOnBackPressedCallback extends OnBackPressedCallback {
        public HomeOnBackPressedCallback() {
            super(true);
        }

        @Override
        public void handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawers();
            } else if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
            } else {
                finish(); // Default behavior to exit the activity
            }
        }
    }
}