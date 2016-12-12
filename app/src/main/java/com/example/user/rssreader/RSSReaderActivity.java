package com.example.user.rssreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RSSReaderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
  private ListView lvTitles;
  private TitlesListAdapter titlesListAdapter;
  private SwipeRefreshLayout refreshLayout;
  public static final String RSS_URL = "http://rss.cnn.com/rss/cnn_topstories.rss";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_rss_reader);
    lvTitles = (ListView) findViewById(R.id.lv_titles);
    setupCursorAdapter();
    lvTitles.setAdapter(titlesListAdapter);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.l_refresh);
    loadRssFromDb();
    if (savedInstanceState == null) {
      onRefresh();
    }
    refreshLayout.setOnRefreshListener(this);

  }

  private void loadRssFromDb() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RSSReaderDbHelper dbHelper = new RSSReaderDbHelper(RSSReaderActivity.this, RSSReaderDbHelper.DATABASE_NAME, null, RSSReaderDbHelper.DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RSSReaderContract.RSSEntry._ID,
                RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE
        };

        final Cursor c = db.query(
                RSSReaderContract.RSSEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            titlesListAdapter.swapCursor(c);
            refreshLayout.setRefreshing(false);
          }
        });
      }
    }).start();
  }

  private void setupCursorAdapter() {
    String[] uiBindFrom = {RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE};
    int[] uiBindTo = {R.id.title_view};
    titlesListAdapter = new TitlesListAdapter(
            this, R.layout.i_titles_list_item,
            null, uiBindFrom, uiBindTo,
            0);
  }


  @Override
  public void onRefresh() {
    refreshLayout.setRefreshing(true);
    new Thread(new Runnable() {
      @Override
      public void run() {
        // Initializing instance variables
        List<String> headlines = new ArrayList();
        List<String> links = new ArrayList();
        List<String> descriptions = new ArrayList<>();

        try {
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

        RSSReaderDbHelper dbHelper = new RSSReaderDbHelper(RSSReaderActivity.this, RSSReaderDbHelper.DATABASE_NAME, null, RSSReaderDbHelper.DATABASE_VERSION);
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
        loadRssFromDb();
      }
    }).start();
  }
}
