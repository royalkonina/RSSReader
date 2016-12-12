package com.example.user.rssreader;


import android.provider.BaseColumns;

public final class RSSReaderContract {


  private RSSReaderContract() {
  }

  public static class RSSEntry implements BaseColumns {
    public static final String TABLE_NAME = "RSS_Records";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_LINK = "link";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
  }


}
