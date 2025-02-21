package com.sahansachintha.meds.activity.store;

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
import com.sahansachintha.meds.adapters.OrderItemAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.CartManager;
import com.sahansachintha.meds.helper.data.OrderManager;
import com.sahansachintha.meds.model.Order;
import com.sahansachintha.meds.model.ProductItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

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

    //public static final String ORDER_ID = UUID.randomUUID().toString();
    public static final String ORDER_ID = OrderManager.generateOrderId();
    public static final double DELIVERY = 200;

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

        findViewById(R.id.checkout_back_btn).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        initOrderRecycler();
        getCustomer();

        findViewById(R.id.checkout_pay_btn).setOnClickListener(v -> initPayment());

        imgPrescription = findViewById(R.id.checkout_prescription);
        imgPrescription.setOnClickListener(v -> selectImageFromGallery());
    }

    private void getCustomer() {
        TextView customerName = findViewById(R.id.checkout_customer_name);
        TextView customerMobile = findViewById(R.id.checkout_customer_mobile);
        TextView address = findViewById(R.id.checkout_address);
        customerName.setText("Sahan Sachintha");
        customerMobile.setText("+94771234567");
        address.setText("No.1, Galle Road, Main Street, Colombo.");
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

        String fileName = "prescription_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(directory, fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream fos = new FileOutputStream(imageFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
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

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(CartManager.getInstance().getCartItems(), CheckoutActivity.this, item -> {
            Log.i(TAG, item.getProduct().getTitle());
        });
        orderRecycler.setAdapter(orderItemAdapter);

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        String subTotal = String.format(Locale.US, "LKR %.2f", CartManager.getInstance().getTotalPrice());
        TextView subTotalText = findViewById(R.id.checkout_subtotal);
        subTotalText.setText(subTotal);

        String delivery = String.format(Locale.US, "LKR %.2f", DELIVERY);
        TextView deliveryText = findViewById(R.id.checkout_delivery);
        deliveryText.setText(delivery);

        double totalValue = CartManager.getInstance().getTotalPrice() + DELIVERY;
        String total = String.format(Locale.US, "LKR %.2f", totalValue);
        TextView totalText = findViewById(R.id.checkout_total);
        totalText.setText(total);
    }


    private void initPayment() {
        try {
            InitRequest req = new InitRequest();
            req.setMerchantId(MERCHANT_ID);
            req.setCurrency("LKR");
            req.setAmount(CartManager.getInstance().getTotalPrice() + DELIVERY);
            req.setOrderId(ORDER_ID);

            req.setItemsDescription("MyMeds Order");
            req.setCustom1("Prescription Payment");

            if (!setCustomerDetails(req)) {
                Toast.makeText(this, "Invalid customer details", Toast.LENGTH_SHORT).show();
                return;
            }

            req.setNotifyUrl("https://evisionit.lk/api/payment/callback");

            req.getItems().clear();
            for (ProductItem item : CartManager.getInstance().getCartItems()) {
                //req.getItems().add(new Item(null, item.getProduct().getTitle(), item.getQuantity(), item.getTotalPrice().doubleValue()));
                req.getItems().add(new Item(null, item.getProduct().getTitle(), item.getQuantity(), item.getTotalPrice()));
            }

            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
            PHConfigs.setBaseUrl(PAYHERE_SANDBOX_URL);

            payHereLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Payment initialization failed", e);
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
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
                if (serializable instanceof PHResponse<?>) {
                    PHResponse<?> genericResponse = (PHResponse<?>) serializable;
                    Object responseData = genericResponse.getData();

                    if (responseData instanceof StatusResponse) {
                        @SuppressWarnings("unchecked")
                        PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) genericResponse;

                        if (response.isSuccess()) {
                            Log.d(TAG, "Payment Success: " + response.getData());
                            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

                            Order order = new Order.Builder()
                                    .setOrderId(ORDER_ID)
                                    .setOrderItems(CartManager.getInstance().getCartItems())
                                    .setStatus("Paid")
                                    .setDelivery(DELIVERY)
                                    .build();
                            OrderManager.getInstance().addOrder(order);
                            CartManager.getInstance().clearCart();

                            NavigationHelper.getInstance().viewOrderComplete(this, order);
                            // Run Order Save API here
                        } else {
                            Log.e(TAG, "Payment Failed: " + response);
                            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

                            NavigationHelper.getInstance().viewCart(this);
                            // Run Order Fail API here
                        }
                    } else {
                        Log.e(TAG, "Unexpected response type: " + responseData);
                    }
                } else {
                    Log.e(TAG, "Unexpected result type: " + serializable);
                }
            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Log.w(TAG, "Payment Cancelled");
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        }
    });
}
