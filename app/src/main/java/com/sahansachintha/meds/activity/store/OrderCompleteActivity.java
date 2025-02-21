package com.sahansachintha.meds.activity.store;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.model.Order;

import java.util.Locale;

public class OrderCompleteActivity extends AppCompatActivity {

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_complete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setOrder();

        findViewById(R.id.order_confirm_continue_btn).setOnClickListener(v -> NavigationHelper.getInstance().openIntent(this, StoreActivity.class));
        findViewById(R.id.order_confirm_view_btn).setOnClickListener(v -> NavigationHelper.getInstance().viewOrders(this));
    }

    private void setOrder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            order = getIntent().getSerializableExtra("order", Order.class);
        } else {
            order = (Order) getIntent().getSerializableExtra("order");
        }

        if (order != null) {
            //Toast.makeText(this, order.getOrderId(), Toast.LENGTH_SHORT).show();
            TextView orderID = findViewById(R.id.order_confirm_order_id);
            orderID.setText(order.getOrderId());
            TextView orderTotal = findViewById(R.id.order_confirm_order_total);
            orderTotal.setText(String.format(Locale.US, "LKR %.2f", order.getTotalPrice()));
        }
    }
}