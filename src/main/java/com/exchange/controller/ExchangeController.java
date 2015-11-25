package com.exchange.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ExchangeController {

    @ResponseBody
    @RequestMapping("/api/today/CHF")
    public String getTodayExchangeRate() throws IOException, SAXException, ParserConfigurationException {
        List<JSONObject> rateList;
        try {
            String text = IOUtils.toString(new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml").openConnection().getInputStream());
            JSONObject jsonObject = XML.toJSONObject(text);
            JSONArray jsonArray = ((JSONArray)((JSONObject)((JSONObject)XML.toJSONObject(text).get("gesmes:Envelope")).get("Cube")).get("Cube"));
            List<JSONObject> list= jsonArrayToArray(jsonArray);
            Optional<JSONObject> optionalRatesAtDate = list.stream().filter(el -> el.get("time").equals("2015-11-24")).findAny();
            JSONObject ratesAtDate = optionalRatesAtDate.get();
            rateList = jsonArrayToArray((JSONArray)ratesAtDate.get("Cube"));
            return rateList.stream().filter(el -> el.get("currency").equals("CHF")).findAny().get().get("rate").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @ResponseBody
    @RequestMapping("/api/2015-11-10/CHF")
    public String getExchangeRateOnDate() {
        return "0.8";
    }

    private List<JSONObject> jsonArrayToArray(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            jsonObjects.add(((JSONObject)jsonObject));
        }
        return jsonObjects;
    }
}
