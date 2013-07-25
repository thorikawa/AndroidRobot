package com.polysfactory.contactphoto.adapter;

import com.polysfactory.contactphoto.dao.PeopleDao;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PeopleAdapter extends CursorAdapter {

    /** Inflater */
    LayoutInflater mInflater;

    /**
     * コンストラクタ<br>
     * @param context Context
     * @param c Cursor
     */
    public PeopleAdapter(Context context, Cursor c) {
        super(context, c);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.text1);
        textView.setText(cursor.getString(PeopleDao.DISPLAY_NAME));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.simple_list_item_1, null);
    }
}
