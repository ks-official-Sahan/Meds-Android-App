package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.store.StoreActivity;
import com.sahansachintha.meds.adapters.OrderAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.OrderManager;
import com.sahansachintha.meds.model.Order;

import java.util.List;

public class OrdersFragment extends Fragment {

    private View view;
    private RecyclerView ordersRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_orders, container, false);

        view.findViewById(R.id.orders_continue_btn).setOnClickListener(v -> NavigationHelper.getInstance().openIntent(requireContext(), StoreActivity.class));
        initOrderRecycler();

        return view;
    }

    private void initOrderRecycler() {
        ordersRecycler = view.findViewById(R.id.orders_recycler);

        ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ordersRecycler.setHasFixedSize(true);

        List<Order> orderList = OrderManager.getInstance().getAllOrders();
        view.findViewById(R.id.orders_empty_view).setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);

        OrderAdapter orderAdapter = new OrderAdapter(orderList, getContext(), order -> {
            try {
                NavigationHelper.getInstance().viewOrderDetails(requireContext(), order);
            } catch (IllegalStateException e) {
                Log.e("MyMedsOrders", "Error navigating to order details: " + e.getMessage());
            }
        });
        ordersRecycler.setAdapter(orderAdapter);
    }
}