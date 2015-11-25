package com.exchange.controller;

import com.exchange.service.ExchangeRateService;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Controller
public class ExchangeController {

    @Autowired
    ExchangeRateService exchangeRateService;

    @ResponseBody
    @RequestMapping(value = "/api/today/{currency}")
    public String getTodayExchangeRate(@PathVariable String currency) {
        return exchangeRateService.getTodayExchangeRate(currency);
    }

    @ResponseBody
    @RequestMapping("/api/{date}/{currency}")
    public String getExchangeRateOnDate(@PathVariable String date, @PathVariable String currency) {
        return exchangeRateService.getExchangeRateOnDate(date, currency);
    }

    public void setExchangeRateService(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }
}
