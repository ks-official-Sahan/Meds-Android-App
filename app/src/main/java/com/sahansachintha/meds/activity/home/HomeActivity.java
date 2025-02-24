package com.sahansachintha.meds.activity.home;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.fragment.navigation.ProfileFragment;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.PermissionHelper;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private static NavigationView navigationView;
    private FragmentManager fragmentManager;
    private static BottomNavigationView bottomNavigationView;

    public static BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public static NavigationView getNavigationView() {
        return navigationView;
    }

    private static final String PREFS_NAME = "MyMedsPrefs";

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

        initViews();

        setupToolbar();
        //setupFloatingButton();

        setupFragmentManagement();

        /* Navigations */
        setupNavigationListeners();
        setupNavigationViewColors();
        /* Navigations */

        requestPermissions();

        getOnBackPressedDispatcher().addCallback(this, new HomeOnBackPressedCallback());
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout_home);
        navigationView = findViewById(R.id.navigation_view_user);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_user);
    }

    /* Fragment Management */
    private void setupFragmentManagement() {
        fragmentManager = getSupportFragmentManager();

        //showFragment(HomeFragment.class);
        int fragmentId = getIntent().getIntExtra("FRAGMENT_ID", R.id.menu_item_home);
//        if (NavigationHelper.getInstance().sharedFragments.containsKey(fragmentId)) {
//            showFragment(NavigationHelper.getInstance().getFragmentById(fragmentId));
//        } else {
//            bottomNavigationView.setSelectedItemId(fragmentId);
//        }

        showFragment(NavigationHelper.getInstance().getFragmentById(fragmentId));
//        if (fragmentId != R.id.menu_item_home) {
//            if (!NavigationHelper.getInstance().sharedFragments.containsKey(fragmentId)) {
//                bottomNavigationView.setSelectedItemId(fragmentId);
//            }
//        }

        //        int itemId = getSelectedMenuId();
//        if (itemId != R.id.nav_item_home && NavigationHelper.getInstance().homeFragments.containsKey(itemId)) {
//            if (itemId == R.id.nav_item_reminders) {
//                bottomNavigationView.setSelectedItemId(R.id.menu_item_reminders);
//            } else if (itemId == R.id.nav_item_medications) {
//                bottomNavigationView.setSelectedItemId(R.id.menu_item_medications);
//            }
//        }
    }

    private void showFragment(Class<? extends Fragment> fragmentClass) {
        NavigationHelper.getInstance().showFragment(fragmentManager, R.id.fragmentContainerViewHome, fragmentClass);
    }
    /* Fragment Management */

    /* Custom Toolbar */
    private void setupToolbar() {
        findViewById(R.id.action_menu).setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        findViewById(R.id.action_profile).setOnClickListener(v -> showFragment(ProfileFragment.class));
    }
    /* Custom Toolbar */

    /* Floating Button */
    private void setupFloatingButton() {
        findViewById(R.id.fab).setOnClickListener(v -> NavigationHelper.getInstance().openIntent(this, StoreActivity.class));
    }
    /* Floating Button */

    /* Setup Navigation */
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
    /* Setup Navigation */

    /* Manage Navigation Logic */
    private boolean runNavigation(int itemID) {
        return NavigationHelper.getInstance().runNavigation(itemID,
                this::showFragment,
                this::openIntent,
                getSelectedMenuId());
    }

    private int getSelectedMenuId() {
        return (navigationView.getCheckedItem() != null)
                ? navigationView.getCheckedItem().getItemId()
                : bottomNavigationView.getSelectedItemId();
    }
    /* Manage Navigation Logic */

    /* Activity Open */
    private void openIntent(Class<?> activity) {
        NavigationHelper.getInstance().openIntent(HomeActivity.this, activity);
        finish();
    }

    private void openIntent(Class<?> activity, int fragmentId) {
        NavigationHelper.getInstance().openIntent(HomeActivity.this, activity, fragmentId);
        finish();
    }
    /* Activity Open */

    /* Request Permissions */
    private void requestPermissions() {
        PermissionHelper.getNotificationPermission(this);
        PermissionHelper.getLocationPermission(this);
        PermissionHelper.getBluetoothPermission(this);
        PermissionHelper.requestBatteryOptimizations(this);
        PermissionHelper.requestOverlayPermission(this);
    }
    /* Request Permissions */

    private void logNavigation(int itemID) {
        Log.i("MyMedsNavigation", String.valueOf(itemID));
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
