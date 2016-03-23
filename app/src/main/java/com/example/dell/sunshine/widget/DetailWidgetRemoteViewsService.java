package com.example.dell.sunshine.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dell.sunshine.R;
import com.example.dell.sunshine.Utility;
import com.example.dell.sunshine.data.WeatherContract;

/**
 * Created by DeLL on 3/12/2016.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService{

  public static String []FORECAST_COLUMN={
          WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
          WeatherContract.WeatherEntry.COLUMN_DATE,
          WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
          WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
          WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
          WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
  };

    static final int INDEX_WEATHER_ID = 0;
    static final int INDEX_WEATHER_DATE = 1;
    static final int INDEX_WEATHER_CONDITION_ID = 2;
    static final int INDEX_WEATHER_DESC = 3;
    static final int INDEX_WEATHER_MAX_TEMP = 4;
    static final int INDEX_WEATHER_MIN_TEMP = 5;



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor data=null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                 if(data !=null) {
                     data.close();
                 }

                final long identityToken= Binder.clearCallingIdentity();
                String location= Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherLocationUri= WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis());
                data=getContentResolver().query(weatherLocationUri,FORECAST_COLUMN, null,
                        null,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
                Binder.restoreCallingIdentity(identityToken);


            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews list_item=new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
                int iconView = Utility.getIconResourceForWeatherCondition(weatherId);
                 long date=data.getLong(INDEX_WEATHER_DATE);
                String friendlyDate=Utility.getFriendlyDayString(DetailWidgetRemoteViewsService.this, date);
                String desc=data.getString(INDEX_WEATHER_DESC);
                double max=data.getDouble(INDEX_WEATHER_MAX_TEMP);
                double min=data.getDouble(INDEX_WEATHER_MIN_TEMP);
                String formattedHighTemperature=Utility.formatTemperature(DetailWidgetRemoteViewsService.this, max);
                String formattedLowTemperature=Utility.formatTemperature(DetailWidgetRemoteViewsService.this,min);

                list_item.setImageViewResource(R.id.image_widget_icon,iconView);
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                    list_item.setContentDescription(R.id.image_widget_icon,desc);
                list_item.setTextViewText(R.id.widget_date,friendlyDate);
                list_item.setTextViewText(R.id.desp,desc);
                list_item.setTextViewText(R.id.max_temp,formattedHighTemperature);
                list_item.setTextViewText(R.id.min_temp,formattedLowTemperature);


                final Intent fillInIntent = new Intent();
                String locationSetting =
                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting,
                        date);
                fillInIntent.setData(weatherUri);
                list_item.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return list_item;
            }

            @Override
            public RemoteViews getLoadingView() {

                return new RemoteViews(getPackageName(),R.layout.widget_detail_list_item) ;
            }

            @Override
            public int getViewTypeCount() {
              return  1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_WEATHER_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
