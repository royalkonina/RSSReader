package com.example.user.rssreader;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSReaderDbHelper  extends SQLiteOpenHelper{
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "RSSReader.db";
  public static final String TEXT_TYPE = " TEXT";
  public static final String COMMA_SEP = ",";
  public static final String SQL_CREATE_ENTRIES =
          "CREATE TABLE " + RSSReaderContract.RSSEntry.TABLE_NAME + " (" +
                  RSSReaderContract.RSSEntry._ID + " INTEGER PRIMARY KEY," +
                  RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                  RSSReaderContract.RSSEntry.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                  RSSReaderContract.RSSEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + " )";

  public static final String SQL_DELETE_ENTRIES =
          "DROP TABLE IF EXISTS " + RSSReaderContract.RSSEntry.TABLE_NAME;

  public RSSReaderDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    onCreate(sqLiteDatabase);
  }
}
