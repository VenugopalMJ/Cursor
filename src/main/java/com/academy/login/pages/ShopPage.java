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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopPage {

    private static final By SHOP_HEADING = By.xpath("//h1[normalize-space()='Shop Name']");
    private static final By PRIMARY_MENU_LINKS = By.cssSelector("nav.navbar-expand-sm a.nav-link");
    private static final By HOME_LINK = By.cssSelector("a.nav-link[href='/angularpractice']");
    private static final By SHOP_LINK = By.cssSelector("a.nav-link[href='/angularpractice/shop']");
    private static final By CHECKOUT_LINK = By.xpath("//a[contains(normalize-space(.),'Checkout')]");
    private static final Pattern CHECKOUT_COUNT = Pattern.compile("\\(\\s*(\\d+)\\s*\\)");

    private final WebDriver driver;
    private final WaitUtils wait;

    public ShopPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        wait.visible(SHOP_HEADING);
        wait.visible(CHECKOUT_LINK);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isShopHeadingDisplayed() {
        return wait.visible(SHOP_HEADING).isDisplayed();
    }

    public List<String> getPrimaryMenuOptions() {
        wait.visible(HOME_LINK);
        return driver.findElements(PRIMARY_MENU_LINKS)
                .stream()
                .map(link -> link.getText().trim())
                .filter(text -> !text.isEmpty())
                .toList();
    }

    public boolean isHomeDisplayed() {
        return wait.visible(HOME_LINK).isDisplayed();
    }

    public boolean isShopDisplayed() {
        return wait.visible(SHOP_LINK).isDisplayed();
    }

    public boolean isCheckoutDisplayed() {
        return wait.visible(CHECKOUT_LINK).isDisplayed();
    }

    public String getCheckoutLabel() {
        return wait.visible(CHECKOUT_LINK).getText().replaceAll("\\s+", " ").trim();
    }

    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        wait.visible(CHECKOUT_LINK);
    }

    public int getCheckoutCount() {
        Matcher matcher = CHECKOUT_COUNT.matcher(getCheckoutLabel());
        if (!matcher.find()) {
            throw new IllegalStateException("Checkout count was not shown in: " + getCheckoutLabel());
        }
        return Integer.parseInt(matcher.group(1));
    }

    public CartPage openCheckout() {
        return AllureSteps.run(driver, "Click checkout", () -> {
            scrollToTop();
            WebElement link = wait.clickable(CHECKOUT_LINK);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
            return new CartPage(driver);
        });
    }

    public void addProductToCart(String productName) {
        AllureSteps.run(driver, "Add " + productName + " to the cart", () -> {
            int countBefore = getCheckoutCount();
            WebElement button = wait.clickable(addButton(productName));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'}); arguments[0].click();",
                    button);
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(webDriver -> getCheckoutCount() == countBefore + 1);
        });
    }

    private static By addButton(String productName) {
        String lowered = productName.toLowerCase();
        return By.xpath(
                "//app-card[.//h4[contains(translate(normalize-space(.), "
                        + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                        + lowered
                        + "')]]//button[contains(normalize-space(.), 'Add')]");
    }
}
