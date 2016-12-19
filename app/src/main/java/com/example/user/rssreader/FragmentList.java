package com.example.user.rssreader;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class FragmentList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
  private ListView lvTitles;
  private TitlesListAdapter titlesListAdapter;
  private SwipeRefreshLayout refreshLayout;
  private boolean isFirstLaunch = true;
  public static String ACTION_LOAD = "loading";
  private BroadcastReceiver receiver;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.f_titles, container);
    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupCursorAdapter();
    IntentFilter filter = new IntentFilter(ACTION_LOAD);
    receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        Log.d("receiver", "1");
        loadRssFromDb();
      }
    };
    getActivity().registerReceiver(receiver, filter);

    loadRssFromDb();
    if (savedInstanceState == null) {
      onRefresh();
    }
    isFirstLaunch = false;

  }

  @Override
  public void onDestroy() {
    getActivity().unregisterReceiver(receiver);
    super.onDestroy();
  }

  private void setupCursorAdapter() {
    String[] uiBindFrom = {RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE};
    int[] uiBindTo = {R.id.title_view};
    titlesListAdapter = new TitlesListAdapter(
            getActivity(), R.layout.i_titles_list_item,
            null, uiBindFrom, uiBindTo,
            0);
    titlesListAdapter.setOnClickLinkListener(new OnClickLinkListener() {
      @Override
      public void onClickLink(String link) {
        FragmentDetail fragmentDetail = (FragmentDetail) getFragmentManager().findFragmentById(R.id.fragment_detail);
        if (fragmentDetail != null && fragmentDetail.isInLayout()) {
          fragmentDetail.goToLink(link);
        } else {
          Intent intent = new Intent(getActivity(), FragmentDetailActivity.class);
          intent.putExtra(FragmentDetailActivity.EXTRA_SELECTED, link);
          getActivity().startActivity(intent);
        }
      }
    });
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    lvTitles = (ListView) view.findViewById(R.id.lv_titles);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.l_refresh);
    lvTitles.setAdapter(titlesListAdapter);
    refreshLayout.setOnRefreshListener(this);
  }

  private void loadRssFromDb() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d("load", "start");
        RSSReaderDbHelper dbHelper = new RSSReaderDbHelper(getActivity(), RSSReaderDbHelper.DATABASE_NAME, null, RSSReaderDbHelper.DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RSSReaderContract.RSSEntry._ID,
                RSSReaderContract.RSSEntry.COLUMN_NAME_TITLE,
                RSSReaderContract.RSSEntry.COLUMN_NAME_LINK
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

        Activity activity = getActivity();
        Log.d("load", "finish");
        if (activity != null) {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              titlesListAdapter.swapCursor(c);
              refreshLayout.setRefreshing(false);
            }
          });
        }
        // db.close();
      }
    }).start();
  }

  @Override
  public void onRefresh() {
    if (!isFirstLaunch) {
      refreshLayout.setRefreshing(true);
    }
    Intent intent = new Intent(getActivity(), ServiceLoading.class);
    intent.setAction(ACTION_LOAD);
    getActivity().startService(intent);
  }


}
