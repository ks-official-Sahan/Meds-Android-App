package com.sahansachintha.meds.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseUser;
import com.sahansachintha.meds.R;
import com.sahansachintha.meds.activity.home.HomeActivity;
import com.sahansachintha.meds.helper.AppHelper;
import com.sahansachintha.meds.network.AuthService;

import java.util.concurrent.atomic.AtomicBoolean;

public class AuthActivity extends AppCompatActivity {

    private ConstraintLayout authSplashLayout;
    private TextView textLogin, textSignup, forgotPassword;

    private final AtomicBoolean isSignUp = new AtomicBoolean(true);
    private Button authSubmitButton;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initActivity();

        textLogin = findViewById(R.id.auth_switch_login);
        textSignup = findViewById(R.id.auth_switch_signup);

        authSubmitButton = findViewById(R.id.authSubmitButton);
        forgotPassword = findViewById(R.id.auth_forgot_password);

        textLogin.setOnClickListener((View v) -> manageAuthModeToggle());
        textSignup.setOnClickListener(v -> manageAuthModeToggle());

        authService = new AuthService(this);

        authSubmitButton.setOnClickListener(v -> {
            EditText emailField = findViewById(R.id.auth_email_input);
            EditText passwordField = findViewById(R.id.auth_password_input);
            EditText confirmPasswordField = findViewById(R.id.auth_cpassword_input);

            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String cpassword = confirmPasswordField.getText().toString().trim();

            if (isSignUp.get()) {
                // Sign Up flow
                if (email.isEmpty() || password.isEmpty() || cpassword.isEmpty()) {
                    Toast.makeText(this, "Please enter email and password and confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(cpassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                authService.signUp(email, password, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        //Toast.makeText(AuthActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        openIntent(HomeActivity.class);
                        finish();
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(AuthActivity.this, "Signup failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Login flow
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                authService.login(email, password, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Toast.makeText(AuthActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        authService.getToken(user, new AuthService.TokenCallback() {
                            @Override
                            public void onTokenReceived(String token) {
                                // Token is sent to backend automatically via AuthService.
                                openIntent(HomeActivity.class);
                                finish();
                            }
                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(AuthActivity.this, "Token retrieval failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(AuthActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show();
        });
    }

    /* Switch Logic */
    private void manageAuthModeToggle() {
        if (isSignUp.get()) {
            switchToLogin();
        } else {
            switchToSignUp();
        }
    }
    /* Switch Logic */

    /* Switch Auth Mode */
    private void switchToLogin() {
        isSignUp.set(false);

        textLogin.setBackgroundResource(R.drawable.background_selected);
        textLogin.setTextColor(getThemeColor(this, com.google.android.material.R.attr.colorOnPrimary));
        textLogin.setElevation(5f);

        textSignup.setBackgroundResource(R.drawable.background_unselected);
        textSignup.setTextColor(getThemeColor(this, com.google.android.material.R.attr.colorOnSurface));
        textSignup.setElevation(3f);

        forgotPassword.setVisibility(View.VISIBLE);
        findViewById(R.id.auth_cpassword_layout).setVisibility(View.GONE);

        authSubmitButton.setText(R.string.title_login);
    }

    private void switchToSignUp() {
        isSignUp.set(true);

        textSignup.setBackgroundResource(R.drawable.background_selected);
        textSignup.setTextColor(getThemeColor(this, com.google.android.material.R.attr.colorOnPrimary));
        textSignup.setElevation(5f);

        textLogin.setBackgroundResource(R.drawable.background_unselected);
        textLogin.setTextColor(getThemeColor(this, com.google.android.material.R.attr.colorOnSurface));
        textLogin.setElevation(3f);

        forgotPassword.setVisibility(View.GONE);
        findViewById(R.id.auth_cpassword_layout).setVisibility(View.VISIBLE);

        authSubmitButton.setText(R.string.title_signup);
    }
    /* Switch Auth Mode */

    /* Activity Open */
    private void openIntent(Class<?> activity) {
        Intent intent = new Intent(AuthActivity.this, activity);
        startActivity(intent);
    }
    /* Activity Open */

    private int getThemeColor(Context context, int attribute) {
        return AppHelper.getInstance().getThemeColor(context, attribute);
    }

    private void initActivity() {
        authSplashLayout = findViewById(R.id.auth_splash_cl_1);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        findViewById(R.id.auth_forgot_password).setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            authSplashLayout.setVisibility(View.GONE);
        }, 500);
    }
}