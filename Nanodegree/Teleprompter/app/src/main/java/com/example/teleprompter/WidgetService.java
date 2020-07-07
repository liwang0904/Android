package com.example.teleprompter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WidgetService extends JobIntentService {
    private static final String HANDLE_UPDATE_WIDGET = "update-widgets";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (HANDLE_UPDATE_WIDGET.equals(intent.getAction())) handleUpdateWidget();
    }

    private void handleUpdateWidget() {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] ids = manager.getAppWidgetIds(new ComponentName(this, Widget.class));

        Document document = null;
        boolean isLoggedIn;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            isLoggedIn = true;
            int id = SharedPreferenceUtils.getPinnedId(this);
            if (id != -1) {
                Uri uri = Contract.Entry.getUriForId(id);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    document = new Document(cursor);
                    cursor.close();
                }
            } else document = null;
        } else {
            document = null;
            isLoggedIn = false;
        }
        Widget.updateAll(this, manager, ids, document, isLoggedIn);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(HANDLE_UPDATE_WIDGET);
        enqueueWork(context, WidgetService.class, 0, intent);
    }
}