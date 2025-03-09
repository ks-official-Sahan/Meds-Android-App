package com.sahansachintha.meds.adapters;

import com.sahansachintha.meds.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sahansachintha.meds.helper.AppHelper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private final List<LocalDate> dates;
    private LocalDate selectedDate;

    private final Context context;

    private final OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(LocalDate date);
    }

    public DateAdapter(Context context, List<LocalDate> dates, LocalDate selectedDate, OnDateClickListener listener) {
        this.context = context;
        this.dates = dates;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context != null ? context : parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_holder_date, parent, false);

        return new DateViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        LocalDate date = dates.get(position);

        holder.textDate.setText(String.valueOf(date.getDayOfMonth()));
        holder.textDay.setText(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        //dateViewHolder.textMonth.setText(date.getMonth().toString().substring(0, 3));
        holder.textMonth.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()));

        boolean isSelected = date.equals(selectedDate);

        holder.cardView.setSelected(isSelected);
        if (isSelected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //dateViewHolder.cardView.setOutlineAmbientShadowColor(context.getResources().getColor(R.color.primary_light, null));
                holder.cardView.setOutlineAmbientShadowColor(getThemeColor(context, R.attr.colorPrimary300));
                holder.cardView.setOutlineSpotShadowColor(getThemeColor(context, R.attr.colorPrimary300));
            }
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(getThemeColor(context, R.attr.colorPrimary300));
            holder.cardView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start();

            holder.textMonth.setTextColor(getThemeColor(context, R.attr.colorPrimary400));
            holder.textDate.setTextColor(getThemeColor(context, R.attr.colorPrimary400));
            holder.textDay.setTextColor(getThemeColor(context, R.attr.colorPrimary400));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                holder.cardView.setOutlineAmbientShadowColor(getThemeColor(context, R.attr.colorNeutral400));
                holder.cardView.setOutlineSpotShadowColor(getThemeColor(context, R.attr.colorNeutral400));
            }
            holder.cardView.animate().scaleX(1f).scaleY(1f).setDuration(500).start();

            if (date.equals(LocalDate.now())) {
                holder.cardView.setStrokeWidth(1);
                holder.cardView.setStrokeColor(getThemeColor(context, R.attr.colorAccent));

                holder.textMonth.setTextColor(getThemeColor(context, R.attr.colorSecondary300));
                holder.textDate.setTextColor(getThemeColor(context, R.attr.colorSecondary300));
                holder.textDay.setTextColor(getThemeColor(context, R.attr.colorSecondary300));
            } else {
                holder.cardView.setStrokeWidth(0);
                holder.cardView.setStrokeColor(getThemeColor(context, R.attr.colorNeutral300));

                holder.textMonth.setTextColor(getThemeColor(context, com.google.android.material.R.attr.colorOnSurface));
                holder.textDate.setTextColor(getThemeColor(context, com.google.android.material.R.attr.colorOnSurface));
                holder.textDay.setTextColor(getThemeColor(context, com.google.android.material.R.attr.colorOnSurface));
            }
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateClick(date);
            }
            setSelectedDate(date);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedDate(LocalDate newDate) {
        selectedDate = newDate;
        notifyDataSetChanged();
    }

    private int getThemeColor(Context context, int attribute) {
        return AppHelper.getInstance().getThemeColor(context, attribute);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textDay;
        TextView textMonth;
        MaterialCardView cardView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textDay = itemView.findViewById(R.id.textDay);
            textMonth = itemView.findViewById(R.id.textMonth);
            cardView = itemView.findViewById(R.id.date_holder_card);
        }
    }
}
