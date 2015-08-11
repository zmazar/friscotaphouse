package com.friscotaphouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.friscotaphouse.activity.BaseActivity;
import com.friscotaphouse.beer.BeerDbHelper;
import com.friscotaphouse.beer.BeerListFragment;
import com.friscotaphouse.ui.DefaultLocationDialog;

public class Frisco extends BaseActivity {
    private static final String TAG = "FriscoUtil Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the Frisco layout into the main_content of BaseActivity
        LayoutInflater.from(this).inflate(R.layout.activity_frisco, vMainContent, true);

        // Get the stored default location, and show that beer list on start.
        int location = getDefaultLocation();

        if(location == FriscoUtil.LOCATION_CROFTON) {
            // Display Crofton's list on start
            onNavigationDrawerItemSelected(1);
        }
        else {
            // Display Columbia's list on start
            onNavigationDrawerItemSelected(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frisco, menu);
        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        super.onNavigationDrawerItemSelected(position);
        FragmentManager fm;
        FragmentTransaction transaction;

        switch (position) {
            case 0:
                fm = getSupportFragmentManager();
                transaction = fm.beginTransaction();
                transaction.replace(R.id.main_content, getColumbiaFragment());
                transaction.commit();

                getSupportActionBar().setSubtitle("Columbia");
                break;
            case 1:
                fm = getSupportFragmentManager();
                transaction = fm.beginTransaction();
                transaction.replace(R.id.main_content, getCroftonFragment());
                transaction.commit();

                getSupportActionBar().setSubtitle("Crofton");
                break;
            case 2:
                // Show the default location selection dialog
                DefaultLocationDialog f = DefaultLocationDialog.newInstance();
                f.show(getSupportFragmentManager(), TAG);

                break;
            default:
                FriscoUtil.toast(this, "Pressed: " + position);
        }
    }

    @Override
    public void onVolumeChange() {
        super.onVolumeChange();

        // The beer list fragments need to be refreshed for the
        // new font sizes to take effect.
        BeerListFragment f = (BeerListFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);

        if(f != null) {
            f.refreshList();
        }
        else {
            Log.d(TAG, "BeerListFragment failed to be found");
        }
    }

    /***************************************************************************************
     *
     * Create the beer fragments for each location
     *
     ***************************************************************************************/
    private Fragment getColumbiaFragment() {
        BeerListFragment f;

        // Grab all beers in Columbia
        f = BeerListFragment.newInstance(
                BeerDbHelper.TABLE_COLUMBIA,
                FriscoUtil.ColumbiaUrl
        );

        return f;
    }

    private Fragment getCroftonFragment() {
        BeerListFragment f;

        // Grab all beers in Crofton
        f = BeerListFragment.newInstance(
                BeerDbHelper.TABLE_CROFTON,
                FriscoUtil.CroftonUrl
        );

        return f;
    }
}
