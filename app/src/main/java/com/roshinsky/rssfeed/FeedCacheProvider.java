package com.roshinsky.rssfeed;

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


public class FeedCacheProvider extends ContentProvider {
    static final String DB_NAME = "HISTORY_DB";
    static final int DB_VERSION = 1;

    static final String FEEDS_TABLE = "FEEDS";

    static final String FEED_ITEM_ID = "_id";
    static final String FEED_ITEM_TITLE = "title";
    static final String FEED_ITEM_DESCRIPTION = "description";
    static final String FEED_ITEM_IMAGE_URL = "image_url";
    static final String FEED_ITEM_LINK = "link";

    static final String DB_CREATE = "create table " + FEEDS_TABLE + "("
            + FEED_ITEM_ID + " integer primary key autoincrement, "
            + FEED_ITEM_TITLE + " text, "
            + FEED_ITEM_DESCRIPTION + " text, "
            + FEED_ITEM_IMAGE_URL + " text, "
            + FEED_ITEM_LINK + " text" + ");";

    static final String AUTHORITY = "com.roshin.providers.feeds";

    static final String FEED_PATH = "feeds";

    public static final Uri FEED_ITEM_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FEED_PATH);

    static final String FEEDS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + FEED_PATH;

    static final String FEED_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + FEED_PATH;

    static final int URI_FEED_ITEMS = 1;

    static final int URI_FEED_ITEMS_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FEED_PATH, URI_FEED_ITEMS);
        uriMatcher.addURI(AUTHORITY, FEED_PATH + "/#", URI_FEED_ITEMS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;


    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case URI_FEED_ITEMS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = FEED_ITEM_TITLE + " ASC";
                }
                break;
            case URI_FEED_ITEMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FEED_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FEED_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(FEEDS_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), FEED_ITEM_CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_FEED_ITEMS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(FEEDS_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(FEED_ITEM_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_FEED_ITEMS:
                break;
            case URI_FEED_ITEMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FEED_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FEED_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int count = db.delete(FEEDS_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_FEED_ITEMS:
                break;
            case URI_FEED_ITEMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FEED_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FEED_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(FEEDS_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_FEED_ITEMS:
                return FEEDS_CONTENT_TYPE;
            case URI_FEED_ITEMS_ID:
                return FEED_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}