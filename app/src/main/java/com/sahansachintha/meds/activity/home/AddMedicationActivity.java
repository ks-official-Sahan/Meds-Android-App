package com.sahansachintha.meds.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.data.MedicationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AddMedicationActivity extends AppCompatActivity {

    private String status;
    private ImageView medicationImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupStatusSelector();

        findViewById(R.id.add_medication_cancel_btn).setOnClickListener(v -> finish());
        findViewById(R.id.add_medication_save_btn).setOnClickListener(v -> saveMedication());

        medicationImgView = findViewById(R.id.add_medication_img);
        medicationImgView.setOnClickListener(v -> selectImageFromGallery());
    }

    private void setupStatusSelector() {
        AutoCompleteTextView statusSelectorView = findViewById(R.id.medication_status_selector);

        List<String> items = Arrays.asList("Active", "Inactive");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        statusSelectorView.setAdapter(adapter);

        statusSelectorView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            status = selectedItem;
            //Toast.makeText(AddMedicationActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
        });
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        processSelectedImage(selectedImageUri);
                    }
                } else {
                    Log.w("ImageSelection", "Image selection canceled or failed");
                }
            });

    /* Trigger Image Selection */
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    public void processSelectedImage(Uri imageUri) {
        try {
            File savedFile = saveImageToStorage(imageUri);
            loadImageFromStorage(); // Load from storage for better performance
        } catch (IOException e) {
            Log.e("ImageSelection", "Failed to save image", e);
        }
    }

    private void loadImageFromStorage() {
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds/" + fileName);
        img = imageFile.getAbsolutePath();
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(medicationImgView);

            medicationImgView.setBackgroundResource(R.color.transparent);
        }
    }

    private String fileName;
    private String img;

    private File saveImageToStorage(Uri imageUri) throws IOException {
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds");
        if (!directory.exists()) directory.mkdirs();

        fileName = "medication_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(directory, fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream fos = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while (true) {
                assert inputStream != null;
                if ((bytesRead = inputStream.read(buffer)) == -1) break;
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        }

        return imageFile;
    }

    private void saveMedication() {
        EditText nameText = findViewById(R.id.add_medication_name);
        EditText dosageText = findViewById(R.id.add_medication_dosage);
        EditText frequencyText = findViewById(R.id.add_medication_frequency);
        EditText instructionsText = findViewById(R.id.add_medication_instructions);

        String name = nameText.getText().toString();
        String dosage = dosageText.getText().toString();
        String frequency = frequencyText.getText().toString();
        String instructions = instructionsText.getText().toString();
        String status = this.status;

        if (name.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || instructions.isEmpty() || status == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSuccess = false;
        if (img != null) {
            isSuccess = MedicationManager.getInstance().addMedication(MedicationManager.generateMedicationId(), name, dosage, frequency, instructions, img, status);
        } else {
            //MedicationManager.getInstance().addMedication(MedicationManager.generateMedicationId(), name, dosage, frequency, instructions);
            isSuccess = MedicationManager.getInstance().addMedication(MedicationManager.generateMedicationId(), name, dosage, frequency, instructions, status);
        }

        if (isSuccess) {
            Toast.makeText(this, "Medication saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save medication", Toast.LENGTH_SHORT).show();
        }
    }
}