package com.sahansachintha.meds.fragment.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.User;
import com.sahansachintha.meds.network.ProfileService;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileFragment extends Fragment {

    public static final String TAG = "MyMedsProfileFragment";
    private View view;
    private EditText email, name, address, city, country, mobile, location, password;
    private ImageView profile_img;
    private TextView nameText, editBtn;

    private final AtomicBoolean isEditing = new AtomicBoolean(false);

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        view.findViewById(R.id.profile_container).setAlpha(0.1f);
        view.findViewById(R.id.profile_container).setScaleX(0.2f);
        view.findViewById(R.id.profile_container).setScaleY(0.2f);
        view.findViewById(R.id.profile_container).animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(1500).start();

        email = view.findViewById(R.id.profile_email);
        name = view.findViewById(R.id.profile_name_field);
        address = view.findViewById(R.id.profile_address);
        city = view.findViewById(R.id.profile_city);
        country = view.findViewById(R.id.profile_country);
        mobile = view.findViewById(R.id.profile_mobile);
        profile_img = view.findViewById(R.id.profile_img);

        location = view.findViewById(R.id.profile_location);
        password = view.findViewById(R.id.profile_password);
        view.findViewById(R.id.textInputLayout2).setVisibility(View.GONE);

        nameText = view.findViewById(R.id.profile_name);

        loadUserProfile();

        editBtn = view.findViewById(R.id.profile_edit_btn);
        editBtn.setOnClickListener(v -> {
            if (isEditing.get()) {
                // TODO save updates
                String newName = name.getText().toString().trim();
                String newMobile = mobile.getText().toString().trim();
                String newAddress = address.getText().toString().trim();
                String newCity = city.getText().toString().trim();
                String newCountry = country.getText().toString().trim();
                // For simplicity, profileImage remains an empty string or can be set accordingly.
                String newProfileImage = "";

                ProfileService profileService = new ProfileService();
                profileService.updateProfile(newName, newMobile, newAddress, newCity, newCountry, newProfileImage, new ProfileService.UpdateProfileCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            // Optionally update UI elements with updatedUser data
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Profile update failed: " + errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });

                isEditing.set(false);
                editBtn.setText(R.string.edit);
            } else {
                isEditing.set(true);
                editBtn.setText(R.string.save);
            }
            email.setEnabled(isEditing.get());
            name.setEnabled(isEditing.get());
            address.setEnabled(isEditing.get());
            city.setEnabled(isEditing.get());
            country.setEnabled(isEditing.get());
            mobile.setEnabled(isEditing.get());
            password.setEnabled(isEditing.get());
            //location.setEnabled(isEditing.get());
        });

        location.setOnClickListener(v -> {
            MapPickerDialogFragment mapDialog = new MapPickerDialogFragment();
            String locText = location.getText().toString();
            if (!locText.isEmpty()) {
                String[] parts = locText.split(",");
                if (parts.length == 2) {
                    try {
                        double lat = Double.parseDouble(parts[0].trim());
                        double lng = Double.parseDouble(parts[1].trim());
                        mapDialog.setInitialLocation(new LatLng(lat, lng));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            mapDialog.setOnLocationSelectedListener(latLng ->
                    location.setText(latLng.latitude + ", " + latLng.longitude));
            mapDialog.show(getChildFragmentManager(), "mapPicker");
        });


        return view;
    }

    private void loadUserProfile() {
        ProfileService profileService = new ProfileService();
        profileService.loadProfile(new ProfileService.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (user == null) {
                        Toast.makeText(getContext(), "User data is null", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "User data: " + user);
                    email.setText(user.getEmail());
                    nameText.setText(user.getEmail());
                    if (user.getName() != null) {
                        name.setText(user.getName());
                        nameText.setText(user.getName());
                    }
                    if (user.getAddress() != null) {
                        address.setText(user.getAddress());
                    }
                    if (user.getCity() != null) {
                        city.setText(user.getCity());
                    }
                    if (user.getCountry() != null) {
                        country.setText(user.getCountry());
                    }
                    if (user.getMobile() != null) {
                        mobile.setText(user.getMobile());
                    }
                    if (user.getProfileImage() != null) {
                        Glide.with(requireContext()).load(user.getProfileImage()).placeholder(R.drawable.ic_profile).into(profile_img);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (errorMessage.trim().toLowerCase().contains("HTTP code: 401".trim().toLowerCase())) {
                        Toast.makeText(getContext(), "Your login has been expired: Please Login Again.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), "Failed to load profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}