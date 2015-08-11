package com.test.papin.dictionary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class Content extends ContentProvider
{
    final String LOG_TAG = "myLogs";

    static final String d_ID = "_id";
    static final String d_word = "word";

    static final String AUTHORITY = "papin.test.com.MyCPW";
    static final String CONTACT_PATH = "dictionary";
    public static final Uri dictionary_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTACT_PATH);

    static final String dictionary_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CONTACT_PATH;
    static final String dictionary_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + CONTACT_PATH;

    static final int URI_dictionary = 1;
    static final int URI_dictionary_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH, URI_dictionary);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH + "/#", URI_dictionary_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        Log.d(LOG_TAG, "query, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_dictionary: //  Uri
                Log.d(LOG_TAG, "URI_CONTACTS");
                if (TextUtils.isEmpty(s1)) {
                    s1 = d_word + " ASC";
                }
                break;
            case URI_dictionary_ID: // Uri  ID
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(s)) {
                    s = d_ID + " = " + id;
                } else {
                    s = s + " AND " + d_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Dictionary", strings, s,
                strings1, null, null, s1);
        cursor.setNotificationUri(getContext().getContentResolver(),
                dictionary_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_dictionary)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert("Dictionary", null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(dictionary_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String s, String[] strings) {

        return 0;
    }

    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        return 0;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_dictionary:
                return dictionary_CONTENT_TYPE;
            case URI_dictionary_ID:
                return dictionary_CONTENT_ITEM_TYPE;
        }
        return null;
    }



    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "10", null, 1);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("create table Dictionary ("
                    + "_id integer primary key autoincrement,"
                    + "word text,"
                    + "translate text"+");");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
