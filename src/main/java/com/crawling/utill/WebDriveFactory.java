package com.crawling.utill;

import lombok.NoArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriveFactory {
    private static WebDriver webDriver;

    public static WebDriver getChromeDriver() {
        if (webDriver == null) {
            chromeDriverFactory();
        }
        return webDriver;
    }

    private static void chromeDriverFactory() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        webDriver = new ChromeDriver(chromeOptions);
    }
}
