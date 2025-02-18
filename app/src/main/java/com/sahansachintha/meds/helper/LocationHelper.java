package com.sahansachintha.meds.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private static LocationHelper locationHelper;

    private LocationHelper() {
    }

    public static LocationHelper getInstance() {
        if (locationHelper == null) {
            locationHelper = new LocationHelper();
        }
        return locationHelper;
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(Context context) {
        if (!PermissionHelper.getLocationPermission(context)) {
            Log.e("MyMedsLocation", "Location permission not granted!");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d("MyMedsLocation", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                        Toast.makeText(context, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.w("MyMedsLocation", "Failed to get location.");
                    }
                });
    }
}
