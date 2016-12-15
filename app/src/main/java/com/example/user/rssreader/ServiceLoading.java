package com.example.user.rssreader;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ServiceLoading extends IntentService {

  public static final String RSS_URL = "http://rss.cnn.com/rss/cnn_topstories.rss";
  private boolean running = false;

  public ServiceLoading(String name) {
    super(name);
    Log.d("CREATGE", "start");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d("onRefresh", "start0");
    if (!running) {
      running = true;
      Log.d("onRefresh", "start2");
      // Initializing instance variables
      List<String> headlines = new ArrayList();
      List<String> links = new ArrayList();
      List<String> descriptions = new ArrayList<>();

      try {
        Log.d("onRefresh", "start");
        URL url = new URL(RSS_URL);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
        boolean insideItem = false;
        int eventType = xpp.getEventType();
        Log.d("URL", "connected");
        while (eventType != XmlPullParser.END_DOCUMENT) {
          if (eventType == XmlPullParser.START_TAG) {

            if (xpp.getName().equalsIgnoreCase("item")) {
              insideItem = true;
            } else if (xpp.getName().equalsIgnoreCase("title")) {
              if (insideItem)
                headlines.add(xpp.nextText()); //extract the headline
            } else if (xpp.getName().equalsIgnoreCase("link")) {
              if (insideItem)
                links.add(xpp.nextText()); //extract the link of article
            } else if (xpp.getName().equalsIgnoreCase("description")) {
              if (insideItem)
                descriptions.add(xpp.nextText()); //extract the headline
            }
          } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
            insideItem = false;
          }
          eventType = xpp.next(); //move to next element
        }
      } catch (XmlPullParserException | IOException e) {
        e.printStackTrace();
      }
      Log.d("onRefresh", "middle");
      RSSReaderDbHelper dbHelper = new RSSReaderDbHelper(null, RSSReaderDbHelper.DATABASE_NAME, null, RSSReaderDbHelper.DATABASE_VERSION);
      SQLiteDatabase db = dbHelper.getWritableDatabase();
      db.execSQL(RSSReaderDbHelper.SQL_DELETE_ENTRIES);
      db.execSQL(RSSReaderDbHelper.SQL_CREATE_ENTRIES);
      int countNews = headlines.size();
      for (int i = 0; i < countNews; i++) {
        ContentValues values = new ContentValues();
        values.put(RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE, headlines.get(i));
        values.put(RSSReaderContract.RSSEntry.COLUMN_NAME_LINK, links.get(i));
        values.put(RSSReaderContract.RSSEntry.COLUMN_NAME_DESCRIPTION, descriptions.get(i));
        db.insert(RSSReaderContract.RSSEntry.TABLE_NAME, null, values);
      }
      // db.close();
      Log.d("onRefresh", "finish");
      Intent intentFinished = new Intent(FragmentList.ACTION_LOAD);
      sendBroadcast(intentFinished);
      running = false;

    }
  }
}
