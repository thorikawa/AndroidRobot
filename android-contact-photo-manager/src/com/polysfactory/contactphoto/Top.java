package com.polysfactory.contactphoto;

import com.polysfactory.contactphoto.adapter.PeopleAdapter;
import com.polysfactory.contactphoto.dao.PeopleDao;
import com.polysfactory.contactphoto.util.Log;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Top extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.main);
        PeopleDao peopleDao = new PeopleDao(this);
        Cursor cursor = peopleDao.findAllCursor();
        if (cursor != null) {
            startManagingCursor(cursor);
            ListAdapter adapter = new PeopleAdapter(this, cursor);
            setListAdapter(adapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(this, "click:" + id);
        Uri uri = ContentUris.withAppendedId(PeopleDao.getContentUri(), id);
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

}