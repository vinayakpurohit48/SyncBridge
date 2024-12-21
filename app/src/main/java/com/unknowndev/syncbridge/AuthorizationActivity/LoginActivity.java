package com.unknowndev.syncbridge.AuthorizationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.unknowndev.syncbridge.MainActivity;
import com.unknowndev.syncbridge.R;

public class LoginActivity extends AppCompatActivity {

    private TextView loginText, registerRedirectTextView, forgotPasswordTextView;
    private EditText edEmail;
    private com.google.android.material.textfield.TextInputEditText edPassword;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Use the correct layout file

        // Find views by ID
        loginText = findViewById(R.id.loginText);
        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerRedirectTextView = findViewById(R.id.registerRedirect);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordText);
        progressBar = findViewById(R.id.progressBar);

        isProcessing(false);

        loginButton.setOnClickListener(v -> {
            isProcessing(true);
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                // Handle empty fields
                if (email.isEmpty()) {
                    edEmail.setError("Email is required");
                }
                if (password.isEmpty()) {
                    edPassword.setError("Password is required");
                }
                isProcessing(false);
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            isProcessing(false);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Something getting wrong", Toast.LENGTH_SHORT).show();
                            isProcessing(false);
                        }
                    }
                });
            }
        });

        registerRedirectTextView.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            // Handle forgot password logic (e.g., open a reset password screen)
        });
    }
    void isProcessing(Boolean inProcess){
        if (inProcess){
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
