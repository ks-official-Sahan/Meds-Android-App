package com.sahansachintha.meds.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.Medication;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;

    private final Context context;

    public MedicationAdapter(List<Medication> medicationList, Context context) {
        this.medicationList = medicationList;
        this.context = context;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);

        holder.medicationId.setText(String.valueOf(medication.getId()));
        holder.medicationName.setText(medication.getName());
        holder.medicationNote.setText(medication.getNotes());
        holder.medicationTime.setText(medication.getTime());
        holder.medicationDosage.setText(medication.getDosage());

        //if (medication.getImg()) {
        //} else {
            holder.medicationImg.setImageResource(R.drawable.ic_medicine);
            holder.medicationImg.setImageTintMode(PorterDuff.Mode.SRC_IN);
        //}
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {

        TextView medicationId;
        TextView medicationName;
        TextView medicationNote;
        TextView medicationTime;
        TextView medicationDosage;

        ImageView medicationImg;
        MaterialCardView medicationHolderCard;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationId = itemView.findViewById(R.id.medication_id);
            medicationName = itemView.findViewById(R.id.medication_name);
            medicationNote = itemView.findViewById(R.id.medication_note);
            medicationTime = itemView.findViewById(R.id.medication_time);
            medicationDosage = itemView.findViewById(R.id.medication_dosage);
            medicationImg = itemView.findViewById(R.id.medication_img);
            medicationHolderCard = itemView.findViewById(R.id.medication_holder_card);
        }
    }
}
