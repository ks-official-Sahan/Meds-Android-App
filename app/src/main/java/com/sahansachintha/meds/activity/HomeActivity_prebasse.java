package com.sahansachintha.meds.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.fragment.navigation.HomeFragment;
import com.sahansachintha.meds.fragment.navigation.ProfileFragment;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.PermissionHelper;

public class HomeActivity_prebasse extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    //private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView bottomNavigationView;

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
        navigationView = findViewById(R.id.navigation_view_user);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_user);

        /* Default Toolbar Configuration */
        //MaterialToolbar toolbar = findViewById(R.id.toolbar_user);
        // //toolbar.setNavigationOnClickListener(v -> drawerLayout.open());
        //setSupportActionBar(toolbar);
        //drawerToggle = new ActionBarDrawerToggle(
        //        this,
        //        drawerLayout,
        //        toolbar,
        //        R.string.navigation_drawer_open,
        //        R.string.navigation_drawer_close
        //);
        //drawerLayout.addDrawerListener(drawerToggle);
        //drawerToggle.syncState();
        /* Default Toolbar Configuration */

        /* Custom Toolbar */
        ImageView menuIcon = findViewById(R.id.action_menu);
        ImageView profileIcon = findViewById(R.id.action_profile);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        profileIcon.setOnClickListener(v -> showFragment(ProfileFragment.class));
        /* Custom Toolbar */

        /* Floating Button */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> NavigationHelper.getInstance().makeCall(this));
        /* Floating Button */

        /* Fragment Management */
        fragmentManager = getSupportFragmentManager();
        showFragment(HomeFragment.class);
        navigationView.setCheckedItem(R.id.nav_item_home);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_home);
        /* Fragment Management */

        /* Handle Drawer Navigation */
        navigationView.setNavigationItemSelectedListener(item -> {
            Log.i("MyMedsNavigation", item.toString());
            drawerLayout.closeDrawers();
            return runNavigation(item.getItemId());
        });
        /* Handle Drawer Navigation */

        /* Handle Bottom Navigation */
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.i("MyMedsNavigation", item.toString());
            return runNavigation(item.getItemId());
        });
        /* Handle Bottom Navigation */

        /* Navigation View Item Color (default and selected) */
        navigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.nav_item_icon_tint));
        bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.nav_item_icon_tint));

        navigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.nav_item_text_color));
        bottomNavigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.nav_item_text_color));
        /* Navigation View Item Color */

        /* Default Toolbar Back-press Configuration */
        // getOnBackPressedDispatcher().addCallback(this, new HomeOnBackPressedCallback());
        /* Navigations */


        /* Request Permissions */
        PermissionHelper.getNotificationPermission(this);
        PermissionHelper.getLocationPermission(this);
        PermissionHelper.getBluetoothPermission(this);

        PermissionHelper.requestBatteryOptimizations(this);
        PermissionHelper.requestOverlayPermission(this);
        /* Request Permissions */
    }

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

//    private boolean runNavigation(int itemID) {
//        if (itemID == R.id.nav_item_home || itemID == R.id.menu_item_home) {
//            showFragment(HomeFragment.class);
//        } else if (itemID == R.id.nav_item_reminders || itemID == R.id.menu_item_reminders) {
//            showFragment(RemindersFragment.class);
//        } else if (itemID == R.id.nav_item_medications || itemID == R.id.menu_item_medications) {
//            showFragment(MedicationsFragment.class);
//        } else if (itemID == R.id.nav_item_store || itemID == R.id.menu_item_store) {
//            showFragment(StoreFragment.class);
//        } else if (itemID == R.id.nav_item_cart || itemID == R.id.menu_item_cart) {
//            showFragment(CartFragment.class);
//        } else if (itemID == R.id.nav_item_orders || itemID == R.id.menu_item_orders) {
//            showFragment(OrdersFragment.class);
//        } else if (itemID == R.id.nav_item_profile) {
//            showFragment(ProfileFragment.class);
//        } else if (itemID == R.id.nav_item_setting) {
//            showFragment(SettingFragment.class);
//        } else if (itemID == R.id.nav_item_logout) {
//            openIntent(AuthActivity.class);
//        } else {
//            return false;
//        }
//        return true;
//    }
    /* Manage Navigation Logic */

    /* Fragment Management */
    private void showFragment(Class<? extends Fragment> fragmentClass) {
        NavigationHelper.getInstance().showFragment(fragmentManager, R.id.fragmentContainerViewHome, fragmentClass);
    }
//    private void showFragment(Class<? extends Fragment> fragmentClass) {
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        try {
//            Fragment fragment = fragmentClass.newInstance();
//            fragmentTransaction.replace(R.id.fragmentContainerViewHome, fragment, fragmentClass.getSimpleName())
//                    .setReorderingAllowed(true)
//                    .addToBackStack(fragmentClass.getSimpleName())
//                    .commit();
//        } catch (IllegalAccessException | InstantiationException e) {
//            Log.e("MyMedsFragments", "Error creating fragment", e);
//        }
//    }
    /* Fragment Management */

    /* Activity Open */
    private void openIntent(Class<?> activity) {
        NavigationHelper.getInstance().openIntent(HomeActivity_prebasse.this, activity);
    }

    private void openIntent(Class<?> activity, int fragmentId) {
        NavigationHelper.getInstance().openIntent(HomeActivity_prebasse.this, activity, fragmentId);
    }
//    private void openIntent(Class<?> activity) {
//        Intent intent = new Intent(HomeActivity.this, activity);
//        startActivity(intent);
//    }
    /* Activity Open */


    /* Default Toolbar additional Menu Inflation */
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.user_toolbar_menu, menu);  // Inflate the menu
    //    return true;
    //}
    /* Default Toolbar additional Menu Inflation */


    /* Default Toolbar Drawer Toggle */
    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //    if (drawerToggle.onOptionsItemSelected(item)) {
    //        return true;
    //    } else {
    /* Default Toolbar additional Menu Logic */
    //if (item.getItemId() == R.id.action_home) {
    //    // showFragment(HomeFragment.class);
    //    openIntent(AuthActivity.class);
    //    return true;
    //}
    /* Default Toolbar additional Menu Logic */
    //        return super.onOptionsItemSelected(item);
    //    }
    //}
    /* Default Toolbar Drawer Toggle */

    /* Default Toolbar Back-press Configuration */
    //private class HomeOnBackPressedCallback extends OnBackPressedCallback {
    //    public HomeOnBackPressedCallback() {
    //        super(true);
    //    }
    //    @Override
    //    public void handleOnBackPressed() {
    //        if (drawerLayout.isDrawerOpen(navigationView)) {
    //            drawerLayout.closeDrawers();
    //        } else if (fragmentManager.getBackStackEntryCount() > 1) {
    //            fragmentManager.popBackStack();
    //        } else {
    //            finish(); // Default behavior to exit the activity
    //        }
    //    }
    //}
    /* Default Toolbar Back-press Configuration */
}
