package com.sahansachintha.meds.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.OrderAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.CartManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "MyMedsPayhere";
    private static final String MERCHANT_ID = "1221502";
    private static final String PAYHERE_SANDBOX_URL = PHConfigs.SANDBOX_URL;

    private RecyclerView orderRecycler;
    private ImageView imgPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initOrderRecycler();

        findViewById(R.id.checkout_pay_btn).setOnClickListener(v -> initPayment());

        imgPrescription = findViewById(R.id.checkout_prescription);
        imgPrescription.setOnClickListener(v -> selectImageFromGallery());
    }

    /* ImageSelection */
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

    /* Process Selected Image using Glide */
    private void processSelectedImage(Uri imageUri) {
        try {
            File savedFile = saveImageToStorage(imageUri);
            loadImageFromStorage(); // Load from storage for better performance
        } catch (IOException e) {
            Log.e("ImageSelection", "Failed to save image", e);
        }
    }

    private void loadImageFromStorage() {
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds/prescription.jpg");
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imgPrescription);
        }
    }

    private File saveImageToStorage(Uri imageUri) throws IOException {
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds");
        if (!directory.exists()) directory.mkdirs();

        File imageFile = new File(directory, "prescription.jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream fos = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[4096]; // Larger buffer for efficiency
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

    private void uploadImageToServer() {
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyMeds/prescription.jpg");
        if (!imageFile.exists()) {
            Log.e("Upload", "Image file not found!");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                .build();

        Request request = new Request.Builder()
                .url("https://your-nestjs-backend.com/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.e("Upload", "Upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                Log.d("Upload", "Upload success: " + response.body().string());
            }
        });
    }


    private void initOrderRecycler() {
        orderRecycler = findViewById(R.id.checkout_recycler);

        orderRecycler.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this, LinearLayoutManager.VERTICAL, false));

        OrderAdapter orderAdapter = new OrderAdapter(CartManager.getInstance().getCartItems(), CheckoutActivity.this, cartItem -> {
            Log.i(TAG, cartItem.getProduct().getTitle());
        });
        orderRecycler.setAdapter(orderAdapter);

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        String totalValue = String.format(Locale.US, "LKR %.2f", CartManager.getTotalPrice());
        TextView totalText = findViewById(R.id.checkout_total);
        totalText.setText(totalValue);
    }


    private void initPayment() {
        try {
            InitRequest req = new InitRequest();
            req.setMerchantId(MERCHANT_ID);

            req.setCurrency("LKR");
            req.setAmount(CartManager.getTotalPrice());
            req.setOrderId("230000123");

            req.setItemsDescription("MyMeds Medications");
            req.setCustom1("Custom message 1");
            req.setCustom2("Custom message 2");

            // Validate customer details
            if (!setCustomerDetails(req)) {
                Log.e(TAG, "Invalid customer details");
                Toast.makeText(this, "Invalid customer details", Toast.LENGTH_SHORT).show();
                return;
            }

            req.setNotifyUrl("https://evisionit.lk");

            req.getItems().clear();
            req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
            PHConfigs.setBaseUrl(PAYHERE_SANDBOX_URL);

            payHereLauncher.launch(intent);

        } catch (Exception e) {
            Log.e(TAG, "Payment initialization failed", e);
            Toast.makeText(this, "Payment initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean setCustomerDetails(InitRequest req) {
        try {
            req.getCustomer().setFirstName("Saman");
            req.getCustomer().setLastName("Perera");
            req.getCustomer().setEmail("samanp@gmail.com");
            req.getCustomer().setPhone("+94771234567");

            req.getCustomer().getAddress().setAddress("No.1, Galle Road");
            req.getCustomer().getAddress().setCity("Colombo");
            req.getCustomer().getAddress().setCountry("Sri Lanka");

            req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
            req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
            req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error setting customer details: " + e.getMessage(), e);
            return false;
        }
    }

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                if (serializable instanceof PHResponse) {
                    PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                    if (response.isSuccess()) {
                        Log.d(TAG, "Payment Success: " + response.getData());
                        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

                        NavigationHelper.getInstance().openIntent(CheckoutActivity.this, StoreActivity.class);
                        // Run Order Save API here
                    } else {
                        Log.e(TAG, "Payment Failed: " + response);
                        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

                        NavigationHelper.getInstance().openIntent(CheckoutActivity.this, StoreActivity.class);
                        // Run Order Fail API here
                    }
                }
            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Log.w(TAG, "Payment Cancelled");
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        }
    });
}
