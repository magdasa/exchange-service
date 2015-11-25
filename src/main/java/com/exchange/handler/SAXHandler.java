package com.exchange.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Magda on 2015-11-24.
 */
public class SAXHandler extends DefaultHandler {

    private String currency;
    private String period;

    private static boolean seriesFound = false;

    private String result = "";

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getResult() {
        return result;
    }

    public void startElement(final String namespaceURI, final String sName,
                             final String qName, final Attributes attrs) {
        if (qName.equals("Cube") && attrs != null
                && period.equals(attrs.getValue("time"))) {
            seriesFound = true;
        } else if (qName.equals("Cube")) {
            seriesFound = false;
        } else if (qName.equals("Cube") && seriesFound
                && currency.equals(attrs.getValue("currency"))) {
            System.out.println("Exchange rate value for " + currency + " on "
                    + period + ": " + attrs.getValue("rate"));
            result = attrs.getValue("rate");
        }
    }
}
