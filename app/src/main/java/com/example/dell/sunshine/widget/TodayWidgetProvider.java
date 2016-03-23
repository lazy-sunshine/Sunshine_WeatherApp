package com.example.dell.sunshine.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.dell.sunshine.sync.SunshineSyncAdapter;

/**
 * Created by DeLL on 3/7/2016.
 */
public class TodayWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent widget=new Intent(context, WidgetIntentService.class);

        context.startService(widget);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(SunshineSyncAdapter.ACTION_DATA_UPDATED .equals(intent.getAction())){
            context.startService(new Intent(context,WidgetIntentService.class));
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

            context.startService(new Intent(context,WidgetIntentService.class));


    }
}
