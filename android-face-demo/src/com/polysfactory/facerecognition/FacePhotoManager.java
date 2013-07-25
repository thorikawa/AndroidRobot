package com.polysfactory.facerecognition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.polysfactory.contactphoto.dao.PeopleDao;

public class FacePhotoManager {
    Context mContext;

    private Map<Long, String> nameMap;

    /**
     * コンストラクタ<br>
     * @param context Context
     */
    public FacePhotoManager(Context context) {
        nameMap = new HashMap<Long, String>();
        // 初期値登録
        String[] names = {"不明", "秋田", "ホソミ", "イガリ", "伊藤", "中澤", "古賀", "ナミカワ", "ハン", "佐藤", "ポリー", "山田", "ショウコ" };
        for (int i = 0; i < names.length; i++) {
            nameMap.put((long) i, names[i]);
        }
        mContext = context;
    }

    /**
     * 指定されたIDの人の名前を取得する<br>
     * @param id
     * @return
     */
    public String getName(long id) {
        return nameMap.get(id);
    }

    /**
     * 連絡帳データから画像ファイルを吐き出し、インデックスファイルを更新する<br>
     */
    public void update() {
        PeopleDao peopleDao = new PeopleDao(mContext);
        Cursor cursor = peopleDao.findAllCursor();
        if (cursor == null) {
            return;
        }
        FileOutputStream indexFileOutputStream = null;
        try {
            indexFileOutputStream = new FileOutputStream(new File("/sdcard/photo/train.txt"));
        } catch (FileNotFoundException e) {
            Log.e(App.TAG, "index file open error", e);
            return;
        }
        nameMap.clear();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(PeopleDao.ID);
            String displayName = cursor.getString(PeopleDao.DISPLAY_NAME);
            Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
            InputStream is = Contacts.openContactPhotoInputStream(mContext.getContentResolver(), contactUri);
            if (is == null) {
                Log.w(App.TAG, "no available");
                continue;
            }
            BufferedInputStream bis = new BufferedInputStream(is);
            String fileName = "/sdcard/photo/" + id + ".jpg";
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(fileName));
                bos = new BufferedOutputStream(fos);
            } catch (FileNotFoundException e) {
                Log.e(App.TAG, "read error", e);
                continue;
            }
            byte[] buffer = new byte[4096];
            int n;
            try {
                while ((n = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, n);
                }
                bos.close();
                fos.close();
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e(App.TAG, "write error", e);
                continue;
            }
            try {
                indexFileOutputStream.write((id + " " + fileName + "\n").getBytes());
            } catch (IOException e) {
                Log.e(App.TAG, "index write error", e);
            }
            nameMap.put(id, displayName);
            Log.d(App.TAG, "file output end:contact id:" + id);
        }
        try {
            indexFileOutputStream.close();
        } catch (IOException e) {
            Log.d(App.TAG, "file close error", e);
        }
        cursor.close();
    }
}
