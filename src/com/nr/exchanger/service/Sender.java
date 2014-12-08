package com.nr.exchanger.service;

import java.io.Serializable;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.nr.exchanger.ExchangeWidget;
import com.nr.exchanger.ExchangerSettings;
import com.nr.exchanger.provider.Pair;
import com.nr.exchanger.provider.YahooProvider;

public class Sender extends Service {
	
	//ExecutorService es;
	YahooProvider provider = new YahooProvider();
	Map<Pair<Currency,Currency>,Float> result;
	
	
	@Override
	public void onCreate() {
		
		Log.d("EXCHANGER", "service.onCreate");
		super.onCreate();
	}




	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("EXCHANGER", "service.onStartCommand");
		
		retreiveRates();
		
		return START_NOT_STICKY;
	}




	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	
	public class ProviderRun implements Runnable{
		

		@Override
		public void run() {
			Log.d("EXCHANGER", "getting Rates...");
			
			result = provider.getRates(getBaseCurrency(), getCurrencies());
			
			if (result.size() == 0) {
				Log.d("EXCHANGER", "no rates retrieved !!!");
				
				return;
			}
			
			try{
				sendRates(result);
			} catch (InterruptedException e) {
		        e.printStackTrace();
		    }
			
			
			stopSelf();
		
		}
		
	}
	
	private void retreiveRates() {
		Thread thread = new Thread(new ProviderRun());
		thread.start();
	}
	
	private synchronized void sendRates(Map<Pair<Currency, Currency>, Float> result) throws InterruptedException{
		Log.d("EXCHANGER", "sending rates");
		Intent i = new Intent(ExchangeWidget.BROADCAST_ACTION);
		i.putExtra("rates", (Serializable) result);
		sendBroadcast(i);
	}
	
	
	@SuppressWarnings("static-access")
	@Override
    public void onDestroy() {
		Log.d("EXCHANGER", "onDestroy");
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
            alarm.RTC_WAKEUP,
            System.currentTimeMillis() + getRefreshPeriod(),
            PendingIntent.getService(this, 0, new Intent(this, Sender.class), 0)
        );
    }
	
	private int getRefreshPeriod(){
		SharedPreferences prefs = getSharedPreferences(ExchangerSettings.WIDGET_PREF, MODE_PRIVATE);
		
		String period = prefs.getString(ExchangerSettings.REFRESH_PERIOD_PREF, "30");
		Log.d("EXCHANGER", "refresh period = " + period);
		
		
		return Integer.valueOf(period)*1000*60;
	}
	
	
	private Currency getBaseCurrency(){
		SharedPreferences prefs = getSharedPreferences(ExchangerSettings.WIDGET_PREF, MODE_PRIVATE);
		String base= prefs.getString(ExchangerSettings.BASE_CURRENCY_PREF, "UAH");
		Log.d("EXCHANGER", "base currency = " + base);
		return Currency.getInstance(base);
	}
	
	private Currency[] getCurrencies() {
		
		Set<String> defaultCurrencies = new HashSet<>();
//		defaul.add("USD"); 
//		defaul.add("EUR");
		
		SharedPreferences prefs = getSharedPreferences(ExchangerSettings.WIDGET_PREF, MODE_PRIVATE);
		Set<String> currencies = prefs.getStringSet(ExchangerSettings.CURRENCY_PREF, defaultCurrencies);
		
		Log.d("EXCHANGER", "currencies = " + currencies);
		Currency[] values=new Currency[currencies.size()];
		int i=0;
		for (String cur : currencies) {
			values[i++]=Currency.getInstance(cur);
		}
		
		return values;
	}

}
