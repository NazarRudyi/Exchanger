package com.nr.exchanger.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.commons.io.IOUtils;


import android.util.Log;

public class YahooProvider implements Provider {
	
	private static final String URL = "http://download.finance.yahoo.com/d/quotes.csv?s=";

	private static final String TAG = "EXCHANGER";
	
	//http://finance.yahoo.com/d/quotes.csv?s=USDUAH=X,EURUAH=X&f=sl1d1t1	
	//http://download.finance.yahoo.com/d/quotes.csv?s=EURUAH=X&f=l1
	
	
	private HttpClient httpClient = new DefaultHttpClient();
	
	
	@SuppressWarnings("resource")
	@Override
	public Map<Pair<Currency,Currency>,Float> getRates(Currency baseCurr, Currency[] currencies) {
		String result = "", params = "";
		Map<Pair<Currency,Currency>,Float> map = new HashMap<>();
		
		if (currencies.length == 0) {
			return map; 
		}
		
		for (int i = 0; i < currencies.length; i++) {
			params += currencies[i].getCurrencyCode() + baseCurr.getCurrencyCode() + "=X,";
		}
		
		String url = URL + params + "&f=sl1d1t1";
		String[] rows, valueString;
		String first, second;
		float rate;
		
		Log.d(TAG, "url=" + url);
		
		HttpGet httpget = new HttpGet(url);

	    try {
	    	
			HttpResponse response = httpClient.execute(httpget);		
			HttpEntity entity = response.getEntity();
		    InputStream resultStream = entity.getContent();
		    
		    result = new Scanner(resultStream,"UTF-8").useDelimiter("\\A").next();
		    rows = result.split("\n");
		    for (int i = 0; i < rows.length; i++) {
				second= rows[i].substring(1, 4);
				first= rows[i].substring(4, 7);
				valueString = rows[i].split(",");
				rate = Float.valueOf(valueString[1].toString());
				map.put(new Pair<Currency,Currency>(Currency.getInstance(first),Currency.getInstance(second)),rate);
			}
		    
		    
		} catch (IOException e) {			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
		return map;
	}

}
