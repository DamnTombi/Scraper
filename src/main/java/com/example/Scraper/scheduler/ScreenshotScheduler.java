package com.example.Scraper.scheduler;

import com.example.Scraper.service.FileArchiveService;
import com.example.Scraper.service.ScreenshotService;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
public class ScreenshotScheduler {
    private static final Logger log = LoggerFactory.getLogger(FileArchiveService.class);
    private final ObjectFactory<WebDriver> webDriverFactory;

    @PostConstruct
    public void testScreenshot() {
        log.info("Выполняю тестовый скриншот при старте");
        takeHourlyScreenshot();
    }

    @Value("${target.url}")
    private String targetUrl;

    @Value("${target.css.selector}")
    private String cssSelector;


    private final ScreenshotService screenshotService;

    @Autowired
    public ScreenshotScheduler(ScreenshotService screenshotService,
                               ObjectFactory<WebDriver> webDriverFactory) {
        this.screenshotService = screenshotService;
        this.webDriverFactory = webDriverFactory;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void takeHourlyScreenshot() {
        WebDriver localDriver = null;
        try {
            localDriver = webDriverFactory.getObject();
            screenshotService.captureElement(localDriver, targetUrl, cssSelector);
        }catch (Exception e){
            log.error("Screenshot capture failed",e);
        }finally {
            if (localDriver != null) {
                localDriver.quit();
            }
        }


    }
}
