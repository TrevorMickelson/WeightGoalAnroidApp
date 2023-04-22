package com.example.trevorsandroidapplication.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trevorsandroidapplication.WeightAppMain;

import java.util.Optional;

public class AppUtil {
    public static void runSync(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * This seems to almost be a perfect solution, unfortunately
     * in its current state there's a double messaging going on here
     *
     * Probably I'm missing something
     *
     * I did notice that this implementation automatically asks
     * the user if they want to agree or not, so I didn't end up
     * having to do any custom coding for that feature
     */
    public static void attemptToSendSMSMessage(WeightAppMain main, String message) {
        getCurrentPhoneNumber(main).ifPresent(number -> {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
        });
    }

    @SuppressLint("HardwareIds")
    public static Optional<String> getCurrentPhoneNumber(WeightAppMain main) {
        if (ContextCompat.checkSelfPermission(main, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS},
                    PackageManager.PERMISSION_GRANTED);
        }

        return Optional.ofNullable(((TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number());
    }
}
