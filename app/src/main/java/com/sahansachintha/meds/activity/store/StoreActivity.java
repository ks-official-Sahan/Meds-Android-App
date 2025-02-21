package com.sahansachintha.meds.activity.store;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.HomeActivity;
import com.sahansachintha.meds.fragment.navigation.ProfileFragment;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.PermissionHelper;

public class StoreActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);
        setupEdgeToEdgeInsets();

        initViews();
        setupToolbar();
        setupFloatingButton();
        setupFragmentManagement();
        setupNavigationListeners();
        setupNavigationViewColors();
        requestPermissions();

         getOnBackPressedDispatcher().addCallback(this, new StoreOnBackPressedCallback());
    }

    private void setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout_store), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout_store);
        navigationView = findViewById(R.id.navigation_view_user_store);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_user_store);
    }

    private void setupToolbar() {
        findViewById(R.id.action_menu_store).setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        findViewById(R.id.action_home_store).setOnClickListener(v -> openIntent(HomeActivity.class));
        findViewById(R.id.action_profile_store).setOnClickListener(v -> showFragment(ProfileFragment.class));
    }

    private void setupFloatingButton() {
        findViewById(R.id.fab_store).setOnClickListener(v -> NavigationHelper.getInstance().makeCall(this));
    }

    private void setupFragmentManagement() {
        fragmentManager = getSupportFragmentManager();

        //showFragment(StoreFragment.class);
        showFragment(NavigationHelper.getInstance().getFragmentById(getIntent().getIntExtra("FRAGMENT_ID", R.id.menu_item_store)));
    }

    private void setupNavigationListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            logNavigation(item.getItemId());
            drawerLayout.closeDrawers();
            return runNavigation(item.getItemId());
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            logNavigation(item.getItemId());
            return runNavigation(item.getItemId());
        });
    }

    private void setupNavigationViewColors() {
        int iconTint = R.color.nav_item_icon_tint;
        int textColor = R.color.nav_item_text_color;

        navigationView.setItemIconTintList(ContextCompat.getColorStateList(this, iconTint));
        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, iconTint));
        navigationView.setItemTextColor(ContextCompat.getColorStateList(this, textColor));
        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, textColor));
    }

    private void requestPermissions() {
        PermissionHelper.getNotificationPermission(this);
        PermissionHelper.getLocationPermission(this);
        PermissionHelper.getBluetoothPermission(this);
        PermissionHelper.requestBatteryOptimizations(this);
        PermissionHelper.requestOverlayPermission(this);
    }

    private void logNavigation(int itemID) {
        Log.i("MyMedsNavigation", String.valueOf(itemID));
    }

    private boolean runNavigation(int itemID) {
        return NavigationHelper.getInstance().runNavigation(
                itemID,
                this::showFragment,
                this::openIntent,
                getSelectedMenuId()
        );
    }

    private int getSelectedMenuId() {
        return navigationView.getCheckedItem() != null
                ? navigationView.getCheckedItem().getItemId()
                : bottomNavigationView.getSelectedItemId();
    }

    private void showFragment(Class<? extends Fragment> fragmentClass) {
        NavigationHelper.getInstance().showFragment(fragmentManager, R.id.fragmentContainerViewStore, fragmentClass);
    }

    private void openIntent(Class<?> activity) {
        NavigationHelper.getInstance().openIntent(this, activity);
        finish();
    }

    private void openIntent(Class<?> activity, int fragmentId) {
        NavigationHelper.getInstance().openIntent(StoreActivity.this, activity, fragmentId);
        finish();
    }

    private class StoreOnBackPressedCallback extends OnBackPressedCallback {
        public StoreOnBackPressedCallback() {
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
