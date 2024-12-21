package com.unknowndev.syncbridge.AuthorizationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.unknowndev.syncbridge.Model.UserModel;
import com.unknowndev.syncbridge.MyServices.MyFirebase;
import com.unknowndev.syncbridge.R;
public class RegisterActivity extends AppCompatActivity {
    private EditText edFirstName, edLastName, edPhoneNumber, edEmail, edPassword;
    private CountryCodePicker countryCodePicker;
    private TextView loginRedirectTextView;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        edFirstName = findViewById(R.id.firstname);
        edLastName = findViewById(R.id.last_name);
        edPhoneNumber = findViewById(R.id.phone);
        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        registerButton = findViewById(R.id.registerButton);
        loginRedirectTextView = findViewById(R.id.loginRedirect);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        isProcessing(false);
        registerButton.setOnClickListener( v -> {
            isProcessing(true);
            String firstName = edFirstName.getText().toString().trim();
            String lastName = edLastName.getText().toString().trim();
            String phone = edPhoneNumber.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();

            if (isValidFields()){
                isProcessing(true);
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    UserModel userModel = new UserModel(firstName,lastName,phone,email,password, mAuth.getUid());
                                    MyFirebase.getMyDetails().setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "User Created Successful ... ", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Something getting wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Something getting wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                isProcessing(false);
            } else {
                isProcessing(false);
            }

        });

    }
    public boolean isValidFields() {
        String firstName = edFirstName.getText().toString().trim();
        String lastName = edLastName.getText().toString().trim();
        String phoneNumber = edPhoneNumber.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (firstName.isEmpty()) {
            edFirstName.setError("First name is required");
            return false;
        }

        if (lastName.isEmpty()) {
            edLastName.setError("Last name is required");
            return false;
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() != 10) {
            edPhoneNumber.setError("Enter a valid phone number (at least 10 digits)");
            return false;
        }

        // Validate Email (using Android's built-in Patterns utility)
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Enter a valid email address");
            return false;
        }

        // Validate Password (e.g., at least 6 characters)
        if (password.isEmpty() || password.length() < 6) {
            edPassword.setError("Password must be at least 6 characters long");
            return false;
        }
        // If all fields are valid, return true
        return true;
    }
    void isProcessing(Boolean inProcess){
        if (inProcess){
            registerButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            registerButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}