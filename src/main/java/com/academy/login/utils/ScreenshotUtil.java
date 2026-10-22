package com.academy.login.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ScreenshotUtil {

    private ScreenshotUtil() {
    }

    public static byte[] capture(WebDriver driver) {
        if (!(driver instanceof TakesScreenshot screenshotTaker)) {
            return new byte[0];
        }
        try {
            return screenshotTaker.getScreenshotAs(OutputType.BYTES);
        } catch (RuntimeException exception) {
            System.out.println("Unable to capture screenshot: " + exception.getMessage());
            return new byte[0];
        }
    }

    public static void attachToAllure(WebDriver driver, String stepName) {
        byte[] png = capture(driver);
        if (png.length == 0) {
            return;
        }
        Allure.attachment(
                stepName,
                "image/png",
                new ByteArrayInputStream(png),
                AttachmentOptions.withFileExtension(".png"));
    }

    public static void save(byte[] png, String testName) {
        if (png.length == 0) {
            return;
        }
        try {
            Path directory = Path.of("target", "screenshots");
            Files.createDirectories(directory);
            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path file = directory.resolve(safeName + "_" + System.currentTimeMillis() + ".png");
            Files.write(file, png);
            System.out.println("Saved failure screenshot to " + file.toAbsolutePath());
        } catch (IOException exception) {
            System.out.println("Unable to save failure screenshot: " + exception.getMessage());
        }
    }
}
