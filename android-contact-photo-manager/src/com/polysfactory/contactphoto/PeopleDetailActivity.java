package com.polysfactory.contactphoto;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.polysfactory.contactphoto.adapter.GalleryAdapter;
import com.polysfactory.contactphoto.dao.ContactPhotoDao;
import com.polysfactory.contactphoto.dao.PeopleDao;
import com.polysfactory.contactphoto.entity.People;
import com.polysfactory.contactphoto.util.Log;

/**
 * 個別詳細画面
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class PeopleDetailActivity extends Activity {

    protected static final int ACTIVITY_RESULT_SELECT_IMAGE = 100;

    ContactPhotoDao mContactPhotoDao;

    long contactId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.people_detail);
        TextView title = (TextView) findViewById(R.id.Title);
        TextView subTitle = (TextView) findViewById(R.id.SubTitle);
        Gallery gallery = (Gallery) findViewById(R.id.Gallery);
        Button addButton = (Button) findViewById(R.id.AddButton);
        mContactPhotoDao = new ContactPhotoDao(this);
        Uri data = getIntent().getData();

        if (data != null) {
            contactId = ContentUris.parseId(data);
            PeopleDao peopleDao = new PeopleDao(this);
            People people = peopleDao.findByUri(data);
            Cursor imageCursor = mContactPhotoDao.findByContactId(contactId);
            startManagingCursor(imageCursor);
            title.setText(people.displayName);
            subTitle.setText(people.phoneticName);
            SpinnerAdapter galleryAdapter = new GalleryAdapter(this, imageCursor);
            gallery.setAdapter(galleryAdapter);
            registerForContextMenu(gallery);
            addButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, ACTIVITY_RESULT_SELECT_IMAGE);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (ACTIVITY_RESULT_SELECT_IMAGE == resultCode) {
        if (data != null) {
            Uri uri = data.getData();
            Log.d(this, "select image:" + uri);
            if (uri != null) {
                Uri insertedUri = mContactPhotoDao.insertNewPhoto(contactId, uri);
                Log.d(this, "inserted:" + insertedUri);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add("削除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 選択されたヤツをテキストビューに設定する
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = info.id;
        int deleted = mContactPhotoDao.deleteById(id);
        Log.v(this, "select:" + id + " " + deleted + " deleted");
        return super.onContextItemSelected(item);
    }

}
