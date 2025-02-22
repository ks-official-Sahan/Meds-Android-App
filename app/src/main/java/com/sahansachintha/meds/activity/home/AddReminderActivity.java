package com.sahansachintha.meds.activity.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.data.ReminderManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupDatePicker();
        setupTimePicker();
        setupRepeatPicker();

        findViewById(R.id.add_reminder_cancel_btn).setOnClickListener(v -> finish());
        findViewById(R.id.add_reminder_save_btn).setOnClickListener(v -> saveReminder());
    }

    private void setupRepeatPicker() {
        EditText repeatText = findViewById(R.id.add_reminder_repeat1);
        repeatText.setEnabled(false);
        repeatText.setText(String.valueOf(0));

        repeatText.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
            NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(100);
            numberPicker.setWrapSelectorWheel(false);

            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> {
                int selectedNumber = numberPicker.getValue();
                repeatText.setText(String.valueOf(selectedNumber)); // Set selected number to EditText
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    private void setupDatePicker() {
        EditText dateText = findViewById(R.id.add_reminder_date);
        EditText repeatText = findViewById(R.id.add_reminder_repeat1);
        TextView endDateText = findViewById(R.id.add_reminder_end_date);

        /* Android Default */
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,
//                (view, year, month, dayOfMonth) -> {
//                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
//                    dateText.setText(selectedDate);
//                },
//                2024, 0, 1 // Default year, month, day
//        );
//        //datePickerDialog.show();
//        dateText.setOnClickListener(v -> datePickerDialog.show());

        /* Material */
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default: Today
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH); // 0-based
            this.day = calendar.get(Calendar.DAY_OF_MONTH);

            dateText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));

            repeatText.setEnabled(true);
            repeatText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    calculateEndDate(calendar, s.toString(), endDateText);
                }
            });

            calculateEndDate(calendar, repeatText.getText().toString(), endDateText);
        });

        //datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        dateText.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
    }

    private void calculateEndDate(Calendar selectedDateTime, String repeatValue, TextView endDateText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        if (repeatValue.isEmpty()) {
            endDateText.setText(sdf.format(selectedDateTime.getTime()));
            return;
        }

        try {
            int repeatDays = Integer.parseInt(repeatValue);
            if (repeatDays == 0) {
                endDateText.setText(sdf.format(selectedDateTime.getTime()));
                return;
            }

            Calendar calendar = (Calendar) selectedDateTime.clone();
            calendar.add(Calendar.DAY_OF_MONTH, repeatDays); // Add repeat days

            endDateText.setText(sdf.format(calendar.getTime())); // Set calculated end date with time
        } catch (NumberFormatException e) {
            endDateText.setText("Invalid repeat value"); // Handle invalid input
        }
    }

    private void setupTimePicker() {
        EditText timeText = findViewById(R.id.add_reminder_time);
        EditText repeatText = findViewById(R.id.add_reminder_repeat1);
        TextView endDateText = findViewById(R.id.add_reminder_end_date);

        /* Android Default */
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                this,
//                (view, hourOfDay, minute) -> {
//                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
//                    timeText.setText(selectedTime);
//                },
//                12, 0, true // Default hour, minute, and 24-hour format
//        );
//        //timePickerDialog.show();
//        timeText.setOnClickListener(v -> timePickerDialog.show());

        /* Material */
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            this.hour = timePicker.getHour();
            this.minute = timePicker.getMinute();

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            timeText.setText(selectedTime);

            Calendar calendar = Calendar.getInstance();
            calendar.set(this.year, this.month, this.day, this.hour, this.minute);

            calculateEndDate(calendar, repeatText.getText().toString(), endDateText);
        });
        //timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        timeText.setOnClickListener(v -> timePicker.show(getSupportFragmentManager(), "TIME_PICKER"));
    }

    private void saveReminder() {
        EditText titleText = findViewById(R.id.add_reminder_title);
        EditText descriptionText = findViewById(R.id.add_reminder_description);
        EditText repeatText = findViewById(R.id.add_reminder_repeat1);
        TextView endDateText = findViewById(R.id.add_reminder_end_date);
        EditText dateText = findViewById(R.id.add_reminder_date);
        EditText timeText = findViewById(R.id.add_reminder_time);

        Calendar startCalender = Calendar.getInstance();
        startCalender.set(this.year, this.month, this.day, this.hour, this.minute);

        if (!("0").equalsIgnoreCase(repeatText.getText().toString())) {
            try {
                Date endDate = DateFormat.getDateInstance().parse(endDateText.getText().toString());
                assert endDate != null;

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);
                endCalendar.getTimeInMillis();

                // TODO implement repeat
                ReminderManager.getInstance().addReminder(ReminderManager.generateReminderId(), titleText.getText().toString(), startCalender.getTimeInMillis(), descriptionText.toString());
            } catch (ParseException e) {
                Log.e("MyMedsAddReminder", "End Date Parse Error");
            }
        } else {
            ReminderManager.getInstance().addReminder(ReminderManager.generateReminderId(), titleText.getText().toString(), startCalender.getTimeInMillis(), descriptionText.toString());
            //ReminderManager.getInstance().addReminder(ReminderManager.generateReminderId(), titleText.getText().toString(), this.year, this.month, this.day, this.hour, this.minute, descriptionText.toString());
        }

        Toast.makeText(this, "Reminder saved! ", Toast.LENGTH_SHORT).show();

    }

}