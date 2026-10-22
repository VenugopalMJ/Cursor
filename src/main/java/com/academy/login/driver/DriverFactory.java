package com.academy.login.driver;

import com.academy.login.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        try {
            WebDriver webDriver = switch (browser) {
                case "chrome" -> new ChromeDriver(chromeOptions());
                case "edge" -> new EdgeDriver(edgeOptions());
                case "firefox" -> new FirefoxDriver(firefoxOptions());
                default -> throw new IllegalArgumentException(
                        "Unsupported browser '" + browser + "'. Use chrome, edge, or firefox.");
            };
            webDriver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(ConfigReader.getInt("page.load.timeout.seconds")));
            if (!ConfigReader.getBoolean("headless")) {
                webDriver.manage().window().maximize();
            }
            DRIVER.set(webDriver);
            return webDriver;
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Unable to start " + browser + ". Confirm the browser is installed, or pass -Dbrowser=chrome|edge|firefox.",
                    exception);
        }
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver webDriver = DRIVER.get();
        if (webDriver != null) {
            webDriver.quit();
            DRIVER.remove();
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false));
        applyHeadless(options);
        return options;
    }

    private static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-notifications");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false));
        applyHeadless(options);
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    private static void applyHeadless(ChromeOptions options) {
        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
    }

    private static void applyHeadless(EdgeOptions options) {
        if (ConfigReader.getBoolean("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
    }
}
