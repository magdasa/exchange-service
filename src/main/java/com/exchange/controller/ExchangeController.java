package com.exchange.controller;

import com.exchange.handler.SAXHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Magda on 2015-11-23.
 */

@Controller
public class ExchangeController {

    @ResponseBody
    @RequestMapping("/api/today/CHF")
    public String getTodayExchangeRate() throws IOException, SAXException, ParserConfigurationException {
        final SAXHandler handler = new SAXHandler();
        handler.setCurrency("CHF");
        handler.setPeriod("2015-11-23");
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            final SAXParser parser = factory.newSAXParser();
            parser.parse(new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml").openConnection().getInputStream(), handler);
        } catch (final ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return handler.getResult();
    }

    @ResponseBody
    @RequestMapping("/api/2015-11-10/CHF")
    public String getExchangeRateOnDate() {
        return "0.8";
    }
}
