package com.friscotaphouse.beer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Zach on 8/8/2015.
 */
public class BeerDataSource {
    private static final String TAG = "FriscoUtil DataSource";
    private SQLiteDatabase mDatabase;
    private BeerDbHelper mDbHelper;
    private String mTableName;
    private long mLocation;

    private String[] allBeerColumns = {
            BeerDbHelper.COL_ID,
            BeerDbHelper.COL_FRISCO_ID,
            BeerDbHelper.COL_NAME,
            BeerDbHelper.COL_ACTIVE,
            BeerDbHelper.COL_NEW_BEER,
            BeerDbHelper.COL_ABV,
            BeerDbHelper.COL_OUNCES
    };

    public BeerDataSource(Context context) {
        mDbHelper = new BeerDbHelper(context);
        mTableName = BeerDbHelper.TABLE_COLUMBIA;
        mLocation = 0;
    }

    public BeerDataSource(Context context, String t, long loc) {
        mDbHelper = new BeerDbHelper(context);
        mTableName = t;
        mLocation = loc;
    }

    public void open() throws SQLException {
        Log.d(TAG, "Open Database");

        mDatabase = mDbHelper.getWritableDatabase();

        if(mDatabase == null) {
            Log.d(TAG, "open(): Database pointer is null");
        }
    }

    public void clearTable() {
        if(mDatabase.isOpen()) {
            try {
                mDatabase.execSQL("DELETE FROM " +
                                mTableName + " WHERE " + BeerDbHelper.COL_FRISCO_ID + " = " + mLocation
                );
            }
            catch (Exception e) {
                Log.d(TAG, "" + e.toString());
            }
        }
        else {
            Log.d(TAG, "Database not open");
        }
    }

    public void close() {
        Log.d(TAG, "Close Database");
        mDbHelper.close();
    }

    public void deleteBeer(Beer beer) {
        long id = beer.getId();

        mDatabase.delete(
                mTableName,
                BeerDbHelper.COL_NAME + " = " + id + " AND " + BeerDbHelper.COL_FRISCO_ID + " = " + mLocation,
                null
        );
    }

    public Beer getBeer(String name) {
        Beer beer = null;

        Cursor cursor = mDatabase.query(
                mTableName,
                allBeerColumns,
                BeerDbHelper.COL_NAME + " = " + name + " AND " + BeerDbHelper.COL_FRISCO_ID + " = " + mLocation,
                null,
                null,
                null,
                null
        );

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            beer = cursorToBeer(cursor);
            cursor.close();
        }

        return beer;
    }

    public Beer getBeer(long id) {
        Beer beer = null;

        Cursor cursor = mDatabase.query(
                mTableName,
                allBeerColumns,
                BeerDbHelper.COL_NAME + " = " + id + " AND " + BeerDbHelper.COL_FRISCO_ID + " = " + mLocation,
                null,
                null,
                null,
                null
        );

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            beer = cursorToBeer(cursor);
            cursor.close();
        }

        return beer;
    }

    public ArrayList<Beer> getAllBeers() {
        ArrayList<Beer> beers = new ArrayList<Beer>();

        try {
            Cursor cursor = mDatabase.query(
                    mTableName,
                    allBeerColumns,
                    BeerDbHelper.COL_FRISCO_ID + " = " + mLocation,
                    null,
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                Beer beer = cursorToBeer(cursor);
                beers.add(beer);
                cursor.moveToNext();
            }

            cursor.close();
        }
        catch(Exception e) {
            Log.d(TAG, "" + e.toString());
        }

        return beers;
    }

    public long insertBeer(Beer beer) {
        long insertId = -1;
        ContentValues values = new ContentValues();

        values.put(BeerDbHelper.COL_FRISCO_ID, beer.getFriscoId());
        values.put(BeerDbHelper.COL_NAME, beer.getName());
        values.put(BeerDbHelper.COL_ACTIVE, beer.getActive());
        values.put(BeerDbHelper.COL_NEW_BEER, beer.getNewBeer());
        values.put(BeerDbHelper.COL_ABV, beer.getAbv());
        values.put(BeerDbHelper.COL_OUNCES, beer.getOunces());

        try {
            insertId = mDatabase.insert(
                    mTableName,
                    null,
                    values
            );
        }
        catch(Exception e) {
            Log.d(TAG, "" + e.toString());
        }

        return insertId;
    }

    private Beer cursorToBeer(Cursor cursor) {
        Beer beer = new Beer();

        beer.setId(cursor.getLong(0));
        beer.setFriscoId(cursor.getLong(1));
        beer.setName(cursor.getString(2));
        beer.setActive(cursor.getInt(3));
        beer.setNewBeer(cursor.getInt(4));
        beer.setAbv(cursor.getString(5));
        beer.setOunces(cursor.getInt(6));

        return beer;
    }
}
