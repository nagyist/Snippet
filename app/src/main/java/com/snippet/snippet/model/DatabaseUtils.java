package com.snippet.snippet.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Kyle on 12/3/2016.
 */

public class DatabaseUtils {

    private DatabaseUtils() {
        //Nobody is allowed to instantiate this class
    }

    private static FileDatabaseHelper getFilesDatabaseReference(Context context) {
        return new FileDatabaseHelper(context);
    }

    private static TagDatabaseHelper getTagsDatabaseReference(Context context) {
        return new TagDatabaseHelper(context);
    }

    private static PairDatabaseHelper getPairsDatabaseReference(Context context) {
        return new PairDatabaseHelper(context);
    }

    public static void addFilePathToDB(Context context, String filePath, boolean isAutoTagged) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getFilesDatabaseReference(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH, filePath);
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, isAutoTagged);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FileDatabaseContract.FileDatabase.TABLE_NAME, null, values);
    }

    public static void addTagToDB(Context context, String tag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getTagsDatabaseReference(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME, tag);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TagDatabaseContract.TagDatabase.TABLE_NAME, null, values);
    }

    public static void addPairToDB(Context context, int fileID, int tagID) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getPairsDatabaseReference(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID, fileID);
        values.put(PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID, tagID);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(PairDatabaseContract.PairDatabase.TABLE_NAME, null, values);
    }

}
