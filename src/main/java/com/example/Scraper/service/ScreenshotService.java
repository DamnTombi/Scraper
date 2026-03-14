package com.example.Scraper.service;



import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScreenshotService {
    @Value("${screenshot.folder}")
    private String screenshotFolder;

    public void captureElement(WebDriver driver, String url, String cssSelector) {
        File folder = new File(screenshotFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));

            WebElement element = driver.findElement(By.cssSelector(cssSelector));
            File screenshot = element.getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));
            String fileName = "flight_list_" + timestamp + ".png";
            File destination = new File(screenshotFolder, fileName);

            FileUtils.copyFile(screenshot, destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }
}
