package com.academy.login.listeners;

import com.academy.login.config.ConfigReader;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AllureReportListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        try {
            Path results = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
            Files.createDirectories(results);
            Files.writeString(results.resolve("environment.properties"), environment());
            try (InputStream categories = getClass().getClassLoader().getResourceAsStream("categories.json")) {
                if (categories != null) {
                    Files.copy(categories, results.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to prepare Allure report files: " + exception.getMessage());
        }
    }

    private static String environment() {
        return "Browser=" + ConfigReader.get("browser") + System.lineSeparator()
                + "Headless=" + ConfigReader.get("headless") + System.lineSeparator()
                + "Base.URL=" + ConfigReader.get("base.url") + System.lineSeparator()
                + "Java=" + System.getProperty("java.version") + System.lineSeparator()
                + "OS=" + System.getProperty("os.name") + " " + System.getProperty("os.version") + System.lineSeparator();
    }
}
