package com.academy.login.pages;

import com.academy.login.config.ConfigReader;
import com.academy.login.utils.AllureSteps;
import com.academy.login.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CartPage {

    private static final By PRODUCT_ROWS = By.cssSelector("table tbody tr");
    private static final By QUANTITY_INPUT = By.cssSelector("table input[type='number']");
    private static final By GRAND_TOTAL = By.cssSelector("h3 strong");
    private static final By CHECKOUT_BUTTON = By.cssSelector("button.btn-success");

    private final WebDriver driver;
    private final WaitUtils wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        wait.visible(QUANTITY_INPUT);
        wait.visible(CHECKOUT_BUTTON);
    }

    public void scrollToCart() {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                wait.visible(QUANTITY_INPUT));
    }

    public List<CartLine> getLines() {
        List<CartLine> lines = new ArrayList<>();
        for (WebElement row : driver.findElements(PRODUCT_ROWS)) {
            if (row.findElements(By.cssSelector("h4")).isEmpty()) {
                continue;
            }
            String productName = row.findElement(By.cssSelector("h4")).getText().trim();
            int quantity = Integer.parseInt(row.findElement(By.cssSelector("input")).getAttribute("value").trim());
            List<Integer> amounts = row.findElements(By.cssSelector("strong")).stream()
                    .map(WebElement::getText)
                    .filter(text -> text.matches(".*\\d.*"))
                    .map(CartPage::parseAmount)
                    .toList();
            if (amounts.isEmpty()) {
                throw new IllegalStateException("Price was not shown for " + productName);
            }
            int unitPrice = amounts.get(0);
            int lineTotal = amounts.get(amounts.size() - 1);
            lines.add(new CartLine(productName, quantity, unitPrice, lineTotal));
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("No product rows were shown on the checkout page");
        }
        return lines;
    }

    public int sumOfQuantities() {
        return getLines().stream().mapToInt(CartLine::quantity).sum();
    }

    public int getGrandTotal() {
        return parseAmount(wait.visible(GRAND_TOTAL).getText());
    }

    public List<String> getButtonLabels() {
        return driver.findElements(By.cssSelector("button"))
                .stream()
                .filter(WebElement::isDisplayed)
                .map(button -> button.getText().replaceAll("\\s+", " ").trim())
                .filter(label -> !label.isEmpty())
                .toList();
    }

    public PurchasePage proceedToCheckout() {
        return AllureSteps.run(driver, "Click checkout", () -> {
            WebElement button = wait.clickable(CHECKOUT_BUTTON);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'}); arguments[0].click();",
                    button);
            return new PurchasePage(driver);
        });
    }

    static int parseAmount(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalStateException("Amount was not shown in: " + text);
        }
        return Integer.parseInt(digits);
    }
}
