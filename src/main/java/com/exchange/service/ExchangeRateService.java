package com.exchange.service;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("exchangeRateService")
public class ExchangeRateService {

    @Autowired
    CacheManager cacheManager;

    public String getTodayExchangeRate(String currency) {
        try {
            List<JSONObject> list = getJsonObjects("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml",
                    (JSONObject jsonObject) -> ((JSONArray)((JSONObject)((JSONObject)((JSONObject)jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube")).get("Cube")));
            return list.stream().filter(el -> el.get("currency").equals(currency)).findAny().get().get("rate").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "today exchange rate not available";
    }

    private List<JSONObject> getJsonObjects(String url, Function<JSONObject,JSONArray> getObject) throws IOException {
        String text = IOUtils.toString(new URL(url).openConnection().getInputStream());
        JSONObject jsonObject = XML.toJSONObject(text);
        JSONArray jsonArray = getObject.apply(jsonObject);
        return jsonArrayToArray(jsonArray);
    }

    public String getExchangeRateOnDate(String date, String currency) {
        Cache.ValueWrapper exchangeFromCache = cacheManager.getCache("exchangeRates").get(date + "_" + currency);
        return exchangeFromCache != null ? (String) exchangeFromCache.get() : "exchange rate not available";
    }

    public void updateCache() {
        try {
            List<JSONObject> list = getJsonObjects("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml",
                    (JSONObject jsonObject) -> ((JSONArray)((JSONObject) ((JSONObject) jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube")));
            list.stream().forEach(this::cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cache(JSONObject e) {
        String time = e.get("time").toString();
        List<JSONObject> rateList = jsonArrayToArray((JSONArray) e.get("Cube"));
        Map<String, String> rates  = rateList.stream().collect(Collectors.<JSONObject, String, String>toMap(
                e1 -> e1.get("currency").toString(),
                e1 -> e1.get("rate").toString()));
        rates.forEach((k,v) -> cacheManager.getCache("exchangeRates").put(time + "_" + k, v));
    }

    private List<JSONObject> jsonArrayToArray(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            jsonObjects.add(((JSONObject)jsonObject));
        }
        return jsonObjects;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
