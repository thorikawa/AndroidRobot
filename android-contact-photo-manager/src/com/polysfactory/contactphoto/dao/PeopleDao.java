package com.polysfactory.contactphoto.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.polysfactory.contactphoto.entity.People;

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

    /** カーソルインデックス（ファミリ名） */
    private static final int PHONETIC_FAMILY_NAME = 0;

    /** カーソルインデックス（ミドル名） */
    private static final int PHONETIC_MIDDLE_NAME = 1;

    /** カーソルインデックス（洗礼名） */
    private static final int PHONETIC_GIVEN_NAME = 2;

    /** カーソルインデックス（ID） */
    public static final int ID = 0;

    /** カーソルインデックス（表示名） */
    public static final int DISPLAY_NAME = 1;

    /** カーソルインデックス（電話番号） */
    public static final int PHONETIC_NAME = 2;

    /** Contactsのカラム一覧 */
    private static final String[] COLUMNS = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };

    /** StructuredNameのカラム一覧 */
    private static final String[] STRUCTURED_NAME_COLUMNS = {ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,
            ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME, ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME };

    /** ContentResolverラッパー */
    private ContentResolver mContentResolver;

    /** Context */
    private Context mContext;

    /**
     * 新しいインスタンスを生成します。
     * @param context Context
     */
    public PeopleDao(Context context) {
        this.mContentResolver = context.getContentResolver();
        this.mContext = context;
    }

    public Cursor findAllCursor() {
        return mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, COLUMNS, ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1", null, ContactsContract.Contacts.DISPLAY_NAME);
    }

    public People findByUri(Uri peopleUri) {
        Cursor peopleCursor = mContentResolver.query(peopleUri, COLUMNS, null, null, null);
        People people = null;
        if (peopleCursor != null) {
            try {
                if (peopleCursor.moveToFirst()) {
                    people = extractCurrentEntityFromCursor(peopleCursor);
                    Cursor dataCursor = mContentResolver.query(ContactsContract.Data.CONTENT_URI, STRUCTURED_NAME_COLUMNS, ContactsContract.Data.MIMETYPE + "=? AND "
                            + ContactsContract.Data.CONTACT_ID + "=?", new String[] {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, peopleCursor.getString(ID) }, null);
                    if (dataCursor != null) {
                        try {
                            if (dataCursor.moveToFirst()) {
                                String familyName = dataCursor.getString(PHONETIC_FAMILY_NAME);
                                if (familyName == null) {
                                    familyName = "";
                                }
                                String middleName = dataCursor.getString(PHONETIC_MIDDLE_NAME);
                                if (middleName == null) {
                                    middleName = "";
                                }
                                String givenName = dataCursor.getString(PHONETIC_GIVEN_NAME);
                                if (givenName == null) {
                                    givenName = "";
                                }
                                people.phoneticName = familyName + " " + middleName + " " + givenName;
                            }
                        } finally {
                            dataCursor.close();
                        }
                    }
                }
            } finally {
                peopleCursor.close();
            }
        }
        return people;
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
     * カーソルからPeopleエンティティに変換します。
     * @param c カーソル
     * @return Peopleエンティティ
     */
    private static People extractCurrentEntityFromCursor(Cursor c) {
        People people = new People();
        people.id = c.getString(ID);
        people.displayName = c.getString(DISPLAY_NAME);
        return people;
    }

    /**
     * ContactsのURIを取得します。
     * @return ContactsのURI
     */
    public static Uri getContentUri() {
        return ContactsContract.Contacts.CONTENT_URI;
    }

}
