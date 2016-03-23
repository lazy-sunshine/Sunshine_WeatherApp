package com.example.dell.sunshine.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.sunshine.MainActivity;
import com.example.dell.sunshine.R;
import com.example.dell.sunshine.Utility;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DeLL on 2/15/2016.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String EXTRA_DATA = "Bundle";
    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";
    private static final int NOTIFICATION_ID = 1;
    private String Log_Tag=MyGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if(! data.isEmpty()){
            String senderId=getString(R.string.gcm_defaultSenderId);
            if (senderId.length() == 0) {
                                Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show();
                            }
            if(senderId.equals(from)){try {

                    Log.v("DATAAA", data.toString());
                   JSONArray obj=new JSONArray(data.getString(EXTRA_DATA));
                    JSONObject json=obj.getJSONObject(0);
                    String weather=json.getString(EXTRA_WEATHER);
                    String location=json.getString(EXTRA_LOCATION);
                    String alert = String.format(getString(R.string.gcm_weather_alert), weather, location);
                         sendNotification(alert);
                } catch (JSONException e) {
                   e.printStackTrace();
                }
                Log.v(Log_Tag, data.toString());
            }
        }
    }

    private void sendNotification(String alert) {

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.art_storm);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.art_clear)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("ALERT!!!")
                        .setContentText(alert)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(alert))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
