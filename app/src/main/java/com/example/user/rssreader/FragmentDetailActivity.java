package com.example.user.rssreader;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

public class FragmentDetailActivity extends FragmentActivity {
  private String currentLink;
  public static final String EXTRA_SELECTED = "SelectedValue";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_fragment_detail);
    Bundle extras = getIntent().getExtras();
    currentLink = extras.getString(EXTRA_SELECTED);
    /*WebView viewer = (WebView) findViewById(R.id.wv_details);
    viewer.loadUrl(currentLink);*/
    FragmentDetail fragmentDetail = (FragmentDetail) getFragmentManager().findFragmentById(R.id.fragment_detail);
    fragmentDetail.goToLink(currentLink);

  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      Intent intent = new Intent(this, FragmentActivity.class);
      intent.putExtra(EXTRA_SELECTED, currentLink);
      startActivity(intent);
    }
  }

}
