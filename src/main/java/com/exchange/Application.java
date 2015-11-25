package com.exchange;

import com.exchange.service.ExchangeRateService;
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

    CacheManager cacheManager;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate((Runnable) () -> {
            Map<String, String> rates = new ExchangeRateService().updateCache("2015-11-25");
            for (Map.Entry entry : rates.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }, 0, 10, TimeUnit.SECONDS);

        ((ConfigurableApplicationContext)context).close();
    }


}
