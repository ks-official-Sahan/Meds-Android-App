package com.sahansachintha.meds.fragment.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.store.CheckoutActivity;
import com.sahansachintha.meds.adapters.CartAdapter;
import com.sahansachintha.meds.helper.NavigationHelper;
import com.sahansachintha.meds.helper.data.CartManager;
import com.sahansachintha.meds.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {

    private List<CartItem> cartItems;
    private RecyclerView cartRecycler;
    private CartAdapter cartAdapter;
    private TextView totalPrice;
    private View emptyCartView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecycler = view.findViewById(R.id.cart_recycler);
        totalPrice = view.findViewById(R.id.cart_total_price);
        emptyCartView = view.findViewById(R.id.cart_empty_view);

        updateCartUI();

        view.findViewById(R.id.cart_continue_btn).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        view.findViewById(R.id.cart_checkout_btn).setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                proceedToCheckout();
            }
        });

        return view;
    }

    public void updateCartUI() {
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            cartRecycler.setVisibility(View.GONE);
            emptyCartView.setVisibility(View.VISIBLE);
        } else {
            cartRecycler.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
        }

        cartRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, getContext(), this);
        cartRecycler.setAdapter(cartAdapter);

        updateTotalPrice();
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        String totalValue = String.format(Locale.US, "LKR %.2f", CartManager.getTotalPrice());
        totalPrice.setText(totalValue);
    }

    private void proceedToCheckout() {
        Toast.makeText(getContext(), "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
        // Implement checkout navigation
        try {
            NavigationHelper.getInstance().openIntent(requireActivity(), CheckoutActivity.class);
        } catch (IllegalStateException e) {
            Log.e("CartFragment", "Error opening CheckoutActivity: " + e.getMessage());
        }
    }
}