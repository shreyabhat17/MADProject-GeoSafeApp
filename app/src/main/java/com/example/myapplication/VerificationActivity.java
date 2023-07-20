package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class VerificationActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    Button btnGenerateOTP, btnVerify;
    EditText etMobile, etOTP;
    int generatedOTP;
    SmsSender smsSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        btnGenerateOTP = findViewById(R.id.BTotp);
        btnVerify = findViewById(R.id.BTverify);
        etMobile = findViewById(R.id.mobile);
        etOTP = findViewById(R.id.code);

        smsSender = new SmsSender();

        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateOTP();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });
    }

    private void generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        generatedOTP = 100000 + random.nextInt(900000);


        String phoneNumber = etMobile.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }else if (!phoneNumber.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid phone number. Please enter a 10-digit number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(VerificationActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(VerificationActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, send the SMS
            smsSender.sendSms(phoneNumber, String.valueOf(generatedOTP)); // conert to string cuz smssemder is intilalized with string in method
            Toast.makeText(VerificationActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
        }
    }


    private void verifyOTP() {
        String userEnteredOTP = etOTP.getText().toString().trim();

        if (userEnteredOTP.isEmpty()) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Compare the user-entered OTP with the generated OTP
        if (userEnteredOTP.equals(String.valueOf(generatedOTP))) {
            // OTP matched, navigate to SignupActivity
            Toast.makeText(this, "OTP verification successful", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(VerificationActivity.this,SignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // OTP mismatched
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
