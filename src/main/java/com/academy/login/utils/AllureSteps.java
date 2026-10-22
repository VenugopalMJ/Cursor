package com.academy.login.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public final class AllureSteps {

    private AllureSteps() {
    }

    public static void run(WebDriver driver, String name, Runnable action) {
        Allure.step(name, () -> {
            try {
                action.run();
            } finally {
                ScreenshotUtil.attachToAllure(driver, name);
            }
        });
    }

    public static <T> T run(WebDriver driver, String name, Supplier<T> action) {
        return Allure.step(name, () -> {
            try {
                return action.get();
            } finally {
                ScreenshotUtil.attachToAllure(driver, name);
            }
        });
    }
}
