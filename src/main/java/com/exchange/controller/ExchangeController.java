package com.exchange.controller;

import com.exchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
