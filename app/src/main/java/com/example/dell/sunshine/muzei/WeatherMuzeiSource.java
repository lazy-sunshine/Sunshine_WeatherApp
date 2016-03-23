package com.example.dell.sunshine.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.dell.sunshine.MainActivity;
import com.example.dell.sunshine.Utility;
import com.example.dell.sunshine.data.WeatherContract;
import com.example.dell.sunshine.sync.SunshineSyncAdapter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

/**
 * Created by DeLL on 3/12/2016.
 */
public class WeatherMuzeiSource extends MuzeiArtSource {


    public static String[] PROJECTION=new String[]{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    static final int INDEX_WEATHER_ID = 0;

    static final int INDEX_WEATHER_DESC = 1;

    public WeatherMuzeiSource() {
        super("WeatherMuzeiSource");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        boolean dataUpdated= intent !=null && SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());
        Log.v("Is it", String.valueOf(dataUpdated));
        Log.v("Is it isEnable", String.valueOf(isEnabled()));
               if (dataUpdated ) {

                      onUpdate(UPDATE_REASON_OTHER);
                }
    }

    @Override
    protected void onUpdate(int reason) {
        String location = Utility.getPreferredLocation(WeatherMuzeiSource.this);
        Uri weatherLocation = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, System.currentTimeMillis());
        Cursor cursor = getContentResolver().query(weatherLocation, PROJECTION, null, null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        if (cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            Log.v("Weather_id", String.valueOf(weatherId));
            String desc = cursor.getString(INDEX_WEATHER_DESC);
            String imageUrl = Utility.getImageUrlForWeatherCondition(weatherId);
            if (imageUrl != null) {
                publishArtwork(new Artwork.Builder()
                        .imageUri(Uri.parse(imageUrl))
                        .title(desc)
                        .byline(location)
                        .viewIntent(new Intent(this, MainActivity.class))
                        .build());
                Log.v("ImageUrl", imageUrl);
            }
            cursor.close();
        }
    }

    }

