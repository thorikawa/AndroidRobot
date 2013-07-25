package com.polysfactory.contactphoto.adapter;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.polysfactory.contactphoto.dao.ContactPhotoDao;
import com.polysfactory.contactphoto.util.Log;

public class GalleryAdapter extends CursorAdapter {

    Context mContext;

    public GalleryAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view;

        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        String uri = cursor.getString(ContactPhotoDao.INDEX_IMAGE_URI);
        try {
            is = context.getContentResolver().openInputStream(Uri.parse(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is != null) {
            // BitmapFactory.decodeStream(is, null, options);
            // int scaleW = options.outWidth / 240;
            // int scaleH = options.outHeight / 240;
            // int scale = Math.max(scaleW, scaleH);
            int scale = 10;
            Log.v(mContext, "load image:" + uri + ":" + scale);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(is, null, options);

            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new Gallery.LayoutParams(240, 240));
        // imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }
}