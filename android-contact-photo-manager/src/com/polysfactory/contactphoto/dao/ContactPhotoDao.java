package com.polysfactory.contactphoto.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.polysfactory.contactphoto.provider.ContactPhotoStore.ContactPhoto;
import com.polysfactory.contactphoto.provider.ContactPhotoStore.ContactPhotoColumns;

/**
 * 連絡先関連のDAOクラス
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class ContactPhotoDao {

    public static final int INDEX_ID = 0;

    public static final int INDEX_CONTACT_ID = 1;

    public static final int INDEX_IMAGE_URI = 2;

    /** カラム一覧 */
    private static final String[] COLUMNS = {ContactPhotoColumns._ID, ContactPhotoColumns.CONTACT_ID, ContactPhotoColumns.IMAGE_URI };

    /** ContentResolverラッパー */
    private ContentResolver mContentResolver;

    /**
     * 新しいインスタンスを生成します。
     * @param context Context
     */
    public ContactPhotoDao(Context context) {
        this.mContentResolver = context.getContentResolver();
    }

    public Cursor findByContactId(long contactId) {
        Cursor cursor = mContentResolver.query(ContactPhoto.EXTERNAL_CONTENT_URI, COLUMNS, ContactPhotoColumns.CONTACT_ID + "=?", new String[] {String.valueOf(contactId) }, null);
        return cursor;
    }

    public Uri insertNewPhoto(long contactId, Uri imageUri) {
        ContentValues values = new ContentValues();
        values.put(ContactPhotoColumns.CONTACT_ID, contactId);
        values.put(ContactPhotoColumns.IMAGE_URI, imageUri.toString());
        Uri newUri = mContentResolver.insert(ContactPhoto.EXTERNAL_CONTENT_URI, values);
        return newUri;
    }

    public int deleteById(long id) {
        int deleted = mContentResolver.delete(ContentUris.withAppendedId(ContactPhoto.EXTERNAL_CONTENT_URI, id), null, null);
        return deleted;
    }
}
