package com.example.teleprompter;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.appcompat.content.res.AppCompatResources;

public class Widget extends AppWidgetProvider {
    static void update(Context context, AppWidgetManager manager, int id, Document document, boolean isLoggedIn) {
        RemoteViews views = getRemoteViews(context, document, isLoggedIn);
        manager.updateAppWidget(id, views);
    }

    public static void updateAll(Context context, AppWidgetManager manager, int[] ids, Document document, boolean isLoggedIn) {
        for (int id : ids) update(context, manager, id, document, isLoggedIn);
    }

    private static void setPreLollipopImageResource(RemoteViews remoteViews, Context context, int id, int resource) {
        Drawable drawable = AppCompatResources.getDrawable(context, resource);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        remoteViews.setImageViewBitmap(id, bitmap);
    }

    private static void setIcons(RemoteViews remoteViews, Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreLollipopImageResource(remoteViews, context, R.id.widget_new_document, R.drawable.ic_add);
            setPreLollipopImageResource(remoteViews, context, R.id.widget_edit_pinned_document, R.drawable.ic_edit);
            setPreLollipopImageResource(remoteViews, context, R.id.widget_play_pinned_document, R.drawable.ic_play);
        } else {
            remoteViews.setImageViewResource(R.id.widget_new_document, R.drawable.ic_add);
            remoteViews.setImageViewResource(R.id.widget_edit_pinned_document, R.drawable.ic_edit);
            remoteViews.setImageViewResource(R.id.widget_play_pinned_document, R.drawable.ic_play);
        }
    }

    private static PendingIntent getNewDocumentPendingIntent(Context context) {
        Document document = new Document();
        document.setTitle("");
        document.setText("");
        document.setNew(true);
        document.setUserId(SharedPreferenceUtils.getUserId(context));
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.PARCEL_KEY, document);
        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntentWithParentStack(intent);
        return builder.getPendingIntent(210, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getMainActivityPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.NO_PINNED, true);
        return PendingIntent.getActivity(context, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getLoginPendingIntent(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        return PendingIntent.getActivity(context, 201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getPinnedDocumentPendingIntent(Context context, Document document) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.PARCEL_KEY, document);
        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntentWithParentStack(intent);
        return builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getPinnedDocumentSlideshowPendingIntent(Context context, Document document) {
        Intent intent = new Intent(context, MainActivity.class);
        Intent intent1 = new Intent(context, EditActivity.class);
        intent1.putExtra(EditActivity.PARCEL_KEY, document);
        Spec spec = new Spec();
        spec.setScrollSpeed(SharedPreferenceUtils.getDefaultScrollSpeed(context));
        spec.setFontSize(SharedPreferenceUtils.getDefaultFontSize(context));
        spec.setTitle(document.getTitle());
        spec.setContent(document.getText());
        spec.setBackgroundColor(SharedPreferenceUtils.getDefaultBackgroundColor(context));
        spec.setFontColor(SharedPreferenceUtils.getDefaultTextColor(context));
        Intent intent2 = new Intent(context, PlayDocumentActivity.class);
        intent2.putExtra(PlayDocumentActivity.PARCELABLE_KEY, spec);
        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntent(intent);
        builder.addNextIntent(intent1);
        builder.addNextIntent(intent2);
        return builder.getPendingIntent(3, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static RemoteViews getRemoteViews(Context context, Document document, boolean isLoggedIn) {
        PendingIntent newDocumentPendingIntent;
        PendingIntent editDocumentPendingIntent;
        PendingIntent slideshowPendingIntent;

        if (!isLoggedIn) {
            newDocumentPendingIntent = getLoginPendingIntent(context);
            editDocumentPendingIntent = getLoginPendingIntent(context);
            slideshowPendingIntent = getLoginPendingIntent(context);
        } else {
            newDocumentPendingIntent = getNewDocumentPendingIntent(context);
            if (document != null) {
                editDocumentPendingIntent = getPinnedDocumentPendingIntent(context, document);
                slideshowPendingIntent = getPinnedDocumentSlideshowPendingIntent(context, document);
            } else {
                editDocumentPendingIntent = getMainActivityPendingIntent(context);
                slideshowPendingIntent = getMainActivityPendingIntent(context);
            }
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        setIcons(views, context);
        views.setOnClickPendingIntent(R.id.widget_new_document, newDocumentPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_edit_pinned_document, editDocumentPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_play_pinned_document, slideshowPendingIntent);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetService.updateWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}