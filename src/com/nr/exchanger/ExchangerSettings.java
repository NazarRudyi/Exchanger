package com.nr.exchanger;

import com.nr.exchanger.service.Sender;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

public class ExchangerSettings extends PreferenceActivity {
	
	public final static String WIDGET_PREF = "widget_pref";
	public static final String REFRESH_PERIOD_PREF = "RefreshPeriod";
	public static final String BASE_CURRENCY_PREF = "BaseCurrency";
	public static final String CURRENCY_PREF = "Currencies";
	
	int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	Intent resultValue;
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ¬ит€гуЇмо ID в≥джета, що конф≥гуруЇтьс€
				Intent intent = getIntent();
				Bundle extras = intent.getExtras();
				if (extras != null){
					widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				}
				
				// ≥ пров≥р€Їмо його коректн≥сть
				if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID){
					finish();
				}
				
				// формуЇмо ≥нтент в≥дпов≥д≥
				resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
				
				// при негативному результат≥
				setResult(RESULT_CANCELED, resultValue);
        
		getPreferenceManager().setSharedPreferencesName(WIDGET_PREF);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences);
		
		
	}
	
	public void onClick(View v){
		switch (v.getId()){
		case R.id.btn_ok:
			setResult(RESULT_OK, resultValue);
			
			Intent i = new Intent(getApplicationContext(), Sender.class);
			// potentially add data to the intent
			getApplicationContext().startService(i);
			
			break;
		}
		
		finish();
	}

	
	
}
