package com.exchange.service;

public interface ExchangeRateService {

    /**
     * method to retrieve current exchange rates, depending on the time, it is either today's or yesterday's date
     * @param currency to retrieve the exchange rate for
     * @return exchange rate
     */
    String getTodayExchangeRate(String currency);

    /**
     * method to retrieve exchange rate on a given date
     * @param date of the exchange rate, less than 90 days in the past
     * @param currency
     * @return exchange rate
     */
    String getExchangeRateOnDate(String date, String currency);

    /**
     * method to periodically update the cache of all currencies and dates
     */
    void updateCache();
}
