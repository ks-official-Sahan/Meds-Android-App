package com.sahansachintha.meds.fragment.navigation;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.AddMedicationActivity;
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.adapters.MedicationAdapter;
import com.sahansachintha.meds.helper.GenericSwipeCallback;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.MedicationManager;
import com.sahansachintha.meds.model.Medication;

import java.util.List;

public class MedicationsFragment extends Fragment {
    private static final String TAG = "MedicationsFragment";

    private View rootView;
    private RecyclerView medicationRecycler;
    private List<Medication> medicationData;
    private MedicationAdapter medicationAdapter;
    private GestureDetector gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_medications, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setUpStoreButtons();
        initMedicationRecycler();
        setupFabButton();
    }

    private void setupFabButton() {
        View addFab = rootView.findViewById(R.id.fab_add_medications);
        if (addFab != null) {
            addFab.setOnClickListener(v -> {
                NavigationHelper.getInstance().openIntent(requireContext(), AddMedicationActivity.class);
            });

            // Animate FAB on appearance
            ObjectAnimator.ofFloat(addFab, "scaleX", 0.5f, 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(addFab, "scaleY", 0.5f, 1f).setDuration(300).start();
        }
    }

    private void setUpStoreButtons() {
        View shopButton = rootView.findViewById(R.id.fab_shop_medications);
        if (shopButton != null) {
            shopButton.setOnClickListener(v -> {
                try {
                    NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error opening StoreActivity: " + e.getMessage());
                }
            });
        }
    }

    private void initMedicationRecycler() {
        medicationRecycler = rootView.findViewById(R.id.medications_recycler);
        if (medicationRecycler == null) {
            Log.e(TAG, "medications_recycler not found in layout");
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        medicationRecycler.setLayoutManager(layoutManager);

        medicationData = getMedicationData();
        toggleView(R.id.medications_empty_view, medicationData.isEmpty());

        medicationAdapter = new MedicationAdapter(medicationData, requireContext());
        medicationRecycler.setAdapter(medicationAdapter);

        setupSwipeGesture();
        setupLongPressToDelete();
    }

    private void setupSwipeGesture() {
        GenericSwipeCallback.SwipeActionListener swipeListener = new GenericSwipeCallback.SwipeActionListener() {
            @Override
            public void onSwipeRight(int position) {
                // Confirm deletion on right swipe
                confirmDeleteMedication(position);
            }

            @Override
            public void onSwipeLeft(int position) {
                // Toggle medication status on left swipe
                Medication medication = medicationData.get(position);
                medication.setStatus(medication.getStatus().equalsIgnoreCase("Active") ? "Inactive" : "Active");
                Toast.makeText(requireContext(),
                        "Status changed to " + medication.getStatus(),
                        Toast.LENGTH_SHORT).show();
                medicationAdapter.notifyItemChanged(position);
            }
        };

        GenericSwipeCallback swipeCallback = new GenericSwipeCallback(
                requireContext(),
                swipeListener,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_toggle),
                Color.parseColor("#388E3C"),
                "Toggle",
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete),
                Color.parseColor("#D32F2F"),
                "Delete"
        ) {
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(medicationRecycler);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupLongPressToDelete() {
        gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                View child = medicationRecycler.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int position = medicationRecycler.getChildAdapterPosition(child);
                    confirmDeleteMedication(position);
                }
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                return super.onSingleTapUp(e);
            }
        });

        medicationRecycler.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void confirmDeleteMedication(int position) {
        Medication medication = medicationData.get(position);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Medication")
                .setMessage("Are you sure you want to delete " + medication.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMedication(position))
                .setNegativeButton("Cancel", (dialog, which) -> medicationAdapter.notifyItemChanged(position))
                .show();
    }

    private void deleteMedication(int position) {
        medicationData.remove(position);
        medicationAdapter.notifyItemRemoved(position);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(medicationRecycler.getChildAt(position), "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.start();

        toggleView(R.id.medications_empty_view, medicationData.isEmpty());

        Toast.makeText(requireContext(), "Medication deleted", Toast.LENGTH_SHORT).show();
    }

    private List<Medication> getMedicationData() {
        return MedicationManager.getInstance().getAllMedications();
    }

    private void toggleView(int viewId, boolean visible) {
        View targetView = rootView.findViewById(viewId);
        if (targetView != null) {
            targetView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }
}
