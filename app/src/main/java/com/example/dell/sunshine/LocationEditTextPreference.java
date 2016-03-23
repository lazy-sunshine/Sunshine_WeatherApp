package com.example.dell.sunshine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.sql.Connection;
import java.util.jar.Attributes;

/**
 * Created by DeLL on 2/12/2016.
 */
public class LocationEditTextPreference extends EditTextPreference{
int min_length;
    EditText location;
    int PLACE_PICKER_REQUEST=1;
    static final private int DEFAULT_MINIMUM_LOCATION_LENGTH = 3;
    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LocationEditTextPreference,
                0, 0);

        try {
            min_length = a.getInteger(R.styleable.LocationEditTextPreference_min_length, DEFAULT_MINIMUM_LOCATION_LENGTH);

        } finally {
            a.recycle();
        }

        GoogleApiAvailability available=GoogleApiAvailability.getInstance();
        int res=available.isGooglePlayServicesAvailable(getContext());
        if(res== ConnectionResult.SUCCESS){
            setWidgetLayoutResource(R.layout.pref_current_location);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final Context context=getContext();
        final Activity settingActivity= (SettingActivity) context;
        View v=super.onCreateView(parent);
        View current=v.findViewById(R.id.current_location);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
                try {
                    settingActivity.startActivityForResult(builder.build(settingActivity),SettingActivity.PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
return v;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

    location=getEditText();
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Dialog d= getDialog();

                if(editable.length()<min_length){
                  if(d instanceof AlertDialog){
                      ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                  }
                }
                else
                    ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

            }
        });
    }
}
