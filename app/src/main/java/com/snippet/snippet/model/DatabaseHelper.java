package com.snippet.snippet.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kyle on 12/3/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String UNIQUE = " UNIQUE";
    private static final String INT_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES_FILES =
            "CREATE TABLE IF NOT EXISTS " + FileDatabaseContract.FileDatabase.TABLE_NAME + " (" +
                    FileDatabaseContract.FileDatabase._ID + " INTEGER PRIMARY KEY" + " AUTOINCREMENT" + COMMA_SEP +
                    FileDatabaseContract.FileDatabase.COLUMN_NAME_FILEPATH + TEXT_TYPE + UNIQUE + NOT_NULL + COMMA_SEP +
                    FileDatabaseContract.FileDatabase.COLUMN_NAME_AUTOTAGGED + BOOLEAN_TYPE + NOT_NULL + " )";

    private static final String SQL_CREATE_ENTRIES_PAIRS =
            "CREATE TABLE IF NOT EXISTS " + PairDatabaseContract.PairDatabase.TABLE_NAME + " (" +
                    PairDatabaseContract.PairDatabase._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    PairDatabaseContract.PairDatabase.COLUMN_NAME_FILEID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    PairDatabaseContract.PairDatabase.COLUMN_NAME_TAGID + INT_TYPE + NOT_NULL +
                    ")";

    private static final String SQL_CREATE_ENTRIES_TAGS =
            "CREATE TABLE IF NOT EXISTS " + TagDatabaseContract.TagDatabase.TABLE_NAME + " (" +
                    TagDatabaseContract.TagDatabase._ID + INT_TYPE + NOT_NULL + COMMA_SEP +
                    TagDatabaseContract.TagDatabase.COLUMN_NAME_TAGNAME + TEXT_TYPE + UNIQUE + NOT_NULL + COMMA_SEP +
                    " PRIMARY KEY (" + TagDatabaseContract.TagDatabase._ID + ")" +
                    ")";

    private static final String SQL_DELETE_ENTRIES_FILES =
            "DROP TABLE IF EXISTS " + FileDatabaseContract.FileDatabase.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_PAIRS =
            "DROP TABLE IF EXISTS " + PairDatabaseContract.PairDatabase.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_TAGS =
            "DROP TABLE IF EXISTS " + TagDatabaseContract.TagDatabase.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Snippet.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_FILES);
        db.execSQL(SQL_CREATE_ENTRIES_TAGS);
        db.execSQL(SQL_CREATE_ENTRIES_PAIRS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_PAIRS);
        db.execSQL(SQL_DELETE_ENTRIES_TAGS);
        db.execSQL(SQL_DELETE_ENTRIES_FILES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void removeTables(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES_PAIRS);
        db.execSQL(SQL_DELETE_ENTRIES_TAGS);
        db.execSQL(SQL_DELETE_ENTRIES_FILES);
    }

}