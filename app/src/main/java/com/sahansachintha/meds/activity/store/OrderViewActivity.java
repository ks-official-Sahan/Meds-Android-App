package com.sahansachintha.meds.activity.store;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.adapters.OrderItemAdapter;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.model.Order;
import com.sahansachintha.meds.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderViewActivity extends AppCompatActivity {

    public static final String TAG = "MyMedsOrderView";
    private Order order;
    private RecyclerView orderRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.order_view_back_btn).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        findViewById(R.id.order_view_continue_btn).setOnClickListener(v -> NavigationHelper.getInstance().openIntent(this, StoreActivity.class));

        setOrder();

        if (order != null) {
            initOrderRecycler();
        }

    }

    @SuppressLint("SetTextI18n")
    private void setOrder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            order = getIntent().getSerializableExtra(NavigationHelper.ORDER_EXTRA, Order.class);
        } else {
            order = (Order) getIntent().getSerializableExtra(NavigationHelper.ORDER_EXTRA);
        }

        if (order != null) {
            // Toast.makeText(this, order.getOrderId(), Toast.LENGTH_SHORT).show();
            TextView orderID = findViewById(R.id.order_view_order_id);
            orderID.setText((order.getId() != null) ? order.getId() : order.getOrderId());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(order.getTimestamp());

            TextView orderDate = findViewById(R.id.order_view_order_date);
            orderDate.setText(dateFormat.format(calendar.getTime()));

            TextView orderStatus = findViewById(R.id.order_view_order_status);
            orderStatus.setText(order.getStatus());

            TextView orderTotal = findViewById(R.id.order_view_total);
            orderTotal.setText(String.format(Locale.US, "LKR %.2f", order.getTotalPrice()));
            TextView orderDelivery = findViewById(R.id.order_view_delivery);
            orderDelivery.setText(String.format(Locale.US, "LKR %.2f", order.getDelivery()));
            TextView orderSubTotal = findViewById(R.id.order_view_subtotal);
            orderSubTotal.setText(String.format(Locale.US, "LKR %.2f", (order.getTotalPrice() - order.getDelivery())));

            TextView customerName = findViewById(R.id.order_view_customer_name);
            TextView customerMobile = findViewById(R.id.order_view_customer_mobile);
            TextView customerAddress = findViewById(R.id.order_view_address);

            //User user = order.getCustomer();
            User user = AppHelper.getInstance().getUserModel();
            customerName.setText(user.getName());
            customerMobile.setText(user.getMobile());
            customerAddress.setText(user.getAddress() + ", " + user.getCity() + ", " + user.getCountry());
        }
    }

    private void initOrderRecycler() {
        orderRecycler = findViewById(R.id.order_view_recycler);

        orderRecycler
                .setLayoutManager(new LinearLayoutManager(OrderViewActivity.this, LinearLayoutManager.VERTICAL, false));

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(order.getOrderItems(),
                OrderViewActivity.this, item -> {
            Log.i(TAG, item.getProduct().getTitle());
        });
        orderRecycler.setAdapter(orderItemAdapter);

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        String subTotal = String.format(Locale.US, "LKR %.2f", (order.getTotalPrice() - order.getDelivery()));
        TextView subTotalText = findViewById(R.id.order_view_subtotal);
        subTotalText.setText(subTotal);

        String delivery = String.format(Locale.US, "LKR %.2f", order.getDelivery());
        TextView deliveryText = findViewById(R.id.order_view_delivery);
        deliveryText.setText(delivery);

        String total = String.format(Locale.US, "LKR %.2f", order.getTotalPrice());
        TextView totalText = findViewById(R.id.order_view_total);
        totalText.setText(total);
    }

}