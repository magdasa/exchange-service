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

    public static List<JSONObject> getJsonObjects(String url, Function<JSONObject,JSONArray> getObject) throws IOException {
        String text = IOUtils.toString(new URL(url).openConnection().getInputStream());
        JSONObject jsonObject = XML.toJSONObject(text);
        JSONArray jsonArray = getObject.apply(jsonObject);
        return jsonArrayToArray(jsonArray);
    }

    public static List<JSONObject> jsonArrayToArray(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            jsonObjects.add(((JSONObject)jsonObject));
        }
        return jsonObjects;
    }
}
