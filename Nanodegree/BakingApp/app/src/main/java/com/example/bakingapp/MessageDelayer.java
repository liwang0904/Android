package com.example.bakingapp;

import android.os.Handler;

public class MessageDelayer {
    interface DelayerCallback {
        void onDone();
    }

    static void processMessage(final SimpleIdlingResource idlingResource) {
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        }, 5000);
    }
}
