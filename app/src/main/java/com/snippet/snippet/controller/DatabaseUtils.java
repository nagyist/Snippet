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

    /**
     * Gets an instance of the database helper in order to be able to get a readable/writable
     * database from it.
     *
     * @param context
     * @return
     */
    private static DatabaseHelper getDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }

    /**
     * Adds a single file path to the Files table in the database
     *
     * @param context      The application context needed to get the database helper
     * @param filePath     The file path of the image being added to the table
     * @param isAutoTagged Whether the file has been tagged by Clarifai or not
     */
    public static void addFilePathToDB(Context context, String filePath, boolean isAutoTagged) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH, filePath);
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, isAutoTagged);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FileDatabaseContract.FileDatabase.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Adds multiple file paths to the Files table in the database.
     * NOTE: This will default every added path to not AutoTagged by Clarifai
     *
     * @param context   The application context needed to get the database helper
     * @param filePaths The file paths of the image being added to the table
     */
    public static void addFilePathsToDB(Context context, List<String> filePaths) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        for (String filePath : filePaths) {
            values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH, filePath);
            values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, false);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FileDatabaseContract.FileDatabase.TABLE_NAME, null, values);
        }
        db.close();
    }

    /**
     * Adds a single tag to the Tags table in the Database
     *
     * @param context The application context needed to get the database helper
     * @param tag     The tag being added to the table
     */
    public static void addTagToDB(Context context, String tag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME, tag);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TagDatabaseContract.TagDatabase.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Adds a single pair of file ID and tag ID to the Pairs database. Doing this creates a reference
     * between the image with that ID and the tag with that ID.
     *
     * @param context The application context needed to get the database helper
     * @param fileID  The file ID of the file path you want to associate
     * @param tagID   The tag ID of the tag you want to associate
     */
    public static void addPairToDB(Context context, int fileID, int tagID) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getDatabaseHelper(context).getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID, fileID);
        values.put(PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID, tagID);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(PairDatabaseContract.PairDatabase.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Associates a single tag with a singe file path. If either don't already exist in a table, they
     * will be added to their respective tables before association in the Pairs table.
     *
     * @param context  The application context needed to get the database helper
     * @param Tag      The tag you wish to associate with the given image
     * @param filePath The file path of the image you want the tag associated with
     */
    public static void addTagToFilePath(Context context, String Tag, String filePath) {
        int tagID;
        int fileID;
        try {
            tagID = getTagIDFromTag(context, Tag);
        } catch (Exception e) {
            addTagToDB(context, Tag);
            tagID = getTagIDFromTag(context, Tag);
        }

        try {
            fileID = getFileIDFromPath(context, filePath);
            if (fileID == -1) {
                addFilePathToDB(context, filePath, false);
                fileID = getFileIDFromPath(context, filePath);
            }
        } catch (Exception e) {
            addFilePathToDB(context, filePath, false);
            fileID = getFileIDFromPath(context, filePath);
        }

        addPairToDB(context, fileID, tagID);
    }

    /**
     * Associates multiple tags with a singe file path. If either don't already exist in a table, they
     * will be added to their respective tables before association in the Pairs table.
     *
     * @param context  The application context needed to get the database helper
     * @param Tags     The tags you wish to associate with the given image
     * @param filePath The file path of the image you want the tags associated with
     */
    public static void addTagToFilePath(Context context, List<String> Tags, String filePath) {
        List<Integer> tagIDs = new ArrayList<>();
        int fileID;

        for (String Tag : Tags) {
            try {
                tagIDs.add(getTagIDFromTag(context, Tag));
            } catch (Exception e) {
                addTagToDB(context, Tag);
                tagIDs.add(getTagIDFromTag(context, Tag));
            }
        }

        try {
            fileID = getFileIDFromPath(context, filePath);
            if (fileID == -1) {
                addFilePathToDB(context, filePath, false);
                fileID = getFileIDFromPath(context, filePath);
            }
        } catch (Exception e) {
            addFilePathToDB(context, filePath, false);
            fileID = getFileIDFromPath(context, filePath);
        }

        for (Integer tagID : tagIDs) {
            addPairToDB(context, fileID, tagID);
        }
    }

    /**
     * Retrieves all of the image file paths associated with the given tag.
     *
     * @param context The application context needed to get the database helper
     * @param Tag     The tag you wish to search by
     * @return A list of file paths as strings
     */
    public static List<String> getImagePathsWithTag(Context context, String Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tag, return empty list
        if (Tag == null) {
            return paths;
        }

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " = ?";
        String[] tagSelectionArgs = {Tag};

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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tagIDs, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            int temp = c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            );
            if (!fileIDs.contains(temp)) {
                fileIDs.add(temp);
            }
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no fileIDs, return empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

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
            if (i < fileIDs.size() - 1) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     *
     * @param context
     * @param Tag
     * @return
     */
    public static List<String> getImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tags, return empty list
        if (Tag.size() == 0) {
            return paths;
        }

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
            if (i < Tag.size() - 1) {
                tagSelection += "?, ";
            } else {
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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if tagIDs empty, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if fileIDs empty, return empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

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
            if (i < fileIDs.size() - 1) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     * Retrieves The fileID from the Files table with the given file path
     *
     * @param context  The application context needed to use the database helper
     * @param pathName The file path you want the ID for
     * @return The ID for the given path or -1 if no ID was found
     */
    public static int getFileIDFromPath(Context context, String pathName) {
        int fileID = -1;

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase._ID
        };

        // Making the WHERE FileName = File statement
        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] fileSelectionArgs = {pathName};

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
        if (!c1.isAfterLast()) {
            fileID = c1.getInt(c1.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase._ID));
        }

        c1.close();

        db.close();

        return fileID;
    }

    /**
     * Retrieves the tagID from the Tags table with the given tag name
     *
     * @param context The application context needed to use the database helper
     * @param Tag     The tag you want the ID for
     * @return The ID for the given tag
     */
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
        String[] tagSelectionArgs = {Tag};

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

    /**
     * Deletes all of the tables from the database. This is used mainly as a "clean start" or for debugging purposes.
     *
     * @param context The application context needed to use the database helper
     */
    public static void removeAllTables(Context context) {

        DatabaseHelper DBHelper = getDatabaseHelper(context);

        SQLiteDatabase db = DBHelper.getWritableDatabase();
        DBHelper.removeTables(db);

        db.close();
    }

    /**
     * Retrieves every file path that is stored in the Files table of the Database
     *
     * @param context The application context needed to use the database helper
     * @return A list of every file path in the database
     */
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
        while (!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return paths;
    }

    /**
     * Retrieves every tag that is stored in the Tags table of the Database
     * @param context The application context needed to use the database helper
     * @return A list of every Tag in the database
     */
    public static List<String> getAllTags(Context context) {
        List<String> tags = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME
        };

        String sortOrder = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " ASC";

        Cursor c = db.query(
                TagDatabaseContract.TagDatabase.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        c.moveToFirst();
        while(!c.isAfterLast()) {
            tags.add(c.getString(c.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME)));
            c.moveToNext();
        }

        return tags;
    }

    /**
     * Retrieves whether or not the image with the given file path has been tagged by Clarifai yet.
     *
     * @param context  The application context needed to use the database helper
     * @param filePath The image you would like to see whether or not it has been tagged by Clarifai
     * @return Either True if it has been tagged by Clarifai, False otherwise
     */
    public static boolean getAutoTaggedFromFilePath(Context context, String filePath) {
        boolean isAutoTagged = false;

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] selectionArgs = {filePath};

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

        if (value != 0) {
            isAutoTagged = true;
        }

        c.close();
        db.close();

        return isAutoTagged;
    }

    /**
     * Updates the AutoTagged column in the row of the file given (if it is in the database)
     *
     * @param context  The application context to fetch the database
     * @param filePath The path of the file in the database to be updated
     * @param newVal   The new value for the AutoTagged column of the desired row in the table
     * @return An integer indicating how many rows were effected. THIS SHOULD ALWAYS RETURN 1 (or zero if it was already set)
     */
    public static int setAutoTaggedFromFilePath(Context context, String filePath, boolean newVal) {
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED, newVal);

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + " = ?";
        String[] selectionArgs = {filePath};

        int count = db.update(
                FileDatabaseContract.FileDatabase.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        db.close();

        return count;
    }

    /**
     * Retrieves all of the file paths not tagged by Clarifai from the database.
     *
     * @param context The application context needed to use the database helper
     * @return A list of file paths as Strings
     */
    public static List<String> getUntaggedImagesFromDB(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ?";
        String[] selectionArgs = {"0"};

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
        while (!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return paths;
    }

    /**
     * Retrieves all of the filepaths not tagged by Clarifai from the database that are associated with the given tags
     *
     * @param context The application context needed to use the database helper
     * @param Tag     The tags to perform the search with
     * @return A list of file paths as Strings
     */
    public static List<String> getUntaggedImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tags to search, return empty list
        if (Tag.size() == 0) {
            return paths;
        }

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
            if (i < Tag.size() - 1) {
                tagSelection += "?, ";
            } else {
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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tagIDs, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no fileIDs, return empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

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
            fileArgs[i] = Integer.toString(fileIDs.get(i - 1));
            if (i < fileIDs.size()) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     * Retrieves all of the filepaths not tagged by Clarifai from the database that are associated with the given tag
     *
     * @param context The application context needed to use the database helper
     * @param Tag     The tag to perform the search with
     * @return A list of file paths as Strings
     */
    public static List<String> getUntaggedImagePathsWithTag(Context context, String Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if Tag is null, return empty list
        if (Tag == null) {
            return paths;
        }

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + "  = ?";
        String[] tagSelectionArgs = {Tag};

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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //If no tagIDs exists, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //If there are no files, just return the empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

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
            fileArgs[i] = Integer.toString(fileIDs.get(i - 1));
            if (i - 1 < fileIDs.size() - 1) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     * Retrieves all of the file paths tagged by Clarifai from the database
     *
     * @param context The application context needed to use the database helper
     * @return A list of file paths as Strings
     */
    public static List<String> getTaggedImagesFromDB(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };

        String selection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ?";
        String[] selectionArgs = {"1"};

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
        while (!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return paths;
    }

    /**
     * Retrieves all of the filepaths tagged by Clarifai from the database that are associated with the given tags
     *
     * @param context The application context needed to use the database helper
     * @param Tag     The tags to perform the search with
     * @return A list of file paths as Strings
     */
    public static List<String> getTaggedImagePathsWithTag(Context context, List<String> Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tags, return empty list
        if (Tag.size() == 0) {
            return paths;
        }

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
            if (i < Tag.size() - 1) {
                tagSelection += "?, ";
            } else {
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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tagIDs, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        List<Integer> temp = new ArrayList<>();
        for (Integer i : fileIDs) {
            if (!temp.contains(i)) {
                temp.add(i);
            }
        }
        fileIDs = temp;

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no fileIDs, return empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };


        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ? AND " + FileDatabaseContract.FileDatabase._ID + " IN (";

        String[] fileArgs = new String[fileIDs.size() + 1];
        fileArgs[0] = "1";
        for (int i = 1; i < fileIDs.size() + 1; i++) {
            fileArgs[i] = Integer.toString(fileIDs.get(i - 1));
            if (i < fileIDs.size()) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     * Retrieves all of the filepaths tagged by Clarifai from the database that are associated with the given tag
     *
     * @param context The application context needed to use the database helper
     * @param Tag     The tag to perform the search with
     * @return A list of file paths as Strings
     */
    public static List<String> getTaggedImagePathsWithTag(Context context, String Tag) {
        List<String> paths = new ArrayList<>();
        List<Integer> tagIDs = new ArrayList<>();
        List<Integer> fileIDs = new ArrayList<>();

        /* TAGS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tag, return empty list
        if (Tag == null) {
            return paths;
        }

        // Gets the data repository in read mode
        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] tagProjection = {
                TagDatabaseContract.TagDatabase._ID
        };

        // Making the WHERE TagName = Tag statement
        String tagSelection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " = ?";
        String[] tagSelectionArgs = {Tag};

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
        while (!c1.isAfterLast()) {
            tagIDs.add(c1.getInt(
                    c1.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase._ID)
            ));
            c1.moveToNext();
        }

        c1.close();

        /* PAIRS DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no tagIDs, return empty list
        if (tagIDs.size() == 0) {
            return paths;
        }

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
            if (i < tagIDs.size() - 1) {
                pairSelection += "?, ";
            } else {
                pairSelection += "?)";
            }
        }

        String[] pairSelectionArgs = pairArgs;

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
        while (!c2.isAfterLast()) {
            fileIDs.add(c2.getInt(
                    c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID)
            ));
            c2.moveToNext();
        }

        c2.close();

        List<Integer> temp = new ArrayList<>();
        for (Integer i : fileIDs) {
            if (!temp.contains(i)) {
                temp.add(i);
            }
        }
        fileIDs = temp;

        /* FILES DATABASE QUERY */////////////////////////////////////////////////////////////////

        //if no fileIDs, return empty list
        if (fileIDs.size() == 0) {
            return paths;
        }

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] fileProjection = {
                FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH
        };


        String fileSelection = FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = ? AND " + FileDatabaseContract.FileDatabase._ID + " IN (";

        String[] fileArgs = new String[fileIDs.size() + 1];
        fileArgs[0] = "1";
        for (int i = 1; i < fileIDs.size() + 1; i++) {
            fileArgs[i] = Integer.toString(fileIDs.get(i - 1));
            if (i < fileIDs.size()) {
                fileSelection += "?, ";
            } else {
                fileSelection += "?)";
            }
        }

        String[] fileSelectionArgs = fileArgs;

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
        while (!c3.isAfterLast()) {
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
     * Retrieves all of the tags associated with the given image file path.
     *
     * @param context  The application context needed to use the database helper
     * @param filePath The file path you want to retrieve tags from
     * @return A list of tags associated with that image as Strings
     */
    public static List<String> getTagsFromFilePath(Context context, String filePath) {
        List<String> tags = new ArrayList<>();

        //if no file path, return empty list
        if (filePath == null) {
            return tags;
        }

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
        while (!c1.isAfterLast()) {
            fileIDs.add(c1.getInt(c1.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase._ID)));
            c1.moveToNext();
        }
        c1.close();

        ///////////////////////////Pairs Query///////////////////////////////////////////////////////

        //if no fileIDs, return empty list
        if (fileIDs.size() == 0) {
            return tags;
        }

        String[] pairsProjection = {
                PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID
        };

        String pairsSelection = PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " IN (";
        String[] pairsSelectionArgs = new String[fileIDs.size()];

        for (int i = 0; i < fileIDs.size(); i++) {
            pairsSelectionArgs[i] = Integer.toString(fileIDs.get(i));
            if (i < fileIDs.size() - 1) {
                pairsSelection += "?, ";
            } else {
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
        while (!c2.isAfterLast()) {
            tagIDs.add(c2.getInt(c2.getColumnIndexOrThrow(PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID)));
            c2.moveToNext();
        }
        c2.close();

        List<Integer> temp = new ArrayList<>();
        for (Integer i : tagIDs) {
            if (!temp.contains(i)) {
                temp.add(i);
            }
        }
        tagIDs = temp;

        ///////////////////////////Tags Query////////////////////////////////////////////////////////////////

        //if no tagIDs, return empty list
        if (tagIDs.size() == 0) {
            return tags;
        }

        String[] tagsProjection = {
                TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME
        };

        String tagsSelection = TagDatabaseContract.TagDatabase._ID + " IN (";
        String[] tagsSelectionArgs = new String[tagIDs.size()];

        for (int i = 0; i < tagIDs.size(); i++) {
            tagsSelectionArgs[i] = Integer.toString(tagIDs.get(i));
            if (i < tagIDs.size() - 1) {
                tagsSelection += "?, ";
            } else {
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
        while (!c3.isAfterLast()) {
            tags.add(c3.getString(c3.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME)));
            c3.moveToNext();
        }
        c3.close();

        db.close();

        return tags;
    }

    /**
     * Removes the given tag-image association from the Pairs table.
     *
     * @param context  The application context needed to use the database helper
     * @param filePath The file path to the image you are removing the tag from
     * @param Tag      The tag being removed from the given image
     */
    public static void removeTagFromImage(Context context, String filePath, String Tag) {
        int fileID = getFileIDFromPath(context, filePath);
        int tagID = getTagIDFromTag(context, Tag);

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String selection = PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " = ? AND " + PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + " = ?";

        String[] selectionArgs = {Integer.toString(fileID), Integer.toString(tagID)};

        db.delete(PairDatabaseContract.PairDatabase.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Removes the given tag-image associations from the Pairs table.
     *
     * @param context  The application context needed to use the database helper
     * @param filePath The file path to the image you are removing the tag from
     * @param Tags     The tags being removed from the given image
     */
    public static void removeTagFromImage(Context context, String filePath, List<String> Tags) {
        int fileID = getFileIDFromPath(context, filePath);
        List<Integer> tagIDs = new ArrayList<>();

        for (String Tag : Tags) {
            tagIDs.add(getTagIDFromTag(context, Tag));
        }

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String selection = PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " = ? AND " + PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + " IN (";

        String[] selectionArgs = new String[tagIDs.size() + 1];
        selectionArgs[0] = Integer.toString(fileID);
        for (int i = 1; i < tagIDs.size() + 1; i++) {
            selectionArgs[i] = Integer.toString(tagIDs.get(i - 1));
            if (i - 1 < tagIDs.size() - 1) {
                selection += "?, ";
            } else {
                selection += "?)";
            }
        }

        db.delete(PairDatabaseContract.PairDatabase.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * Retrieves all of the tags that could potentially be the partial string. This is to be used
     * when searching to show a list of tags that already exist that the user can pick from so they
     * don't have to type the whole thing out. This will reduce issues dealing with spelling errors
     * and help speed up the user during searching.
     *
     * @param context    The application context needed to use the database helper
     * @param partialTag The partial name for a tag being searched for
     * @return A list of tags that could potentially be the partial tag
     */
    public static List<String> getTagsFromPartialQuery(Context context, String partialTag) {
        List<String> tags = new ArrayList<>();

        //if partialTag is null, return empty list
        if (partialTag == null) {
            return tags;
        }

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String[] projection = {
                TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME
        };

        String selection = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " LIKE ?";
        String[] selectionArgs = {partialTag + "%"};

        String order = TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + " COLLATE NOCASE";

        Cursor c = db.query(
                TagDatabaseContract.TagDatabase.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                order
        );

        c.moveToFirst();
        while (!c.isAfterLast()) {
            tags.add(c.getString(c.getColumnIndexOrThrow(TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return tags;
    }

    public static List<String> getImagePathsWithoutTags(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String rawQuery = "SELECT " + FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH +
                " FROM " + FileDatabaseContract.FileDatabase.TABLE_NAME +
                " WHERE " + FileDatabaseContract.FileDatabase._ID + " NOT IN " +
                "(SELECT " + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " FROM " + PairDatabaseContract.PairDatabase.TABLE_NAME + ");";

        Cursor c = db.rawQuery(rawQuery, null);

        c.moveToFirst();
        while(!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }
        c.close();
        db.close();

        return paths;
    }

    public static List<String> getUntaggedImagePathsWithoutTags(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String rawQuery = "SELECT " + FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH +
                " FROM " + FileDatabaseContract.FileDatabase.TABLE_NAME +
                " WHERE " + FileDatabaseContract.FileDatabase._ID + " NOT IN " +
                "(SELECT " + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " FROM " + PairDatabaseContract.PairDatabase.TABLE_NAME + ") AND " +
                FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = 0;";

        Cursor c = db.rawQuery(rawQuery, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return paths;
    }

    public static List<String> getTaggedImagePathsWithoutTags(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String rawQuery = "SELECT " + FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH +
                " FROM " + FileDatabaseContract.FileDatabase.TABLE_NAME +
                " WHERE " + FileDatabaseContract.FileDatabase._ID + " NOT IN " +
                "(SELECT " + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " FROM " + PairDatabaseContract.PairDatabase.TABLE_NAME + ") AND " +
                FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + " = 1;";

        Cursor c = db.rawQuery(rawQuery, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }

        c.close();
        db.close();

        return paths;
    }

    public static List<String> getImagePathsWithTags(Context context) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = getDatabaseHelper(context).getReadableDatabase();

        String rawQuery = "SELECT " + FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH +
                " FROM " + FileDatabaseContract.FileDatabase.TABLE_NAME  +
                " WHERE " + FileDatabaseContract.FileDatabase._ID + " IN " +
                "(SELECT " + PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + " FROM " + PairDatabaseContract.PairDatabase.TABLE_NAME + ");";

        Cursor c = db.rawQuery(rawQuery, null);

        c.moveToFirst();
        while(!c.isAfterLast()) {
            paths.add(c.getString(c.getColumnIndexOrThrow(FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH)));
            c.moveToNext();
        }
        c.close();
        db.close();

        return paths;
    }

    public static void createDatabaseTables(Context context) {
        DatabaseHelper helper = getDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onCreate(db);
        db.close();
    }

}

