package com.nr.exchanger;

import java.util.Currency;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.nr.exchanger.provider.Pair;
import com.nr.exchanger.provider.YahooProvider;

@SuppressLint("HandlerLeak")
public class ListCurrency extends Activity {
	
	protected static final String TAG = "EXCHANGER";
	protected static final String SELECTED_CURRENCY = "selectedCurrency";
	
	private FrameLayout vProgresBar;
	
	private Currency[] currencies;
	private Currency baseCurrency;
	private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;	

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String[] values = (String[])msg.obj;
			
			
			ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item, R.id.tvName, values);
			ListView list_Currency = (ListView) findViewById(R.id.list_Currency);
			list_Currency.setAdapter(adapter);		
			
			vProgresBar.setVisibility(View.GONE);
		}
	};
	
	
	
	YahooProvider provider = new YahooProvider();
	Map<Pair<Currency,Currency>,Float> result;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_currency);
		
		final ListView list_Currency = (ListView) findViewById(R.id.list_Currency);
		list_Currency.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),ChartActivity.class);
				
				String selected = (String)list_Currency.getAdapter().getItem(position);
				Log.d(TAG, "selected value = " + selected);
				
				intent.putExtra(SELECTED_CURRENCY, selected.substring(0, 3));
				startActivity(intent);
				overridePendingTransition(R.anim.pullinright, R.anim.pushoutleft);
			}
		});
		
		list_Currency.setLayoutAnimation(createAnimation());
		
		
		widgetID = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		showRates();
		
	}




	private LayoutAnimationController createAnimation() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		
		return new LayoutAnimationController(set, 0.3f);
	}




	private void showRates() {
		
		String[] currencyCodes = getResources().getStringArray(R.array.Currencies);
		currencies = new Currency[currencyCodes.length];
		
		SharedPreferences sp = getSharedPreferences(ExchangerSettings.WIDGET_PREF, MODE_PRIVATE);
		baseCurrency=Currency.getInstance(sp.getString(ExchangerSettings.BASE_CURRENCY_PREF, "UAH"));
		
		for(int i=0; i<currencyCodes.length; i++){
			currencies[i] = Currency.getInstance(currencyCodes[i]);
		}

		vProgresBar = (FrameLayout)findViewById(R.id.progresContainer);
		vProgresBar.setVisibility(View.VISIBLE);

		retriveRates();
	}
	
	
	

	private void retriveRates() {
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String[] values = getList(currencies, baseCurrency);
				
				Log.d(TAG, "ALL values = " + values);
				
				Message msg = new Message();				
				msg.obj = values;
				handler.sendMessage(msg);
				
			}
		});
		thread.start();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		showRates();
	}




	private String[] getList(Currency[] currencies, Currency baseCurrency) {
		
		Log.d("EXCHANGER", "ListCurrency.getting All Rates...");		
		result = provider.getRates(baseCurrency, currencies);
		String[] value = new String[result.size()];
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (Pair<Currency,Currency> pair : result.keySet()) {
			sb.append(pair.getSecond().getCurrencyCode()+"/"+pair.getFirst().getCurrencyCode()+" = "+result.get(pair));
			value[i++]=sb.toString();
			sb.delete(0, 50);
		}
		
		return value;
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting, menu);
		return super.onCreateOptionsMenu(menu);
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()){
		case R.id.setting:
			Intent i = new Intent(this, ExchangerSettings.class);
			
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
			//i.setData(Uri.withAppendedPath(Uri.parse("abc" + "://widget/id/"), String.valueOf(widgetID)));
			
			this.startActivity(i);
			
			Log.d(ExchangeWidget.TAG, "start EchangeSetting Activity...");
			return true;		
		default:
			return super.onOptionsItemSelected(item);			
		}				
	}
	
	
	

}
