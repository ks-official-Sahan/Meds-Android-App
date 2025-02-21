package com.sahansachintha.meds.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.HomeActivity;
import com.sahansachintha.meds.activity.store.ProductViewActivity;
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.activity.auth.AuthActivity;
import com.sahansachintha.meds.fragment.navigation.CartFragment;
import com.sahansachintha.meds.fragment.navigation.HomeFragment;
import com.sahansachintha.meds.fragment.navigation.MedicationsFragment;
import com.sahansachintha.meds.fragment.navigation.OrdersFragment;
import com.sahansachintha.meds.fragment.navigation.ProfileFragment;
import com.sahansachintha.meds.fragment.navigation.RemindersFragment;
import com.sahansachintha.meds.fragment.navigation.SettingFragment;
import com.sahansachintha.meds.fragment.navigation.StoreFragment;
import com.sahansachintha.meds.model.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NavigationHelper {

    private static NavigationHelper navigationHelper;

    private NavigationHelper() {
    }

    public static NavigationHelper getInstance() {
        if (navigationHelper == null) {
            navigationHelper = new NavigationHelper();
        }
        return navigationHelper;
    }

    /* Fragment Management */
    public void showFragment(@NonNull FragmentManager fragmentManager, @IdRes int containerViewId, @NonNull Class<? extends Fragment> fragmentClass) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            Fragment fragment = fragmentClass.newInstance();
            fragmentTransaction.replace(containerViewId, fragment, fragmentClass.getSimpleName())
                    .setReorderingAllowed(true)
                    .addToBackStack(fragmentClass.getSimpleName())
                    .commit();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.e("MyMedsFragments", "Error creating fragment", e);
        }
    }
    /* Fragment Management */

    /* Activity Open */
    public void openIntent(@NonNull Context context, @NonNull Class<?> activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    public void openIntent(@NonNull Context context, @NonNull Class<?> activity, int fragmentId) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("FRAGMENT_ID", fragmentId);
        context.startActivity(intent);
    }

    public void viewProduct(@NonNull Context context, @NonNull Product product) {
        Intent intent = new Intent(context, ProductViewActivity.class);
        intent.putExtra("product", product);
        context.startActivity(intent);
    }

    public void viewProfile(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("FRAGMENT_ID", R.id.nav_item_profile);
        context.startActivity(intent);
    }

    public void makeCall(Context context) {
        boolean granted = PermissionHelper.requestPermission(context, "android.permission.CALL_PHONE");
        if (granted) {
            Intent i = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:+94768701148");

            i.setData(uri);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    /* Activity Open */

    /* Manage Navigation Logic */
    /* Fragment Mapping */
    private final Map<Integer, Class<? extends Fragment>> homeFragments = new HashMap<>() {{
        put(R.id.nav_item_home, HomeFragment.class);
        put(R.id.menu_item_home, HomeFragment.class);
        put(R.id.nav_item_reminders, RemindersFragment.class);
        put(R.id.menu_item_reminders, RemindersFragment.class);
        put(R.id.nav_item_medications, MedicationsFragment.class);
        put(R.id.menu_item_medications, MedicationsFragment.class);
    }};

    private final Map<Integer, Class<? extends Fragment>> storeFragments = new HashMap<>() {{
        put(R.id.nav_item_store, StoreFragment.class);
        put(R.id.menu_item_store, StoreFragment.class);
        put(R.id.nav_item_cart, CartFragment.class);
        put(R.id.menu_item_cart, CartFragment.class);
        put(R.id.nav_item_orders, OrdersFragment.class);
        put(R.id.menu_item_orders, OrdersFragment.class);
    }};

    private final Map<Integer, Class<? extends Fragment>> sharedFragments = new HashMap<>() {{
        put(R.id.nav_item_profile, ProfileFragment.class);
        put(R.id.nav_item_setting, SettingFragment.class);
    }};

    private final Map<Integer, Class<?>> activityMap = new HashMap<>() {{
        put(R.id.nav_item_logout, AuthActivity.class);
    }};

    /* Identify Home-Related Items */
    private boolean isHomeRelated(int id) {
        return homeFragments.containsKey(id);
    }

    /* Identify Store-Related Items */
    private boolean isStoreRelated(int id) {
        return storeFragments.containsKey(id);
    }

    public boolean runNavigation(int itemID, FragmentCallback fragmentCallback, ActivityCallback activityCallback, int selectedMenuId) {
        // Handle Home <-> Store Activity switching
        if (isHomeRelated(itemID) && isStoreRelated(selectedMenuId)) {
            activityCallback.openIntent(HomeActivity.class, itemID);
            return true;
        }
        if (isStoreRelated(itemID) && isHomeRelated(selectedMenuId)) {
            activityCallback.openIntent(StoreActivity.class, itemID);
            return true;
        }

        // Open shared fragments in both activities
        if (sharedFragments.containsKey(itemID)) {
            fragmentCallback.showFragment(Objects.requireNonNull(sharedFragments.get(itemID)));
            return true;
        }

        // Handle normal navigation for each activity
        if (homeFragments.containsKey(itemID)) {
            fragmentCallback.showFragment(Objects.requireNonNull(homeFragments.get(itemID)));
            return true;
        }
        if (storeFragments.containsKey(itemID)) {
            fragmentCallback.showFragment(Objects.requireNonNull(storeFragments.get(itemID)));
            return true;
        }
        if (activityMap.containsKey(itemID)) {
            activityCallback.openIntent(Objects.requireNonNull(activityMap.get(itemID)), -1);
            return true;
        }

        return false;
    }

    public boolean runNavigation(int itemID, FragmentCallback fragmentCallback, ActivityCallback activityCallback) {
        return runNavigation(itemID, fragmentCallback, activityCallback, -1);
    }

    public Class<? extends Fragment> getFragmentById(int id) {
        if (homeFragments.containsKey(id)) return homeFragments.get(id);
        if (storeFragments.containsKey(id)) return storeFragments.get(id);
        if (sharedFragments.containsKey(id)) return sharedFragments.get(id);
        return HomeFragment.class; // Default
    }

    public interface FragmentCallback {
        void showFragment(@NonNull Class<? extends Fragment> fragmentClass);
    }

    public interface ActivityCallback {
        void openIntent(@NonNull Class<?> activity, int fragmentId);
    }
    /* Manage Navigation Logic */

}
