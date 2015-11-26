package com.exchange.service;

import com.exchange.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("exchangeRateService")
public class ExchangeRateService {

    public static final String CACHE_NAME = "exchangeRates";
    public static final String CURRENCY = "currency";
    public static final String RATE = "rate";
    public static final String TIME = "time";

    @Autowired
    CacheManager cacheManager;

    public String getTodayExchangeRate(String currency) {
        return getFromCache("today_" + currency);
    }

    public String getExchangeRateOnDate(String date, String currency) {
        return getFromCache(date + "_" + currency);
    }

    private String getFromCache(String key) {
        Cache.ValueWrapper exchangeFromCache = getCache().get(key);
        return exchangeFromCache != null ? (String) exchangeFromCache.get() : "exchange rate not available";
    }

    private Cache getCache() {
        return cacheManager.getCache(CACHE_NAME);
    }

    public void updateCache() {
        try {
            updateCache(JSONUtils.EUROFXREF_HIST_90D_XML, JSONUtils.todayRateJSONArray(), this::cacheExchangeRate);
            updateCache(JSONUtils.EUROFXREF_DAILY_XML, JSONUtils.allExchangeRatesJSONArray(), this::cacheTodayRate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCache(String url, Function<JSONObject, JSONArray> getJSONObject, Consumer<JSONObject> caching) throws IOException {
        List<JSONObject> exchangeRates = JSONUtils.getJsonObjects(url, getJSONObject);
        exchangeRates.stream().forEach(caching);
    }

    private void cacheTodayRate(JSONObject e) {
        getCache().put("today_" + e.get(CURRENCY), e.get(RATE).toString());
    }

    private void cacheExchangeRate(JSONObject e) {
        String time = e.get(TIME).toString();
        List<JSONObject> rateList = JSONUtils.jsonArrayToArray((JSONArray) e.get("Cube"));
        Map<String, String> rates  = rateList.stream().collect(Collectors.<JSONObject, String, String>toMap(
                e1 -> e1.get(CURRENCY).toString(),
                e1 -> e1.get(RATE).toString()));
        rates.forEach((k,v) -> getCache().put(time + "_" + k, v));
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
