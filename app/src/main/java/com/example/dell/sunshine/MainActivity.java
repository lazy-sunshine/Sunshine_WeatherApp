package com.example.dell.sunshine;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dell.sunshine.gcm.RegistrationIntentService;
import com.example.dell.sunshine.sync.SunshineSyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String LOG_TAG =MainActivity.class.getSimpleName() ;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    String m_location;
    private boolean mTwoPane;
    RecyclerView mrecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launch);


        m_location = Utility.getPreferredLocation(this);


        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DisplayMessageActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {


            mTwoPane = false;

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!mTwoPane);


        SunshineSyncAdapter.initializeSyncAdapter(this);

        if (checkPlayServices()) {
            // Because this is the initial creation of the app, we'll want to be certain we have
            // a token. If we do not, then we will start the IntentService that will register this
            // application with GCM.
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            Log.v("MainActivity", " Token...");
            if (!sentToken) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                Log.v("MainActivity", "No Token...");
                startService(intent);
            }
        }

        View v = ff.getView();
        if (v != null){
             mrecycler = (RecyclerView) v.findViewById(R.id.listview_forecast);
    }
        final AppBarLayout appbarView = (AppBarLayout)findViewById(R.id.appbar);

        ViewCompat.setElevation(appbarView, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (0 == mrecycler.computeVerticalScrollOffset()) {
                        appbarView.setElevation(0);
                    } else {
                        appbarView.setElevation(appbarView.getTargetElevation());
                    }
                }
            });
        }


    }



    @Override
    public void onResume() {
        super.onResume();
        String loc=Utility.getPreferredLocation(this);
        if(loc !=null && !(loc.equals(m_location))){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(null !=ff) {
                ff.onLocationChanged();
            }
            m_location=loc;
            DisplayMessageActivityFragment df = (DisplayMessageActivityFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(loc);
            }
            m_location = loc;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent setting=new Intent(this,SettingActivity.class);
            startActivity(setting);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
    private boolean checkPlayServices() {
               GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
                if (resultCode != ConnectionResult.SUCCESS) {
                    if (apiAvailability.isUserResolvableError(resultCode)) {
                               apiAvailability.getErrorDialog(this, resultCode,
                                                PLAY_SERVICES_RESOLUTION_REQUEST).show();
                            } else {
                                Log.i(LOG_TAG, "This device is not supported.");
                                finish();
                            }
                     return false;
                    }
             return true;
            }

    @Override
    public void onItemSelected(Uri dateUri) {
      if(mTwoPane){
          Bundle args= new Bundle();
          args.putParcelable(DisplayMessageActivityFragment.DETAIL_URI,dateUri);


          DisplayMessageActivityFragment fragment = new DisplayMessageActivityFragment();
          fragment.setArguments(args);

          getSupportFragmentManager().beginTransaction()
                  .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                  .commit();
      } else {

          Log.v("MAAIN ACTIVITY",   dateUri.toString());
          Intent intent = new Intent(this, DisplayMessage.class)
                  .setData(dateUri);

          ActivityOptionsCompat activityoptions =ActivityOptionsCompat.makeSceneTransitionAnimation(this);
          ActivityCompat.startActivity(this, intent, activityoptions.toBundle());
      }
      }
    }
