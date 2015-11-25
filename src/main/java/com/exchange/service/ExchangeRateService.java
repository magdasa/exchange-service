package com.exchange.service;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
public class ExchangeRateService {

    CacheManager cacheManager;

    public String getTodayExchangeRate(String currency) {
        List<JSONObject> rateList;
        try {
            String text = IOUtils.toString(new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml").openConnection().getInputStream());
            JSONObject jsonObject = XML.toJSONObject(text);
            JSONArray jsonArray = ((JSONArray)((JSONObject)((JSONObject)((JSONObject)jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube")).get("Cube"));
            List<JSONObject> list= jsonArrayToArray(jsonArray);
            return list.stream().filter(el -> el.get("currency").equals(currency)).findAny().get().get("rate").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getExchangeRateOnDate(String date, String currency) {
        List<JSONObject> rateList;
        try {
            String text = IOUtils.toString(new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml").openConnection().getInputStream());
            JSONObject jsonObject = XML.toJSONObject(text);
            JSONArray jsonArray = ((JSONArray)((JSONObject)((JSONObject)jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube"));
            List<JSONObject> list= jsonArrayToArray(jsonArray);
            Optional<JSONObject> optionalRatesAtDate = list.stream().filter(el -> el.get("time").equals(date)).findAny();
            JSONObject ratesAtDate = optionalRatesAtDate.get();
            rateList = jsonArrayToArray((JSONArray)ratesAtDate.get("Cube"));
            return rateList.stream().filter(el -> el.get("currency").equals(currency)).findAny().get().get("rate").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    public Map<String, String> updateCache(String time) {
        List<JSONObject> rateList;
        Map<String, String> rates = null;//(Map<String, String>) cacheManager.getCache("currencyRates").get(time);
        try {
            String text = IOUtils.toString(new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml").openConnection().getInputStream());
            JSONObject jsonObject = XML.toJSONObject(text);
            JSONArray jsonArray = ((JSONArray) ((JSONObject) ((JSONObject) jsonObject.get("gesmes:Envelope")).get("Cube")).get("Cube"));
            List<JSONObject> list = jsonArrayToArray(jsonArray);
            Optional<JSONObject> optionalRatesAtDate = list.stream().filter(el -> el.get("time").equals(time)).findAny();
            list.stream().forEach(this::cache);
            JSONObject ratesAtDate = optionalRatesAtDate.get();
            rateList = jsonArrayToArray((JSONArray) ratesAtDate.get("Cube"));
            rates = rateList.stream().collect(Collectors.<JSONObject, String, String>toMap(
                    e1 -> e1.get("currency").toString(),
                    e1 -> e1.get("rate").toString()));
            for (Map.Entry entry : rates.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        cacheManager.getCache("currencyRates").put(time, rates);
        return rates;
    }

    @Cacheable(value="exchangeRates", key="#time")
    private void cache(JSONObject e) {
        String time = e.get("time").toString();
        List<JSONObject> rateList = jsonArrayToArray((JSONArray) e.get("Cube"));
        Map<String, String> rates  = rateList.stream().collect(Collectors.<JSONObject, String, String>toMap(
                e1 -> e1.get("currency").toString(),
                e1 -> e1.get("rate").toString()));
    }

    private List<JSONObject> jsonArrayToArray(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            jsonObjects.add(((JSONObject)jsonObject));
        }
        return jsonObjects;
    }
}
