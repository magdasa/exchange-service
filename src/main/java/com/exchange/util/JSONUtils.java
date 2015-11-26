package com.exchange.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JSONUtils {

    public static final String EUROFXREF_HIST_90D_XML = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String EUROFXREF_DAILY_XML = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public static final String ELEMENT = "Cube";
    public static final String PRIMARY_ELEMENT = "gesmes:Envelope";

    public static List<JSONObject> getJsonObjects(String url, Function<String,JSONArray> getObject) throws IOException {
        String xmlText = IOUtils.toString(new URL(url).openConnection().getInputStream());
        JSONArray jsonArray = getObject.apply(xmlText);
        return jsonArrayToArray(jsonArray);
    }

    public static List<JSONObject> jsonArrayToArray(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            jsonObjects.add(((JSONObject)jsonObject));
        }
        return jsonObjects;
    }

    public static Function<String, JSONArray> allExchangeRatesJSONArrayFunction() {
        return (String urlText) -> ((JSONArray)((JSONObject)((JSONObject)((JSONObject)XML.toJSONObject(urlText)
                .get(PRIMARY_ELEMENT)).get(ELEMENT)).get(ELEMENT)).get(ELEMENT));
    }

    public static Function<String, JSONArray> todayRateJSONArrayFunction() {
        return (String urlText) -> ((JSONArray)((JSONObject) ((JSONObject)XML.toJSONObject(urlText)
                .get(PRIMARY_ELEMENT)).get(ELEMENT)).get(ELEMENT));
    }

}
