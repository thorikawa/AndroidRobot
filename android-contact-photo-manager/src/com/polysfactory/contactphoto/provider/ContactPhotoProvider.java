package com.polysfactory.contactphoto.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.polysfactory.contactphoto.util.Log;

/**
 * 独自のコンテンツプロバイダー
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class ContactPhotoProvider extends ContentProvider {

    public static final int CONTACT_PHOTO = 1;

    public static final int CONTACT_PHOTO_ID = 2;

    /** SQLiteヘルパー */
    private SQLiteOpenHelper mDatabaseHelper;

    /** URIマッチャー */
    private UriMatcher mUrimatcher;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new SQLiteSupportImpl(getContext());
        mUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUrimatcher.addURI(ContactPhotoStore.AUTHORITY, "contactphoto", CONTACT_PHOTO);
        mUrimatcher.addURI(ContactPhotoStore.AUTHORITY, "contactphoto/#", CONTACT_PHOTO_ID);
        return true;
    }

    /**
     * マッチング定数から対応するSQLiteHelperを返す
     * @param match マッチング定数
     * @return SqliteDatabase
     */
    private SQLiteDatabase getSQLiteDatabase(int match) {
        switch (match) {
        case CONTACT_PHOTO:
        case CONTACT_PHOTO_ID:
            return mDatabaseHelper.getWritableDatabase();
        default:
            return null;
        }
    }

    /**
     * マッチング定数から対応するテーブル名を返す
     * @param match マッチング定数
     * @return テーブル名
     */
    private static String getTable(int match) {
        String table;
        switch (match) {
        case CONTACT_PHOTO:
        case CONTACT_PHOTO_ID:
            table = "ContactPhoto";
            break;
        default:
            table = null;
            break;
        }
        return table;
    }

    /**
     * マッチング定数ごとのWhere条件句を取得する
     * @param match マッチング定数
     * @return Where条件句
     */
    private static String getWhere(Uri uri, int match) {
        String where;
        switch (match) {
        case CONTACT_PHOTO_ID:
            long id = ContentUris.parseId(uri);
            where = " _id = " + id;
            break;
        default:
            where = null;
            break;
        }
        return where;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUrimatcher.match(uri);
        SQLiteDatabase db = getSQLiteDatabase(match);
        if (db != null) {
            String table = getTable(match);
            long id = db.insert(table, null, values);
            Uri newUri = uri.buildUpon().appendPath(String.valueOf(id)).build();
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        } else {
            return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = mUrimatcher.match(uri);
        SQLiteDatabase db = getSQLiteDatabase(match);
        if (db != null) {
            String table = getTable(match);
            String where = getWhere(uri, match);
            if (selection == null) {
                selection = where;
            } else if (where != null) {
                selection += " AND (" + where + ")";
            }
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(table);
            Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            if (c != null) {
                c.setNotificationUri(getContext().getContentResolver(), uri);
            }
            return c;
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = mUrimatcher.match(uri);
        SQLiteDatabase db = getSQLiteDatabase(match);
        if (db != null) {
            String table = getTable(match);
            String where = getWhere(uri, match);
            if (TextUtils.isEmpty(selection)) {
                selection = where;
            } else if (!TextUtils.isEmpty(where)) {
                selection += " AND (" + where + ")";
            }
            int count = db.delete(table, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            Log.w(getContext(), "DB is null.can't delete.");
            return 0;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUrimatcher.match(uri);
        SQLiteDatabase db = getSQLiteDatabase(match);
        if (db != null) {
            String table = getTable(match);
            String where = getWhere(uri, match);
            if (selection == null) {
                selection = where;
            } else if (where != null) {
                selection += " AND (" + where + ")";
            }
            int count = db.update(table, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        } else {
            Log.w(getContext(), "DB is null.can't update.");
            return 0;
        }
    }
}