package com.wrteam.quiz.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getName();
    private static String number = null;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, intent.getAction() + ", EXTRA_STATE: " + state);
            // on ringing get incoming number
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG, "EXTRA_INCOMING_NUMBER: " + number);

            }
        }

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, intent.getAction() + ", EXTRA_PHONE_NUMBER: " + number);
        }

    }
}
