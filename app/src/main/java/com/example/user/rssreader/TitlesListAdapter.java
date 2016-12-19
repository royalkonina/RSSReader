package com.example.user.rssreader;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class TitlesListAdapter extends SimpleCursorAdapter {
  private OnClickLinkListener onClickLinkListener;

  public void setOnClickLinkListener(OnClickLinkListener onClickLinkListener) {
    this.onClickLinkListener = onClickLinkListener;
  }

  public TitlesListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
    super(context, layout, c, from, to, flags);
  }

  @Override
  public void bindView(View view, Context context, final Cursor cursor) {
    super.bindView(view, context, cursor);
    view.setTag(cursor.getString(cursor.getColumnIndex(RSSReaderContract.RSSEntry.COLUMN_NAME_LINK)));
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onClickLinkListener.onClickLink((String) view.getTag());

      }
    });
  }


}
