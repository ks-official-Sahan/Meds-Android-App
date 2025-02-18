package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.StoreActivity;
import com.sahansachintha.meds.adapters.MedicationAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.MedicationManager;
import com.sahansachintha.meds.model.Medication;

import java.util.List;

public class MedicationsFragment extends Fragment {
    private View view;

    /* Medication */
    private RecyclerView medicationRecycler;
    private List<Medication> medicationData;
    private MedicationAdapter medicationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_medications, container, false);

        setUpStoreButtons();

        initMedicationRecycler();

        view.findViewById(R.id.fab_add_medications).setOnClickListener(v -> {
            //NavigationHelper.getInstance().openIntent(requireContext(), AddMedicationActivity.class);
            Toast.makeText(getContext(), "Add Medication", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setUpStoreButtons() {
        /* Store Buttons */
//        try {
//            getActivity().findViewById(R.id.fab).setVisibility(View.GONE);
//        } catch (NullPointerException e) {
//            view.findViewById(R.id.fab_shop_medications).setVisibility(View.GONE);
//            Log.e("MyMedsMedications", "Cannot Find Resource with ID R.id.fab in MedicationsFragment");
//        }

        view.findViewById(R.id.fab_shop_medications).setOnClickListener(v -> {
            if (getContext() != null) {
                try {
                    NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class);
                } catch (IllegalStateException e) {
                    Log.e("MyMedsMedications", "Error opening StoreActivity: " + e.getMessage());
                }
            }
        });
    }

    private void initMedicationRecycler() {
        medicationRecycler = view.findViewById(R.id.medications_recycler);

        LinearLayoutManager medicationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        medicationRecycler.setLayoutManager(medicationLayoutManager);

        medicationData = getMedicationData();
        toggleView(R.id.medications_empty_view, medicationData.isEmpty());

        //MedicationAdapter medicationAdapter = new MedicationAdapter(medicationData, getContext());
        medicationAdapter = new MedicationAdapter(medicationData, getContext());
        medicationRecycler.setAdapter(medicationAdapter);
    }

    private List<Medication> getMedicationData() {
        return MedicationManager.getInstance().getAllMedications();
    }

    private void toggleView(int viewId, boolean visible) {
        view.findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    //    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        try {
//            getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
//        } catch (NullPointerException e) {
//            Log.e("MyMedsMedications", "Cannot Find Resource with ID R.id.fab in MedicationsFragment");
//        }
//    }

}