package com.nr.exchanger;

import java.util.Currency;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ChartActivity extends Activity {
	
	private static final String TAG = "EXCHANGER";
//  http://chart.finance.yahoo.com/z?s=USDUAH=x&t=5m&z=l

	WebView charWebView;
	char period ='m';
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		String url = builtUrl(period);
		
		setContentView(R.layout.chart);

		charWebView = (WebView) findViewById(R.id.chartview);
		
		charWebView.setInitialScale(1);
		charWebView.getSettings().setLoadWithOverviewMode(true);
		charWebView.getSettings().setUseWideViewPort(true);
		charWebView.getSettings().setSupportZoom(true);
		charWebView.getSettings().setBuiltInZoomControls(true);
		charWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		charWebView.setScrollbarFadingEnabled(true);
		charWebView.getSettings().setLoadsImagesAutomatically(true);
		
		charWebView.setWebViewClient(new WebViewClient());
		
		charWebView.loadUrl(url);	
	}

	private String builtUrl(char period){
		StringBuilder sb = new StringBuilder();
		SharedPreferences sp = getSharedPreferences(ExchangerSettings.WIDGET_PREF, MODE_PRIVATE);
		Currency baseCurrency=Currency.getInstance(sp.getString(ExchangerSettings.BASE_CURRENCY_PREF, "UAH"));	
		
		
		Intent i = getIntent();
		String selected = i.getStringExtra(ListCurrency.SELECTED_CURRENCY);
		
		Log.d(TAG, "selected = " + selected+" period = "+period);	
		
		sb.append("http://chart.finance.yahoo.com/z?s=" + selected + baseCurrency.getCurrencyCode()+"=x&t=5"+period+"&z=l");
		
		return sb.toString();
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
		switch (item.getItemId()) {	
		  case R.id.item_w:
			  period = 'w';
			  break;
		  case R.id.item_m:
			  period = 'm';
			  break;
		  case R.id.item_y:
			  period = 'y';
			  break;
	      case android.R.id.home:
	    	  finish();
	    	  overridePendingTransition(R.anim.pullinleft, R.anim.pushoutright);
	    	  return true;
	    }
		
		String url = builtUrl(period);
		charWebView.loadUrl(url);
		
		return super.onOptionsItemSelected(item);
	}

		
	
	

}
