package com.academy.login.pages;

import com.academy.login.config.ConfigReader;
import com.academy.login.utils.AllureSteps;
import com.academy.login.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PurchasePage {

    private static final By COUNTRY = By.id("country");
    private static final By TERMS = By.id("checkbox2");
    private static final By PURCHASE = By.cssSelector("input.btn-success[value='Purchase']");
    private static final By SUCCESS = By.cssSelector(".alert-success");

    private final WebDriver driver;
    private final WaitUtils wait;

    public PurchasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        wait.visible(COUNTRY);
        wait.visible(PURCHASE);
    }

    public void enterDeliveryLocation(String location) {
        AllureSteps.run(driver, "Enter delivery location " + location, () -> {
            WebElement input = wait.clickable(COUNTRY);
            ((JavascriptExecutor) driver).executeScript(
                    """
                    const field = arguments[0];
                    const location = arguments[1];
                    field.focus();
                    field.value = location;
                    field.dispatchEvent(new Event('input', {bubbles: true}));
                    field.dispatchEvent(new Event('keyup', {bubbles: true}));
                    """,
                    input, location);
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(webDriver -> location.equals(input.getAttribute("value")));
        });
    }

    public String getDeliveryLocation() {
        return wait.visible(COUNTRY).getAttribute("value");
    }

    public void acceptTerms() {
        AllureSteps.run(driver, "Select the terms and conditions", () -> {
            WebElement checkbox = driver.findElement(TERMS);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", checkbox);
            if (!checkbox.isSelected()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(webDriver -> webDriver.findElement(TERMS).isSelected());
        });
    }

    public boolean isTermsAccepted() {
        return driver.findElement(TERMS).isSelected();
    }

    public void purchase() {
        AllureSteps.run(driver, "Click Purchase", () -> {
            WebElement button = wait.clickable(PURCHASE);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'}); arguments[0].click();",
                    button);
        });
    }

    public String getSuccessMessage() {
        WebElement alert = wait.visible(SUCCESS);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", alert);
        return alert.getText().replaceAll("\\s+", " ").trim();
    }
}
