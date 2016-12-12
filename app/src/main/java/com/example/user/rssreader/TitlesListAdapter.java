package com.example.user.rssreader;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class TitlesListAdapter extends SimpleCursorAdapter {

  public TitlesListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
    super(context, layout, c, from, to, flags);
  }

  @Override
  public void bindView(View view, Context context, final Cursor cursor) {
    super.bindView(view, context, cursor);
    view.setTag(cursor.getString(cursor.getColumnIndex(RSSReaderContract.RSSEntry._ID)));
  }


}
