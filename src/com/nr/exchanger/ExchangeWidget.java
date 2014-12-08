package com.nr.exchanger;

import java.util.Currency;
import java.util.Map;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import com.nr.exchanger.provider.Pair;
import com.nr.exchanger.service.Sender;

public class ExchangeWidget extends AppWidgetProvider {
	
	public final static String BROADCAST_ACTION = "com.nr.exchanger.ExchangerWidget";
	public final static String TAG = "EXCHANGER";
	
	private BroadcastReceiver brReciver;
	private Map<Pair<Currency,Currency>,Float> rates;
//	private String currency="UAH";
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Log.d(TAG, "ExchangerWidget.onUpdate");
		

		for (int i : appWidgetIds) {
			updateWidget(context, appWidgetManager, i);
		}
	}	
	
	
	
	
	 private static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID) {
		 
		RemoteViews remoteV = new RemoteViews(context.getPackageName(),R.layout.widget_main);
		 
		Intent intentAct = new Intent(context, ListCurrency.class);
//		intentAct.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
		intentAct.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		PendingIntent pIntent = PendingIntent.getActivity(context, widgetID, intentAct, 0);
		remoteV.setOnClickPendingIntent(R.id.all_widget, pIntent);
		
		appWidgetManager.updateAppWidget(widgetID, remoteV);
	}




	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		
		Log.d(TAG, "ExchangerWidget.onEnabled");
		forUpdating(context);
		
	}

	 private void forUpdating(Context context){
		 
		 
		 Intent i = new Intent(context, Sender.class);
		 // potentially add data to the intent
		 context.startService(i);
		 
		 brReciver = new BroadcastReceiver() {
			 
			 @SuppressWarnings("unchecked")
			 @Override
			 public void onReceive(Context context, Intent intent) {
//				 StringBuilder sb = new StringBuilder();				 
				 
				 RemoteViews remoteV=new RemoteViews(context.getPackageName(),R.layout.widget_main);
				 
				 Log.d(TAG, "... onReceive");
				 rates = (Map<Pair<Currency,Currency>,Float>)intent.getSerializableExtra("rates");
				 String res;
				 int i = 0;				 
				 for (Pair<Currency,Currency> pair : rates.keySet()) {
					 switch (i) {
						 case 0: {
							 res = pair.getSecond().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_pin1, res);
							 res = rates.get(pair).toString();
							 remoteV.setTextViewText(R.id.tv_rate1, res);
							 res = pair.getFirst().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_pin2, res);
							 
							 break;
						 }
						 case 1: {
							 res = pair.getSecond().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_blu1, res);
							 res = rates.get(pair).toString();
							 remoteV.setTextViewText(R.id.tv_rate2, res);
							 res = pair.getFirst().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_blu2, res);
							 
							 break;
						 }
						 case 2: {
							 res = pair.getSecond().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_gre1, res);
							 res = rates.get(pair).toString();
							 remoteV.setTextViewText(R.id.tv_rate3, res);
							 res = pair.getFirst().getCurrencyCode();
							 remoteV.setTextViewText(R.id.tv_cur_gre2, res);
							 
							 break;
						 }
					 
					 }
					 
				 
//					sb.append(pair.getSecond().getCurrencyCode()+"/"+pair.getFirst().getCurrencyCode()+"="+rates.get(pair) + "\n");
					
					i++; 
				 }
				 
				 
				 Log.d(TAG, "rates=" + rates);
				 
				 AppWidgetManager appWidManager = AppWidgetManager.getInstance(context);
				 
				 ComponentName thisWidget = new ComponentName(context, ExchangeWidget.class);
				 
				 appWidManager.updateAppWidget(thisWidget, remoteV);
				 
				 Log.d(TAG, "update finished");
				 
				 
			 }
		 };
		 
		 
		 IntentFilter intFiltr = new IntentFilter(BROADCAST_ACTION);
		 context.getApplicationContext().registerReceiver(brReciver, intFiltr);
	 } 
		 
		 
//	 private class TimeForUpdate extends TimerTask {		
//		 Context context;
//		 
//		 public TimeForUpdate(Context context) {
//			 this.context = context;			 
//			 
//		 }
//		 
//		 @Override
//		 public void run() {
//			 forUpdating(context);
//		 }
//		 
//	 } 
//	 
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
    }

	@Override
	  public void onDeleted(Context context, int[] appWidgetIds) {
	    super.onDeleted(context, appWidgetIds);
	  }
	

	  @Override
	  public void onDisabled(Context context) {
	    super.onDisabled(context);
	    
	  }
	  
	  
	  


}
