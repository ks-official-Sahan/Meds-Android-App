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
import com.sahansachintha.meds.activity.HomeActivity;
import com.sahansachintha.meds.activity.StoreActivity;
import com.sahansachintha.meds.activity.auth.AuthActivity;
import com.sahansachintha.meds.fragment.navigation.CartFragment;
import com.sahansachintha.meds.fragment.navigation.HomeFragment;
import com.sahansachintha.meds.fragment.navigation.MedicationsFragment;
import com.sahansachintha.meds.fragment.navigation.OrdersFragment;
import com.sahansachintha.meds.fragment.navigation.ProfileFragment;
import com.sahansachintha.meds.fragment.navigation.RemindersFragment;
import com.sahansachintha.meds.fragment.navigation.SettingFragment;
import com.sahansachintha.meds.fragment.navigation.StoreFragment;

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
    private final Map<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<>() {{
        put(R.id.nav_item_home, HomeFragment.class);
        put(R.id.menu_item_home, HomeFragment.class);
        put(R.id.nav_item_store, StoreFragment.class);
        put(R.id.menu_item_store, StoreFragment.class);
        put(R.id.nav_item_reminders, RemindersFragment.class);
        put(R.id.menu_item_reminders, RemindersFragment.class);
        put(R.id.nav_item_medications, MedicationsFragment.class);
        put(R.id.menu_item_medications, MedicationsFragment.class);
        put(R.id.nav_item_cart, CartFragment.class);
        put(R.id.menu_item_cart, CartFragment.class);
        put(R.id.nav_item_orders, OrdersFragment.class);
        put(R.id.menu_item_orders, OrdersFragment.class);
        put(R.id.nav_item_profile, ProfileFragment.class);
        put(R.id.nav_item_setting, SettingFragment.class);
    }};
    private final Map<Integer, Class<?>> activityMap = new HashMap<>() {{
        put(R.id.nav_item_logout, AuthActivity.class);
    }};

    public interface FragmentCallback {
        void showFragment(@NonNull Class<? extends Fragment> fragmentClass);
    }

    public interface ActivityCallback {
        void openIntent(@NonNull Class<?> activity);
    }

    private boolean isHome(int id) {
        return id == R.id.nav_item_home || id == R.id.menu_item_home;
    }
    private boolean isStore(int id) {
        return id == R.id.nav_item_store || id == R.id.menu_item_store;
    }

    public boolean runNavigation(int itemID, FragmentCallback fragmentCallback, ActivityCallback activityCallback, int selectedMenuId) {
        /* Handle Home <-> Store Activity switching */
        if ((isHome(itemID) && isStore(selectedMenuId))) {
            activityCallback.openIntent(HomeActivity.class);
            return true;
        }
        if ((isStore(itemID) && isHome(selectedMenuId))) {
            activityCallback.openIntent(StoreActivity.class);
            return true;
        }
        /* Handle Home <-> Store Activity switching */

        if (fragmentMap.containsKey(itemID)) {
            fragmentCallback.showFragment(Objects.requireNonNull(fragmentMap.get(itemID)));
            return true;
        } else if (activityMap.containsKey(itemID)) {
            activityCallback.openIntent(Objects.requireNonNull(activityMap.get(itemID)));
            return true;
        }

        return false;
    }

    public boolean runNavigation(int itemID, FragmentCallback fragmentCallback, ActivityCallback activityCallback) {
        return runNavigation(itemID, fragmentCallback, activityCallback, -1);
    }
    /* Manage Navigation Logic */

}
