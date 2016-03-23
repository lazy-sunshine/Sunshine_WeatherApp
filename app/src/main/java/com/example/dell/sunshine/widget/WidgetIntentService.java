package com.example.dell.sunshine.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.dell.sunshine.MainActivity;
import com.example.dell.sunshine.MyApplication;
import com.example.dell.sunshine.R;
import com.example.dell.sunshine.Utility;
import com.example.dell.sunshine.data.WeatherContract;

/**
 * Created by DeLL on 3/10/2016.
 */
public class WidgetIntentService extends IntentService {
    public WidgetIntentService() {
        super("WidgetIntentService");
    }
Context context= MyApplication.getAppContext();
    public static String[] FORECAST_COLUMN={
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    public final static int COL_WEATHER_ID=0;
    public final static  int COL_SHORT_DESC=1;
    public final static int COL_MAX_TEMP=2;
    public final static int COL_MIN_TEMP=3;
    Cursor mCursor;
    RemoteViews remote;
    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);

        int[] appWidgetIds=appWidgetManager.getAppWidgetIds(new ComponentName(this, TodayWidgetProvider.class));

        String location= Utility.getPreferredLocation(this);
        Uri weatherLocationUri= WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis());
        mCursor=getContentResolver().query(weatherLocationUri,FORECAST_COLUMN,null,null, WeatherContract.WeatherEntry.COLUMN_DATE +" ASC");
        if(mCursor ==null) {
            Log.v("NULLL","its null");
            return;
        }

        if (!mCursor.moveToFirst()) {
            mCursor.close();
            Log.v("NULLL1", "its null");
            return;
        }
        int weather_Id = mCursor.getInt(COL_WEATHER_ID);
        Log.v("IconView1", String.valueOf(weather_Id));
        int iconView = Utility.getArtResourceForWeatherCondition(weather_Id);
        Log.v("IconView", String.valueOf(iconView));
        String desc=mCursor.getString(COL_SHORT_DESC);
        double high = mCursor.getDouble(COL_MAX_TEMP);
        double low=mCursor.getDouble(COL_MIN_TEMP);
        String lowString=Utility.formatTemperature(context,low);
        String highString = Utility.formatTemperature(context, high);
        Log.v("HighString", highString);
        
        for(int appWidgetId:appWidgetIds) {
           int width= getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_today_large_width);
            if (width >=largeWidth ) {
                remote = new RemoteViews(context.getPackageName(), R.layout.widget_today_large);
                remote.setTextViewText(R.id.min_temp, lowString);
                remote.setTextViewText(R.id.desp, desc);
            }
            else if (width>=defaultWidth) {
                remote = new RemoteViews(context.getPackageName(), R.layout.widget_today);
                remote.setTextViewText(R.id.min_temp, lowString);
            }
            else  {
                remote = new RemoteViews(context.getPackageName(), R.layout.widget_today_small);
        }

            Intent i = new Intent(context, MainActivity.class);
            remote.setImageViewResource(R.id.image_widget_icon, iconView);
            remote.setTextViewText(R.id.max_temp, highString);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(remote, desc);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
            remote.setOnClickPendingIntent(R.id.widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remote);
        }


        }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);

return getWidgetWidthOption(appWidgetManager,appWidgetId);
           }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthOption(AppWidgetManager appWidgetManager, int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_today_default_width);
    }




    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews remote, String desc) {
        remote.setContentDescription(R.id.image_widget_icon,desc);
    }
}
