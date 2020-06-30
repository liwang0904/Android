package com.example.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class RecipeWidget extends AppWidgetProvider {
    private static int position = 1;

    static void updateWidget(Context context, AppWidgetManager manager, int id, int position) {
        StringBuffer buffer = new StringBuffer();
        Recipe recipe;

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.putExtra("RECIPE NUMBER", position);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setRemoteAdapter(R.id.widget_list, intent);
        recipe = GsonInstance.getInstance().fromJson(RecipeRepository.getInstance().getRecipe(position, context), Recipe.class);
        buffer.append(recipe.getName());

        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        views.setOnClickPendingIntent(R.id.open_app_btn, pendingIntent);
        views.setTextViewText(R.id.widget_text, buffer);
        manager.updateAppWidget(id, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        selectRecipe(position, context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public static void selectRecipe(int i, Context context) {
        position = i;
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, RecipeWidget.class));
        for (int id: ids)
            updateWidget(context, manager, id, i);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
