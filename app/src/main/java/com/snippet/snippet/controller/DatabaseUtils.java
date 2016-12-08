package com.snippet.snippet.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.snippet.snippet.model.DatabaseHelper;
import com.snippet.snippet.model.FileDatabaseContract;
import com.snippet.snippet.model.PairDatabaseContract;
import com.snippet.snippet.model.TagDatabaseContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 12/3/2016.
 */

public class DatabaseUtils {

    private DatabaseUtils() {
        //Nobody is allowed to instantiate this class
    }

    private static DatabaseHelper getDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    public static void addFilePathToDB(Context context, String filePath, boolean isAutoTagged) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH, filePath);
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, isAutoTagged);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FileDatabaseContract.FileDatabase.TABLE_NAME, null, values);
    }

    public static void addFilePathsToDB(Context context, List<String> filePaths) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for (String filePath: filePaths) {
            values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH, filePath);
            values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, false);
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FileDatabaseContract.FileDatabase.TABLE_NAME, null, values);
    }

    public static void addTagToDB(Context context, String tag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME, tag);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TagDatabaseContract.TagDatabase.TABLE_NAME, null, values);
    }

    public static void addPairToDB(Context context, int fileID, int tagID) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

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
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

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

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

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
            if(i < tagIDs.size()-1) {
                pairSelection += "?, ";
            }
            else {
                pairSelection += "?)";
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
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

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

    /**
     * Retrieves all of the filepaths from the database that are associated with the given tags
     * @param context
     * @param Tag
     * @return
     */
    public static List<String> getImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " IN (";
        String[] tagSelectionArgs = new String[Tag.size()];
        for (int i = 0; i < Tag.size(); i++) {
            tagSelectionArgs[i] = Tag.get(i);
            if(i < Tag.size()-1) {
                tagSelection += "?, ";
            }
            else {
                tagSelection += "?)";
            }
        }

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

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

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
            if(i < tagIDs.size()-1) {
                pairSelection += "?, ";
            }
            else {
                pairSelection += "?)";
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
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

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
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

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
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

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

    public static void removeAllTables(Context context) {

        DatabaseHelper DBHelper = getDatabaseHelper(context);

        SQLiteDatabase db = DBHelper.getWritableDatabase();
        DBHelper.removeTables(db);

        db.close();
    }

    public static List<String> getAllFilePaths(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        String sortOrder = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        c.moveToFirst();
        while(!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
        }

        return paths;
    }

    public static boolean getAutoTaggedFromFilePath(Context context, String filePath) {
        boolean isAutoTagged = false;

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] selectionArgs = { filePath };

        Cursor c = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        c.moveToFirst();
        int value = c.getInt(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED));

        if(value != 0) {
            isAutoTagged = true;
        }

        return isAutoTagged;
    }

    /**
     * Updates the AutoTagged column in the row of the file given (if it is in the database)
     * @param context The application context to fetch the database
     * @param filePath The path of the file in the database to be updated
     * @param newVal The new value for the AutoTagged column of the desired row in the table
     * @return An integer indicating how many rows were effected. THIS SHOULD ALWAYS RETURN 1 (or zero if it was already set)
     */
    public static int setAutoTaggedFromFilePath(Context context, String filePath, boolean newVal) {
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, newVal);

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] selectionArgs = { filePath };

        int count = db.update(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return count;
    }

    public static List<String> getUntaggedImagesFromDB(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
            FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ?";
        String[] selectionArgs = { "0" };

        String order = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                order
        );

        c.moveToFirst();
        while(!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        return paths;
    }

    /**
     * Retrieves all of the untagged filepaths from the database that are associated with the given tags
     * @param context
     * @param Tag
     * @return
     */
    public static List<String> getUntaggedImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " IN (";
        String[] tagSelectionArgs = new String[Tag.size()];
        for (int i = 0; i < Tag.size(); i++) {
            tagSelectionArgs[i] = Tag.get(i);
            if(i < Tag.size()-1) {
                tagSelection += "?, ";
            }
            else {
                tagSelection += "?)";
            }
        }

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

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

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
            if(i < tagIDs.size()-1) {
                pairSelection += "?, ";
            }
            else {
                pairSelection += "?)";
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
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        // Making the WHERE TagName = Tag statement
        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ? AND " + FileDatabaseContract.FileDatabase._ID + " IN (";

        String[] fileArgs = new String[fileIDs.size() + 1];
        fileArgs[0] = "0";
        for (int i = 1; i < fileIDs.size() + 1; i++) {
            fileArgs[i] = Integer.toString(fileIDs.get(i-1));
            if(i < fileIDs.size()) {
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

    public static List<String> getTaggedImagesFromDB(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ?";
        String[] selectionArgs = { "1" };

        String order = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                order
        );

        c.moveToFirst();
        while(!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        return paths;
    }

    /**
     * Retrieves all of the untagged filepaths from the database that are associated with the given tags
     * @param context
     * @param Tag
     * @return
     */
    public static List<String> getTaggedImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " IN (";
        String[] tagSelectionArgs = new String[Tag.size()];
        for (int i = 0; i < Tag.size(); i++) {
            tagSelectionArgs[i] = Tag.get(i);
            if(i < Tag.size()-1) {
                tagSelection += "?, ";
            }
            else {
                tagSelection += "?)";
            }
        }

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

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

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
            if(i < tagIDs.size()-1) {
                pairSelection += "?, ";
            }
            else {
                pairSelection += "?)";
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
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        List<Integer> temp = new ArrayList<>();
        for (Integer i: fileIDs) {
            if(!temp.contains(i)) {
                temp.add(i);
            }
        }
        fileIDs = temp;

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };


        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ? AND " + FileDatabaseContract.FileDatabase._ID + " IN (";

        String[] fileArgs = new String[fileIDs.size() + 1];
        fileArgs[0] = "1";
        for (int i = 1; i < fileIDs.size() + 1; i++) {
            fileArgs[i] = Integer.toString(fileIDs.get(i-1));
            if(i < fileIDs.size()) {
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

    public static List<String> getTagsFromFilePath(Context context, String filePath) {
        List<String> tags = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] filesProjection = {
                FileDatabaseContract.FileDatabase._ID
        };

        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] fileSelectionArgs = {filePath};

        String fileOrder = FileDatabaseContract.FileDatabase._ID + " DESC";

        Cursor c1 = db.query(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                filesProjection,
                fileSelection,
                fileSelectionArgs,
                null,
                null,
                fileOrder
        );

        List<Integer> fileIDs = new ArrayList<>();

        c1.moveToFirst();
        while(!c1.isAfterLast()) {
            fileIDs.add(c1.getInt(c1.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase._ID)));
            c1.moveToNext();
        }
        c1.close();

        ///////////////////////////Pairs Query///////////////////////////////////////////////////////

        String[] pairsProjection = {
                PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID
        };

        String pairsSelection = PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " IN (";
        String[] pairsSelectionArgs = new String[fileIDs.size()];

        for(int i = 0; i < fileIDs.size(); i++) {
            pairsSelectionArgs[i] = Integer.toString(fileIDs.get(i));
            if(i < fileIDs.size() - 1) {
                pairsSelection += "?, ";
            }
            else {
                pairsSelection += "?)";
            }
        }

        String pairsOrder = PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + " DESC";

        Cursor c2 = db.query(
                PairDatabaseContract.PairDatabase.TABLE_NAME,
                pairsProjection,
                pairsSelection,
                pairsSelectionArgs,
                null,
                null,
                pairsOrder
        );

        List<Integer> tagIDs = new ArrayList<>();

        c2.moveToFirst();
        while(!c2.isAfterLast()) {
            tagIDs.add(c2.getInt(c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID)));
            c2.moveToNext();
        }
        c2.close();

        List<Integer> temp = new ArrayList<>();
        for (Integer i: tagIDs) {
            if(!temp.contains(i)) {
                temp.add(i);
            }
        }
        tagIDs = temp;

        ///////////////////////////Tags Query////////////////////////////////////////////////////////////////

        String[] tagsProjection = {
                TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME
        };

        String tagsSelection = TagDatabaseContract.TagDatabase._ID + " IN (";
        String[] tagsSelectionArgs = new String[tagIDs.size()];

        for(int i = 0; i < tagIDs.size(); i++) {
            tagsSelectionArgs[i] = Integer.toString(tagIDs.get(i));
            if(i < tagIDs.size() - 1) {
                tagsSelection += "?, ";
            }
            else {
                tagsSelection += "?)";
            }
        }

        Cursor c3 = db.query(
                TagDatabaseContract.TagDatabase.TABLE_NAME,
                tagsProjection,
                tagsSelection,
                tagsSelectionArgs,
                null,
                null,
                null
        );

        c3.moveToFirst();
        while(!c3.isAfterLast()) {
            tags.add(c3.getString(c3.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME)));
            c3.moveToNext();
        }
        c3.close();

        return tags;
    }

}
