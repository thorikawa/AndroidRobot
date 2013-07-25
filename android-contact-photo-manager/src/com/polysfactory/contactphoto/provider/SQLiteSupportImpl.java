package com.polysfactory.contactphoto.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.polysfactory.contactphoto.provider.ContactPhotoStore.ContactPhotoColumns;
import com.polysfactory.contactphoto.util.Log;

/**
 * 独自のコンテンツプロバイダーのSQLite周りのヘルパークラス
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class SQLiteSupportImpl extends SQLiteOpenHelper {

    private Context mContext;

    /** データベース名 */
    private static final String DATABASE_NAME = "contactphoto.db";

    /** バージョン */
    private static final int DATABASE_VERSION = 1;

    /** ダウンロードテーブル作成SQL */
    private static final String CREATE_CONTACT_PHOTO = "CREATE TABLE ContactPhoto (" + ContactPhotoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ContactPhotoColumns.CONTACT_ID
            + " long not null," + ContactPhotoColumns.IMAGE_URI + " text not null," + ContactPhotoColumns.FACE_OFFSET_X + " integer," + ContactPhotoColumns.FACE_OFFSET_Y + " integer,"
            + ContactPhotoColumns.FACE_WIDTH + " integer," + ContactPhotoColumns.FACE_HEIGHT + " integer);";

    /**
     * コンストラクタ<br>
     * @param ctx Context
     */
    public SQLiteSupportImpl(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(mContext, "onCreate");
        db.execSQL(CREATE_CONTACT_PHOTO);
        db.setVersion(DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new UnsupportedOperationException();
    }
}
