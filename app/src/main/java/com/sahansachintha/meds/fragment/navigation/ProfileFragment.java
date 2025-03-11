package com.sahansachintha.meds.fragment.navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.model.User;
import com.sahansachintha.meds.network.ProfileService;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileFragment extends Fragment {

    private static final String TAG = "MyMedsProfileFragment";
    private static final long ANIMATION_DURATION = 1500;

    private View rootView;
    private EditText email, name, address, city, country, mobile, location, password;
    private ImageView profileImg;
    private TextView nameText;
    private MaterialButton editBtn;
    private ProgressBar progressBar;

    private final AtomicBoolean isEditing = new AtomicBoolean(false);

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBar = rootView.findViewById(R.id.profile_progress_bar);

        final View profileContainer = rootView.findViewById(R.id.profile_container);
        profileContainer.setVisibility(View.INVISIBLE);
        profileContainer.post(() -> performCircularReveal(profileContainer));

        initViews();
        setUserData(AppHelper.getInstance().getUserModel());
        loadUserProfile();
        setupEditButton();
        setupLocationPicker();

        return rootView;
    }

    private void performCircularReveal(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int centerX = view.getWidth() / 2;
            int centerY = view.getHeight() / 2;
            float finalRadius = (float) Math.hypot(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0, finalRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            view.setVisibility(View.VISIBLE);
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        } else {
            // Fallback for older devices
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        }
    }

    private void initViews() {
        email = rootView.findViewById(R.id.profile_email);
        name = rootView.findViewById(R.id.profile_name_field);
        address = rootView.findViewById(R.id.profile_address);
        city = rootView.findViewById(R.id.profile_city);
        country = rootView.findViewById(R.id.profile_country);
        mobile = rootView.findViewById(R.id.profile_mobile);
        password = rootView.findViewById(R.id.profile_password);
        location = rootView.findViewById(R.id.profile_location);
        profileImg = rootView.findViewById(R.id.profile_img);
        nameText = rootView.findViewById(R.id.profile_name);
        editBtn = rootView.findViewById(R.id.profile_edit_btn);

        // Hide password input layout if not required
        rootView.findViewById(R.id.textInputLayoutPassword).setVisibility(View.GONE);

        // Set default alpha for non-editable fields
        setFieldsAlpha(1f);
    }

    private void setupEditButton() {
        editBtn.setOnClickListener(v -> {
            // Ripple effect: scale button briefly on click
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(editBtn, "scaleX", 0.95f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(editBtn, "scaleY", 0.95f, 1f);
            AnimatorSet rippleAnim = new AnimatorSet();
            rippleAnim.playTogether(scaleX, scaleY);
            rippleAnim.setDuration(150);
            rippleAnim.start();

            if (isEditing.get()) {
                updateProfile();
            } else {
                toggleEditing(true);
            }
        });
    }

    private void updateProfile() {
        showProgress(true);
        String newName = name.getText().toString().trim();
        String newMobile = mobile.getText().toString().trim();
        String newAddress = address.getText().toString().trim();
        String newCity = city.getText().toString().trim();
        String newCountry = country.getText().toString().trim();
        String newLocation = location.getText().toString().trim();
        // Adjust profileImage string if needed
        String newProfileImage = "";

        new ProfileService().updateProfile(newName, newMobile, newAddress, newCity, newCountry, newProfileImage, newLocation,
                new ProfileService.UpdateProfileCallback() {
                    @Override
                    public void onSuccess(User updatedUser) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            showProgress(false);
                            Snackbar.make(rootView, "Profile updated successfully", Snackbar.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            showProgress(false);
                            Snackbar.make(rootView, "Profile update failed: " + errorMessage, Snackbar.LENGTH_LONG).show();
                        });
                    }
                });

        toggleEditing(false);

        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    private void toggleEditing(boolean editing) {
        isEditing.set(editing);
        editBtn.setText(editing ? R.string.save : R.string.edit);
        animateFieldTransition(editing);

        if (editing) {
            name.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void animateFieldTransition(boolean enable) {
        // Fade and slide animation for fields when toggling edit mode
        float startAlpha = enable ? 0.8f : 1f;
        float endAlpha = enable ? 1f : 0.8f;
        float translation = enable ? 20f : 0f;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createFieldAnimator(email, startAlpha, endAlpha, translation),
                createFieldAnimator(name, startAlpha, endAlpha, translation),
                createFieldAnimator(address, startAlpha, endAlpha, translation),
                createFieldAnimator(city, startAlpha, endAlpha, translation),
                createFieldAnimator(country, startAlpha, endAlpha, translation),
                createFieldAnimator(mobile, startAlpha, endAlpha, translation),
                createFieldAnimator(password, startAlpha, endAlpha, translation),
                createFieldAnimator(location, startAlpha, endAlpha, translation)
        );
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setFieldsEnabled(enable);
            }
        });
        animatorSet.start();
    }

    private AnimatorSet createFieldAnimator(View view, float startAlpha, float endAlpha, float translationY) {
        view.setAlpha(startAlpha);
        view.setTranslationY(startAlpha < endAlpha ? translationY : 0f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha);
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(view, "translationY", translationY, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, translationAnim);
        return set;
    }

    private void setFieldsEnabled(boolean enabled) {
        email.setEnabled(enabled);
        name.setEnabled(enabled);
        address.setEnabled(enabled);
        city.setEnabled(enabled);
        country.setEnabled(enabled);
        mobile.setEnabled(enabled);
        password.setEnabled(enabled);
        location.setEnabled(enabled);
        setFieldsAlpha(enabled ? 1f : 0.8f);
    }

    private void setFieldsAlpha(float alpha) {
        email.setAlpha(alpha);
        name.setAlpha(alpha);
        address.setAlpha(alpha);
        city.setAlpha(alpha);
        country.setAlpha(alpha);
        mobile.setAlpha(alpha);
        password.setAlpha(alpha);
        location.setAlpha(alpha);
    }

    private void setupLocationPicker() {
        location.setOnClickListener(v -> {
            MapPickerDialogFragment mapDialog = new MapPickerDialogFragment();
            parseInitialLocation(mapDialog);
            mapDialog.setOnLocationSelectedListener(latLng ->
                    location.setText(latLng.latitude + ", " + latLng.longitude));
            mapDialog.show(getChildFragmentManager(), "mapPicker");
        });
    }

    private void parseInitialLocation(MapPickerDialogFragment mapDialog) {
        String locText = location.getText().toString().trim();
        if (!locText.isEmpty()) {
            String[] parts = locText.split(",");
            if (parts.length == 2) {
                try {
                    double lat = Double.parseDouble(parts[0].trim());
                    double lng = Double.parseDouble(parts[1].trim());
                    mapDialog.setInitialLocation(new LatLng(lat, lng));
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Invalid location format: " + locText);
                }
            }
        }
    }

    private void setUserData(User user) {
        if (user == null) return;
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
        if (user.getLocation() != null) {
            location.setText(user.getLocation());
        }
        if (user.getProfileImage() != null) {
            Glide.with(requireContext())
                    .load(user.getProfileImage())
                    .placeholder(R.drawable.ic_profile)
                    .into(profileImg);
        }
    }

    private void loadUserProfile() {
        showProgress(true);
        new ProfileService().loadProfile(new ProfileService.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    showProgress(false);
                    if (user == null) {
                        Snackbar.make(rootView, "User data is null", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Log.d(TAG, "User data: " + user);
                    setUserData(user);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    showProgress(false);
                    if (errorMessage.trim().equalsIgnoreCase("HTTP code: 401")) {
                        Snackbar.make(rootView, "Your login has expired. Please login again.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(rootView, "Failed to load profile: " + errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBar == null) return;
        progressBar.animate().cancel();
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setAlpha(0f);
            progressBar.animate().alpha(1f).setDuration(300).start();
        } else {
            progressBar.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }
}
