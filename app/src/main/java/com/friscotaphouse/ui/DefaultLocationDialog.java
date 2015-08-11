package com.friscotaphouse.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;

import com.friscotaphouse.FriscoUtil;
import com.friscotaphouse.R;

/**
 * Created by Zach on 8/8/2015.
 */
public class DefaultLocationDialog extends DialogFragment {
    private int mCurrentSelection;
    private SharedPreferences mPrefs;

    public static final DefaultLocationDialog newInstance() {
        DefaultLocationDialog f = new DefaultLocationDialog();

        return f;
    }

    public DefaultLocationDialog() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize variables
        mCurrentSelection = -1;

        // Setup the preferences in order to save the default location
        mPrefs = getActivity().getSharedPreferences(FriscoUtil.PREFS_APP, Context.MODE_PRIVATE);
        int storedLoc = mPrefs.getInt(FriscoUtil.PREFS_DEFAULT_LOCATION, -1);

        if(storedLoc == FriscoUtil.LOCATION_CROFTON) {
            mCurrentSelection = 1;
        }
        else {
            mCurrentSelection = 0;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.layout_default_location, null);

        // Now build the dialog box to be displayed
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);

        // Build the dialog box
        dialogBuilder
                .setTitle("Set Default Location")
                .setSingleChoiceItems(FriscoUtil.defaultLocations, mCurrentSelection, new ChoiceClick())
                .setPositiveButton("Ok", new NotifyOk())
                .setView(v);

        return dialogBuilder.create();
    }

    private class NotifyOk implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    private class ChoiceClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int position) {
            int defaultLocation = -1;

            // Determine what value to store
            switch(position) {
                case 0:
                    defaultLocation = FriscoUtil.LOCATION_COLUMBIA;
                    break;
                case 1:
                    defaultLocation = FriscoUtil.LOCATION_CROFTON;
                    break;
            }

            // When the default location is chosen the position indicates
            // which option was clicked.  Save it in the preferences.
            if(mPrefs != null) {
                Log.d("DefaultLocation", "Saving location " + defaultLocation + "to preferences");
                mPrefs.edit()
                        .putInt(FriscoUtil.PREFS_DEFAULT_LOCATION, defaultLocation)
                        .commit();
            }
        }
    }
}
