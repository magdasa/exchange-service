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
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("exchangeRateService")
public class ExchangeRateService {

    @Autowired
    CacheManager cacheManager;

    public String getTodayExchangeRate(String currency) {
        return getFromCache("today_" + currency);
    }

    public String getExchangeRateOnDate(String date, String currency) {
        return getFromCache(date + "_" + currency);
    }


    private String getFromCache(String key) {
        Cache.ValueWrapper exchangeFromCache = cacheManager.getCache("exchangeRates").get(key);
        return exchangeFromCache != null ? (String) exchangeFromCache.get() : "exchange rate not available";
    }

    public void updateCache() {
        try {
            updateCache("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml",
                    (JSONObject jsonObject) -> ((JSONArray)((JSONObject) ((JSONObject) jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube")),
                    this::cacheExchangeRate);
            updateCache("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml",
                    (JSONObject jsonObject) -> ((JSONArray)((JSONObject)((JSONObject)((JSONObject)jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube")).get("Cube")),
                    this::cacheTodayRate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCache(String url, Function<JSONObject, JSONArray> getJSONObject, Consumer<JSONObject> caching) throws IOException {
        List<JSONObject> exchangeRates = JSONUtils.getJsonObjects(url, getJSONObject);
        exchangeRates.stream().forEach(caching);
    }

    private void cacheTodayRate(JSONObject e) {
        cacheManager.getCache("exchangeRates").put("today_" + e.get("currency"), e.get("rate").toString());
    }

    private void cacheExchangeRate(JSONObject e) {
        String time = e.get("time").toString();
        List<JSONObject> rateList = JSONUtils.jsonArrayToArray((JSONArray) e.get("Cube"));
        Map<String, String> rates  = rateList.stream().collect(Collectors.<JSONObject, String, String>toMap(
                e1 -> e1.get("currency").toString(),
                e1 -> e1.get("rate").toString()));
        rates.forEach((k,v) -> cacheManager.getCache("exchangeRates").put(time + "_" + k, v));
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
