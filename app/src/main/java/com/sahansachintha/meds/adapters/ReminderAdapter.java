package com.sahansachintha.meds.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.model.Reminder;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    private final Context context;

    private OnReminderClickListener listener;

    public interface OnReminderClickListener {
        void onReminderClick(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    public ReminderAdapter(List<Reminder> reminderList, Context context, OnReminderClickListener listener) {
        this.reminderList = reminderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        holder.reminderId.setText(String.valueOf(reminder.getId()));
        holder.reminderTitle.setText(reminder.getTitle());
        holder.reminderNote.setText(reminder.getNotes());
        holder.reminderTime.setText(getFormattedTime(reminder.getCalendar().getTime()));
        //if (reminder.getImg()) {
        //} else {
            holder.reminderImg.setImageResource(R.drawable.ic_reminder);
            holder.reminderImg.setImageTintMode(PorterDuff.Mode.SRC_IN);
        //}
        holder.reminderHolderCard.setOnClickListener(v -> {
            Toast.makeText(context, reminder.getTitle(), Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onReminderClick(reminder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Reminder> newReminders) {
        this.reminderList = newReminders;
        notifyDataSetChanged();
    }

    private String getFormattedTime(Date date) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a"); // For 8:30 AM  // For 24-hour format: "HH:mm" (this would give 08:30)
        //return dateFormat.format(date);
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView reminderId;
        TextView reminderTitle;
        TextView reminderNote;
        TextView reminderTime;
        ImageView reminderImg;
        MaterialCardView reminderHolderCard;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderId = itemView.findViewById(R.id.reminder_id);
            reminderTitle = itemView.findViewById(R.id.reminder_title);
            reminderNote = itemView.findViewById(R.id.reminder_note);
            reminderTime = itemView.findViewById(R.id.reminder_time);
            reminderImg = itemView.findViewById(R.id.reminder_img);
            reminderHolderCard = itemView.findViewById(R.id.reminder_holder_card);
        }
    }
}
