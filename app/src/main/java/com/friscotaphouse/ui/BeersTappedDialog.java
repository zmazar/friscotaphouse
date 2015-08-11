package com.friscotaphouse.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ListView;

import com.friscotaphouse.R;
import com.friscotaphouse.beer.Beer;
import com.friscotaphouse.beer.BeerAdapter;

import java.util.ArrayList;

/**
 * Created by Zach on 8/8/2015.
 */
public class BeersTappedDialog extends DialogFragment {
    private ArrayList<Beer> mBeers;
    private BeerAdapter mAdapter;
    private ListView mListView;

    public static final BeersTappedDialog newInstance(ArrayList<Beer> arg) {
        BeersTappedDialog f = new BeersTappedDialog();
        Bundle bdl = new Bundle(1);

        // Set bundle arguments to pass in
        bdl.putSerializable("beers", arg);
        f.setArguments(bdl);

        return f;
    }

    public BeersTappedDialog() {
        // Required empty constructor
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Grab the beers to be verified.
        mBeers = (ArrayList<Beer>) getArguments().getSerializable("beers");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_beers_tapped, null);

        // Setup the array list and adapter
        mListView = (ListView) v.findViewById(R.id.beers_tapped_list_view);
        mAdapter = new BeerAdapter(
                getActivity(),
                R.layout.beer_item,
                mBeers
        );

        mListView.setAdapter(mAdapter);

        // Now build the dialog box to be displayed
        AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        dialogBuilder.setTitle(mBeers.size() + " New Beers Tapped");
        dialogBuilder.setView(v);

        // Setup the Ok button to close the dialog
        dialogBuilder.setPositiveButton("Ok", new NotifyOk());

        return dialogBuilder.create();
    }

    private class NotifyOk implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();

        }
    }
}
