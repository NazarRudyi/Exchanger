package com.nr.exchanger.provider;

import java.util.Currency;
import java.util.Map;

public interface Provider {

	public Map<Pair<Currency,Currency>,Float> getRates(Currency baseCurr, Currency[] currencies);
}
