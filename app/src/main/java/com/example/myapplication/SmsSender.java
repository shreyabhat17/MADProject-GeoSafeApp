package com.example.myapplication;
import android.telephony.SmsManager;

public class SmsSender {
    public void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            System.out.println("SMS sent successfully.");
        } catch (Exception ex) {
            System.err.println("Failed to send SMS: " + ex.getMessage());
        }
    }
}
