package com.example.dell.sunshine;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.dell.sunshine.data.WeatherContract;
import com.example.dell.sunshine.sync.SunshineSyncAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class SettingActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,SharedPreferences.OnSharedPreferenceChangeListener {
ImageView attribution;

    public static final String KEY_FORECAST = "location";

    protected final static int PLACE_PICKER_REQUEST = 9090;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.location)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.units_prefs)));

               if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
                    attribution= new ImageView(this);

                        if (!Utility.containLongAndLat(this)) {
                       attribution.setVisibility(View.GONE);
                   }
                 setListFooter(attribution);
               }
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
       String key=preference.getKey();
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else
            if(key.equals(getString(R.string.location))){
                @SunshineSyncAdapter.LocationMode int loc=  Utility.getStatus(this);
                switch (loc){
                    case SunshineSyncAdapter.LOCATION_STATUS_OK :
                        preference.setSummary(stringValue);
                        break;

                    case SunshineSyncAdapter.LOCATION_SETTING_INVALID:
                           preference.setSummary(R.string.pref_location_error_description);
                        break;

                    case SunshineSyncAdapter.LOCATION_SETTING_UNKNOWN:
                                                preference.setSummary(getString(R.string.pref_location_unknown_description));
                        break;
                    default:
                        preference.setSummary(stringValue);

                }
            }
        else
               preference.setSummary(stringValue);

        return true;
        }





    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
    public Intent getParentActivityIntent() {
                return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("location"))
        {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove(getString(R.string.pref_location_longitude));
            editor.remove(getString(R.string.pref_location_latitude));
            editor.commit();
            if(attribution !=null)attribution.setVisibility(View.GONE);
            Utility.resetLocation(this);
            updateView();
            SunshineSyncAdapter.syncImmediately(this);
        }
        else if ( key.equals(getString(R.string.pref_units_key)) ) {
            // units have changed. update lists of weather entries accordingly
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }

    private void updateView() {

        EditTextPreference edit= (EditTextPreference) findPreference("location");
     @SunshineSyncAdapter.LocationMode int loc=  Utility.getStatus(this);
        switch(loc)
        {
            case SunshineSyncAdapter.LOCATION_SETTING_INVALID :
                onPreferenceChange(edit,R.string.pref_location_error_description);
                break;
            case SunshineSyncAdapter.LOCATION_STATUS_OK:
                onPreferenceChange(edit,edit.getText());
            default:
                onPreferenceChange(edit,R.string.pref_location_unknown_description);
                        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==PLACE_PICKER_REQUEST){
            if(resultCode ==RESULT_OK){

                if(attribution !=null)
                    attribution.setVisibility(View.VISIBLE);
                else {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, getString(R.string.attribution_text),
                            Snackbar.LENGTH_LONG).show();
                }
                Place place = PlacePicker.getPlace(data, this);
                String address=place.toString();
                Log.v("Address is ---", address);
               LatLng latLong= place.getLatLng();
                Log.v("Latitude and LOngitude", String.valueOf(latLong.latitude));
                if(TextUtils.isEmpty(address))
                    address=String.format("(%.2f,%.2f)",latLong.latitude,latLong.longitude);
                SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor=p.edit();
                editor.putString(getString(R.string.location), address);
                editor.putFloat(getString(R.string.pref_location_latitude), (float) latLong.latitude);
                editor.putFloat(getString(R.string.pref_location_longitude), (float) latLong.longitude);

                editor.commit();

            Preference locationPreference=findPreference(getString(R.string.location));
                onPreferenceChange(locationPreference, address);
                Utility.resetLocation(this);
                SunshineSyncAdapter.syncImmediately(this);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
