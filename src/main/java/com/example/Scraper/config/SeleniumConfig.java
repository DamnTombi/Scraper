package com.example.Scraper.config;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@EnableScheduling
@Configuration
public class SeleniumConfig {

    @Bean( destroyMethod = "quit")
    @Scope(SCOPE_PROTOTYPE)
    public WebDriver driver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        return new ChromeDriver(options);
    }

}
