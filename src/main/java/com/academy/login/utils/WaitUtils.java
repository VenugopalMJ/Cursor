package com.academy.login.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        this.wait.pollingEvery(Duration.ofMillis(200));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    public WebElement visible(By locator) {
        return wait.withMessage("Element not visible: " + locator)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement clickable(By locator) {
        return wait.withMessage("Element not clickable: " + locator)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void click(By locator) {
        WebElement element = clickable(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
        clickable(locator).click();
    }

    public void textContains(By locator, String text) {
        wait.withMessage("Text '" + text + "' not found in " + locator)
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public void invisible(By locator) {
        wait.withMessage("Element still visible: " + locator)
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
