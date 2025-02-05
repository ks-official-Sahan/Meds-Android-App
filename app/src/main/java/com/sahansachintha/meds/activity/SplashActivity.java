package com.sahansachintha.meds.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sahansachintha.meds.MainActivity;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.SQLiteHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        runSplash();
    }

    /* Activity Open */
    private void openItent(Class<?> activity) {
        Intent intent = new Intent(SplashActivity.this, activity);
        startActivity(intent);
    }
    /* Activity Open */

    /* SQLite */
    private void sqliteInsert() {
        new Thread(() -> {
            try {
                //SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1);
                //SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                }
                sqLiteDatabase.execSQL("INSERT INTO `user`(`name`, `mobile`, `city`)  VALUES('Sahan', '0768701148', 'Tangalle')");

                Log.i("MyMedLog", "New user inserted");
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }

    private void sqliteOOInsert() {
        new Thread(() -> {
            try {
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("name", "Sahan");
                contentValues.put("mobile", "0763991148");
                contentValues.put("city", "Tangalle");

                long id = sqLiteDatabase.insert("user", null, contentValues);
                //sqLiteDatabase.execSQL("INSERT INTO `user`(`name`, `mobile`, `city`)  VALUES('Sahan', '0768701148', 'Tangalle')");

                Log.i("MyMedLog", "New user inserted with ID: " + id);
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }

    private void sqliteUpdate() {
        new Thread(() -> {
            try {
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                }

                sqLiteDatabase.execSQL("UPDATE `user` SET `name`='Sachin' WHERE `id`='1'");

                Log.i("MyMedLog", "Users with id=1 updated");
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }

    private void sqliteOOUpdate() {
        new Thread(() -> {
            try {
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("name", "Sachin");

                int updatedRowCount = sqLiteDatabase.update("user", contentValues, "id=?", new String[]{"1"});
                //sqLiteDatabase.execSQL("UPDATE `user` SET `name`='Sachin' WHERE `id`='1'");

                Log.i("MyMedLog", "Users with id=1 updated: " + updatedRowCount);
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }

    private void sqliteSearch() {
        new Thread(() -> {
            try {
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                }

                @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM `user`", new String[]{});

                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    Log.i("App16Log", name);
                }
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }

    private void sqliteOOSearch() {
        new Thread(() -> {
            try {
                SQLiteDatabase sqLiteDatabase;
                try (SQLiteHelper sqLiteHelper = new SQLiteHelper(SplashActivity.this, "MyMed.db", null, 1)) {
                    sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                }

                String[] projection = new String[]{"id", "name", "city"};
                String selection = "id=? AND name=?";
                String[] selectionArgs = new String[]{"1", "Sahan"};

                @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query(
                        "user",
                        projection,
                        selection,
                        selectionArgs,
                        null, null, null);

                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    Log.i("App16Log", name);
                }
            } catch (Exception e) {
                Log.e("MyMedLog", e.getMessage(), e);
            }
        }).start();
    }
    /* SQLite */

    /* Runtime Permissions */
    private void getRuntimePermission() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 0);
        }
    }
    /* Runtime Permissions */

    /* Notifications */
    private void pushNotification(View view) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // API 26+
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel = new NotificationChannel("nc1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);

        notificationManager.createNotificationChannel(notificationChannel);
        //}
        // API 26 +

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                SplashActivity.this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "Click Me",
                pendingIntent
        ).build();

        Notification notification = new NotificationCompat.Builder(SplashActivity.this, "nc1")
                .setContentTitle("Hello")
                .setContentText("This is a Sample Notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(action)
                .build();

        notificationManager.notify(1, notification);
    }
    /* Notifications */

    /* Static Animations */
    private void runAnimation() {
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.animation_1);
        findViewById(R.id.imageView).startAnimation(animation);
    }
    /* Static Animations */

    /* Dynamic Animations */
    AtomicBoolean isReset = new AtomicBoolean(false);

    private void runSpringAnimation(View view) {
        ImageView imageView = findViewById(R.id.imageView);

        SpringAnimation springAnimation = new SpringAnimation(imageView, DynamicAnimation.TRANSLATION_Y);

        SpringForce springForce = new SpringForce();
        springForce.setStiffness(SpringForce.STIFFNESS_MEDIUM);
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
        if (isReset.get()) {
            springForce.setFinalPosition(0f);
            isReset.set(false);
        } else {
            springForce.setFinalPosition(400f);
            isReset.set(true);
        }

        springAnimation.setSpring(springForce);
        springAnimation.start();
    }

    private void runSplash() {
        //WebView webView1 = findViewById(R.id.webView);
        //webView1.getSettings().setJavaScriptEnabled(true);
        //webView1.loadUrl("https://www.thewitcher.com/us/en/");

        new Handler().postDelayed(() -> {
            //Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            //startActivity(intent);
            openItent(HomeActivity.class);
            finish();
        }, 3000);
    }

    private void runFlingAnimation(View view) {
        ImageView imageView = findViewById(R.id.imageView);

        FlingAnimation flingAnimation = new FlingAnimation(imageView, DynamicAnimation.TRANSLATION_Y);

        flingAnimation.setStartVelocity(1000f);
        flingAnimation.setFriction(1f);

        flingAnimation.start();
    }
    /* Dynamic Animations */

    /* Fragment Management */
    private void showFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.fragmentContainerView1, ProductFragment.class, null)
        //        .setReorderingAllowed(true)
        //        .commit();
    }
    /* Fragment Management */

    private void createFile() {
        try {
            File f = new File("data/data/com.sahansachintha.meds/files/DATA" + (int) (Math.random() * 10000) + ".txt");
            if (f.createNewFile()) {
                Log.i("MyMedsLog", "File Created");
            } else {
                Log.i("MyMedsLog", "File Already Exists");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /* Custom Popups */
//    protected void viewCustomToast(String text) {
//        Toast t = new Toast(SplashActivity.this);
//
//        LayoutInflater inflater = LayoutInflater.from(SplashActivity.this);
//        View toastView = inflater.inflate(R.layout.toast_layout_1, null, false);
//
//        TextView textView2 = toastView.findViewById(R.id.textView2);
//        textView2.setText(text);
//
//        t.setView(toastView);
//        t.setGravity(Gravity.CENTER, 0, 0);
//        t.show();
//    }
//
//    protected void viewCustomAlert(String title, String text) {
//        LayoutInflater inflater = LayoutInflater.from(SplashActivity.this);
//        View alerView = inflater.inflate(R.layout.alert_layout_1, null, false);
//
//        TextView textView3 = alerView.findViewById(R.id.textView3);
//        textView3.setText(title);
//        TextView textView4 = alerView.findViewById(R.id.textView4);
//        textView4.setText(text);
//
//        new AlertDialog.Builder(SplashActivity.this).setView(alerView).show();
//    }
//
//    private void viewCustomSnackbar(View v) {
//        // View layoutView = findViewById(R.id.main);
//        View layoutView = findViewById(R.id.layout1);
//        Snackbar.make(layoutView, "Snackbar", Snackbar.LENGTH_LONG)
//                .setAction("OK", view -> {
//                    Log.i("App19", "Log 1");
//                })
//                .show();
//    }
    /* Custom Popups */


    /* Shared Preferences */
    private void storeSharedPreference() {
        Gson gson = new Gson();

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Sahan1");
        stringArrayList.add("Sahan2");
        stringArrayList.add("Sahan3");
        stringArrayList.add("Sahan4");

        SharedPreferences sharedPreferences = getSharedPreferences("com.sahansachintha.meds.data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stringJson", gson.toJson(stringArrayList));
        editor.apply();
    }

    private void getSharedPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.sahansachintha.meds.data", Context.MODE_PRIVATE);
        String stringJson = sharedPreferences.getString("stringJson", null);

        if (stringJson == null) {
            Toast.makeText(this, "Contact List Not Found!", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> stringArrayList = gson.fromJson(stringJson, type);

            //RecyclerView recyclerView = findViewById(R.id.recyclerView1);

            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            //recyclerView.setLayoutManager(linearLayoutManager);

            //recyclerView.setAdapter(new StringAdapter(stringArrayList));
        }
    }
    /* Shared Preferences */
}

class StringAdapter extends RecyclerView.Adapter<StringViewHolder> {

    ArrayList<String> stringArrayList;

    public StringAdapter(ArrayList<String> stringArrayList) {
        this.stringArrayList = stringArrayList;
    }

    @NonNull
    @Override
    public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View contactView = layoutInflater.inflate(R.layout.reminder_item, parent, false);
        return new StringViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
        String contact = stringArrayList.get(position);
        //holder.textViewLetter.setText(String.valueOf(contact.getFirstName().charAt(0)));
        holder.textViewName.setText(contact);

        holder.button.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + contact));
            view.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }
}

class StringViewHolder extends RecyclerView.ViewHolder {

    TextView textViewName;
    Button button;

    public StringViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textView);
        button = itemView.findViewById(R.id.button);
    }

}
