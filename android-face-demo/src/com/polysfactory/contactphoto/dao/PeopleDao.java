package com.polysfactory.contactphoto.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * 連絡先関連のDAOクラス
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class PeopleDao {

    /** カーソルインデックス（ID） */
    public static final int ID = 0;

    /** カーソルインデックス（表示名） */
    public static final int DISPLAY_NAME = 1;

    /** カーソルインデックス（電話番号） */
    public static final int PHONETIC_NAME = 2;

    /** Contactsのカラム一覧 */
    private static final String[] COLUMNS = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };

    /** ContentResolverラッパー */
    private ContentResolver mContentResolver;

    /**
     * 新しいインスタンスを生成します。
     * @param context Context
     */
    public PeopleDao(Context context) {
        this.mContentResolver = context.getContentResolver();
    }

    public Cursor findAllCursor() {
        return mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, COLUMNS, ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1", null, ContactsContract.Contacts.DISPLAY_NAME);
    }

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = mContentResolver.query(photoUri, new String[] {Contacts.Photo.DATA15 }, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * ContactsのURIを取得します。
     * @return ContactsのURI
     */
    public static Uri getContentUri() {
        return ContactsContract.Contacts.CONTENT_URI;
    }

}
