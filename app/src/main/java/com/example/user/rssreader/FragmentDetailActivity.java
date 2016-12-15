package com.example.user.rssreader;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

public class FragmentDetailActivity  extends FragmentActivity{
  String currentLink;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.a_fragment_detail);
    Bundle extras = getIntent().getExtras();
    currentLink = extras.getString("selectedValue");
    WebView viewer = (WebView) findViewById(R.id.wv_details);
    viewer.loadUrl(currentLink);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      Intent intent = new Intent(this, FragmentActivity.class);
      intent.putExtra("selectedValue", currentLink);
      startActivity(intent);
    }
  }

}
