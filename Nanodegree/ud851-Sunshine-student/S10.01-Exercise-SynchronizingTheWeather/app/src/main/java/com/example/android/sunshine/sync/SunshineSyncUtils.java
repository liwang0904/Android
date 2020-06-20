package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

// TODO (9) Create a class called SunshineSyncUtils
public class SunshineSyncUtils {
    //  TODO (10) Create a public static void method called startImmediateSync
    public static void startImmediateSync(@NonNull final Context context) {
//      TODO (11) Within that method, start the SunshineSyncIntentService
        Intent intentToSyncImmediately = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}