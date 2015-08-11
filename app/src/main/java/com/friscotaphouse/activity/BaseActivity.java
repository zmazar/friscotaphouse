package com.friscotaphouse.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.friscotaphouse.FriscoCallbacks;
import com.friscotaphouse.FriscoUtil;
import com.friscotaphouse.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class BaseActivity extends AppCompatActivity
        implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        DialogInterface.OnCancelListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FriscoCallbacks
{
    private static final String TAG = "BaseActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    protected FrameLayout vMainContent;

    // For accessing shared preferences
    private SharedPreferences mPrefs;

    // Stores Google client for accessing GooglePlayServices
    private GoogleApiClient mLocClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Initialize main content view
        vMainContent = (FrameLayout) findViewById(R.id.main_content);

        // Initialize the navigation drawer fragment
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Now setup the initial configurations
        setupInitialConfigs();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect to Google Play Services
        mLocClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect from Google Play Services.  This is important so that the
        // location and GPS aren't continued on stoppage of the app.  This
        // would ensure the app won't drain the battery.
        mLocClient.disconnect();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // This is just an empty function.  The Frisco activity contains the real implementation
        // that will be used to modify and navigate content.
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.base, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case FriscoUtil.REQUEST_CONNECTION_RESULT:

                switch(resultCode) {
                    case Activity.RESULT_OK:
                        mLocClient.connect();
                } // End switch(resultCode)
        } // End switch(requestCode)
    }

    @Override
    public void onVolumeChange() {
        // This is just an empty function.  The Frisco activity contains the real implementation
        // that will be used to modify the font size
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = true;
        int fontSize = 7;
        int newFontSize = 0;

        try {
            switch(keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if(mPrefs != null) {
                        fontSize = mPrefs.getInt(FriscoUtil.PREFS_FONT_SIZE, 7);
                        newFontSize = fontSize - 1;

                        // Make sure that the font size keeps within a reasonable
                        // font size.
                        if(newFontSize >= FriscoUtil.FONT_SIZE_MIN &&
                           newFontSize <= FriscoUtil.FONT_SIZE_MAX)
                        {
                            mPrefs.edit().putInt(FriscoUtil.PREFS_FONT_SIZE, newFontSize).apply();

                            // Trigger the callback function to refresh the currently
                            // displayed beer lists
                            onVolumeChange();

                            // Reset newFontSize just to ensure no other corruption
                            // of the font size setting occurs.
                            newFontSize = 0;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if(mPrefs != null) {
                        fontSize = mPrefs.getInt(FriscoUtil.PREFS_FONT_SIZE, 7);
                        newFontSize = fontSize + 1;

                        // Make sure that the font size keeps within a reasonable
                        // font size.
                        if(newFontSize >= FriscoUtil.FONT_SIZE_MIN &&
                           newFontSize <= FriscoUtil.FONT_SIZE_MAX)
                        {
                            mPrefs.edit().putInt(FriscoUtil.PREFS_FONT_SIZE, newFontSize).commit();

                            // Trigger the callback function to refresh the currently
                            // displayed beer lists
                            onVolumeChange();

                            // Reset newFontSize just to ensure no other corruption
                            // of the font size setting occurs.
                            newFontSize = 0;
                        }
                    }
                    break;
                default:
                    ret = super.onKeyDown(keyCode, event);
            }
        }
        catch(ClassCastException e) {
            Log.d(TAG, e.toString());
        }

        return ret;
    }


    /**
     * setupInitialConfigs
     *
     * Sets up all the default preferences for the app and saves them to local storage.
     */
    private void setupInitialConfigs() {
        int gpStatus = -1;

        // Set the initial value of the font size to be a default 7.
        mPrefs = getSharedPreferences(FriscoUtil.PREFS_APP, Context.MODE_PRIVATE);

        if(mPrefs != null && !mPrefs.contains(FriscoUtil.PREFS_FONT_SIZE)) {
            mPrefs.edit().putInt(FriscoUtil.PREFS_FONT_SIZE, 7).apply();
        }

        // Ensure that Google Play Services is installed
        gpStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(gpStatus == ConnectionResult.SUCCESS) {
            // Grab the current location if Google Play Services was
            // successfully found on the device.
            mLocClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        else {
            // Show the error dialog returned.  This activity contains the
            // onCancel callback.
            Dialog d = GooglePlayServicesUtil.getErrorDialog(gpStatus, this, 0, this);
            d.show();
        }
    }

    /********************************************************************************************
     *
     * Google Play Services classes implemented.  These are for all location related
     * activities that we use Google Play Services to access.
     *
     ********************************************************************************************/

    /**
     * The onCancel listener in case the Google Play Services error dialog is
     * cancelled.  It will only set the initial beer list to be displayed.
     *
     * Since the location via Google Play Services has failed, we'll just use
     * the default location stored in the preferences.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        dialog.dismiss();
    }

    /**
     * Implementation of both onConnectionFailed and ConnectionCallback
     * interfaces.
     *
     * These are used specifically for the LocationClient to ensure that a
     * current location for the device can be found.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects.
        // If the error has a resolution, try sending an Intent to
        // start a Google Play services activity that can resolve
        // error.
        if(connectionResult.hasResolution()) {
            try {
                // Thrown if Google Play services canceled the original
                // PendingIntent

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        FriscoUtil.REQUEST_CONNECTION_RESULT
                );
            }
            catch(IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
        else {
            // TODO: If no resolution is available, display a dialog to the
            // user with the error.
            FriscoUtil.toast(this, "SUPER FAIL Google Play Services");
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        FriscoUtil.toast(this, "Connected to Google Play Services");

        // Alright, lets update the location each time we connect to Google
        // Play services, and center the map view with it.
        if(mLocClient.isConnected()) {
            double distToColumbia = 0.0;
            double distToCrofton= 0.0;
            Location currLoc = LocationServices.FusedLocationApi.getLastLocation(mLocClient);
            Location croftonLoc = new Location("");
            Location columbiaLoc = new Location("");

            // Set the latitude and longitude for each FriscoUtil location
            columbiaLoc.setLatitude(FriscoUtil.LAT_COLUMBIA);
            columbiaLoc.setLongitude(FriscoUtil.LNG_COLUMBIA);
            croftonLoc.setLatitude(FriscoUtil.LAT_CROFTON);
            croftonLoc.setLongitude(FriscoUtil.LNG_CROFTON);

            // Calculate if we're within a specific FriscoUtil Location.
            distToColumbia = currLoc.distanceTo(columbiaLoc);
            distToCrofton = currLoc.distanceTo(croftonLoc);

            // Determine which location (if any) the device is at.
            if(distToColumbia < FriscoUtil.FRISCO_RADIUS) {
                FriscoUtil.toast(this, "Location: Columbia\n");
                if(mPrefs != null) {
                    mPrefs.edit().putInt(FriscoUtil.PREFS_DEFAULT_LOCATION, FriscoUtil.LOCATION_COLUMBIA);
                }
            }
            else if(distToCrofton < FriscoUtil.FRISCO_RADIUS) {
                FriscoUtil.toast(this, "Location: Crofton\n");
                if(mPrefs != null) {
                    mPrefs.edit().putInt(FriscoUtil.PREFS_DEFAULT_LOCATION, FriscoUtil.LOCATION_COLUMBIA);
                }
            }
            else {
                if(mPrefs != null) {
                    mPrefs.edit().putInt(FriscoUtil.PREFS_DEFAULT_LOCATION, getDefaultLocation());
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO: Unimplmented
    }

    protected int getDefaultLocation() {
        int defaultLocation = FriscoUtil.LOCATION_COLUMBIA;

        // Setup the initial fragment based on the default location set by
        // the user.
        mPrefs = getSharedPreferences(FriscoUtil.PREFS_APP, Context.MODE_PRIVATE);

        if(mPrefs != null) {
            defaultLocation = mPrefs.getInt(FriscoUtil.PREFS_DEFAULT_LOCATION, -1);

            if(defaultLocation == -1) {
                defaultLocation = FriscoUtil.LOCATION_COLUMBIA;
            }
        }

        return defaultLocation;
    }
}
