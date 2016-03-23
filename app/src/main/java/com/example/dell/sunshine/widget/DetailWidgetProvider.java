package com.example.dell.sunshine.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.dell.sunshine.DisplayMessage;
import com.example.dell.sunshine.MainActivity;
import com.example.dell.sunshine.R;
import com.example.dell.sunshine.sync.SunshineSyncAdapter;

/**
 * Created by DeLL on 3/12/2016.
 */
public class DetailWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.widget_detail);

            Intent intent=new Intent(context, MainActivity.class);
            PendingIntent pending=PendingIntent.getActivity(context,0,intent,0);
            rv.setOnClickPendingIntent(R.id.widget, pending);


                rv.setRemoteAdapter(R.id.detail_widget_listView,new Intent(context, DetailWidgetRemoteViewsService.class));

            boolean use_detail=context.getResources().getBoolean(R.bool.use_detail_activity);
            Intent clickIntent=use_detail?new Intent(context, DisplayMessage.class):
                    new Intent(context,MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.detail_widget_listView, clickPendingIntentTemplate);
            rv.setEmptyView(R.id.detail_widget_listView, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(@NonNull Context context,@NonNull Intent intent) {
        super.onReceive(context, intent);
        if(SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.detail_widget_listView);
        }
    }
}
