package com.snippet.snippet.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getImagePathsWithTag(Context context, String Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Gets the data repository in read mode
        SQLiteDatabase db = getTagsDatabaseReference(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " = ?";
        String[] tagSelectionArgs = { Tag };

        // Stort order of the resulting table from the query
        String tagSortOrder = TagDatabaseContract.TagDatabase._ID + " DESC";

        Cursor c1 = db.query(
                TagDatabaseContract.TagDatabase.TABLE_NAME,
                tagProjection,
                tagSelection,
                tagSelectionArgs,
                null,
                null,
                tagSortOrder
        );

        c1.moveToFirst();
        while(!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }
        
        c1.close();
        
        db.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        db = getPairsDatabaseReference(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] pairProjection = {
                PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID
        };

        // Making the WHERE TagName = Tag statement
        String pairSelection = PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + " IN (";

        String[] pairArgs = new String[tagIDs.size()];
        for (int i = 0; i < tagIDs.size(); i++) {
            pairArgs[i] = Integer.toString(tagIDs.get(i));
            if(i < fileIDs.size()-1) {
                tagSelection += "?, ";
            }
            else {
                tagSelection += "?)";
            }
        }

        String [] pairSelectionArgs = pairArgs;

        // Stort order of the resulting table from the query
        String pairSortOrder = PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " DESC";

        Cursor c2 = db.query(
                PairDatabaseContract.PairDatabase.TABLE_NAME,
                pairProjection,
                pairSelection,
                pairSelectionArgs,
                null,
                null,
                pairSortOrder
        );

        c2.moveToFirst();
        while(!c2.isAfterLast()) {
            tagIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        db.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        db = getFilesDatabaseReference(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        // Making the WHERE TagName = Tag statement
        String fileSelection = FileDatabaseContract.FileDatabase._ID + " IN (";

        String[] fileArgs = new String[fileIDs.size()];
        for (int i = 0; i < fileIDs.size(); i++) {
            fileArgs[i] = Integer.toString(fileIDs.get(i));
            if(i < fileIDs.size()-1) {
                fileSelection += "?, ";
            }
            else {
                fileSelection += "?)";
            }
        }

        String [] fileSelectionArgs = fileArgs;

        // Stort order of the resulting table from the query
        String fileSortOrder = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c3 = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                fileProjection,
                fileSelection,
                fileSelectionArgs,
                null,
                null,
                fileSortOrder
        );

        c3.moveToFirst();
        while(!c3.isAfterLast()) {
            paths.add(c3.getString(
                    c3.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)
            ));
            c3.moveToNext();
        }

        c3.close();

        db.close();
        
        return paths;
    }
    
    public static int getFileIDFromPath(Context context, String pathName) {
        int fileID;

        // Gets the data repository in read mode
        SQLiteDatabase db = getFilesDatabaseReference(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase._ID
        };

        // Making the WHERE FileName = File statement
        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] fileSelectionArgs = { pathName };

        // Stort order of the resulting table from the query
        String fileSortOrder = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c1 = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                fileProjection,
                fileSelection,
                fileSelectionArgs,
                null,
                null,
                fileSortOrder
        );

        c1.moveToFirst();
        fileID = c1.getInt(c1.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase._ID));

        c1.close();

        db.close();

        return fileID;
    }

    public static int getTagIDFromTag(Context context, String Tag) {
        int tagID;

        // Gets the data repository in read mode
        SQLiteDatabase db = getTagsDatabaseReference(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " = ?";
        String[] tagSelectionArgs = { Tag };

        // Stort order of the resulting table from the query
        String tagSortOrder = TagDatabaseContract.TagDatabase._ID + " DESC";

        Cursor c1 = db.query(
                TagDatabaseContract.TagDatabase.TABLE_NAME,
                tagProjection,
                tagSelection,
                tagSelectionArgs,
                null,
                null,
                tagSortOrder
        );

        c1.moveToFirst();
        tagID = c1.getInt(c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID));

        c1.close();

        db.close();

        return tagID;
    }

}
