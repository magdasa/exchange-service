package com.exchange;

import com.exchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.concurrent.*;

@SpringBootApplication
public class Application {

    @Autowired
    ExchangeRateService exchangeRateService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ExchangeRateService exchangeRateService = (ExchangeRateService) context.getBean("exchangeRateService");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate((Runnable) exchangeRateService::updateCache, 0, 10, TimeUnit.MINUTES);

    }


}
