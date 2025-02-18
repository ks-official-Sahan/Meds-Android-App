package com.sahansachintha.meds.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.NavigationHelper;

import java.io.Serializable;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "MyMedsPayhere";
    private static final String MERCHANT_ID = "1221502";
    private static final String PAYHERE_SANDBOX_URL = PHConfigs.SANDBOX_URL;

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

        findViewById(R.id.checkout_pay_btn).setOnClickListener(v -> initPayment());
    }

    private void initPayment() {
        try {
            InitRequest req = new InitRequest();
            req.setMerchantId(MERCHANT_ID);

            req.setCurrency("LKR");
            req.setAmount(3000.00);
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
