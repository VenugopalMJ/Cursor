package com.academy.login.pages;

import com.academy.login.config.ConfigReader;
import com.academy.login.utils.AllureSteps;
import com.academy.login.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By ADMIN_RADIO = By.cssSelector("#login-form input[name='radio'][value='admin']");
    private static final By USER_RADIO = By.cssSelector("#login-form input[name='radio'][value='user']");
    private static final By ADMIN_LABEL = By.xpath(
            "//form[@id='login-form']//label[contains(@class,'customradio')][.//input[@value='admin']]");
    private static final By USER_LABEL = By.xpath(
            "//form[@id='login-form']//label[contains(@class,'customradio')][.//input[@value='user']]");
    private static final By ROLE_DROPDOWN = By.cssSelector("#login-form select.form-control");
    private static final By TERMS = By.id("terms");
    private static final By TERMS_LINK = By.cssSelector("#login-form .termsText a");
    private static final By SIGN_IN = By.id("signInBtn");
    private static final By ALERT = By.cssSelector("#login-form .alert-danger");
    private static final By USER_MODAL = By.id("myModal");
    private static final By USER_MODAL_MESSAGE = By.cssSelector("#myModal .modal-body p");
    private static final By USER_MODAL_BACKDROP = By.cssSelector(".modal-backdrop");
    private static final By OKAY_BUTTON = By.id("okayBtn");
    private static final By CANCEL_BUTTON = By.id("cancelBtn");
    private static final By CREDENTIAL_HINT = By.cssSelector("#login-form p.text-white");
    private static final By LOGIN_FORM = By.id("login-form");

    private final WebDriver driver;
    private final WaitUtils wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        wait.visible(USERNAME);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isAtLoginPage() {
        String url = getCurrentUrl();
        return url.contains("loginpagePractise") && !url.contains("/angularpractice/");
    }

    public boolean isLoginFormDisplayed() {
        return driver.findElement(LOGIN_FORM).isDisplayed() && driver.findElement(USERNAME).isDisplayed();
    }

    public boolean isUsernameDisplayed() {
        return wait.visible(USERNAME).isDisplayed();
    }

    public boolean isPasswordDisplayed() {
        return wait.visible(PASSWORD).isDisplayed();
    }

    public boolean isAdminOptionDisplayed() {
        return wait.visible(ADMIN_LABEL).isDisplayed();
    }

    public boolean isUserOptionDisplayed() {
        return wait.visible(USER_LABEL).isDisplayed();
    }

    public boolean isRoleDropdownDisplayed() {
        return wait.visible(ROLE_DROPDOWN).isDisplayed();
    }

    public boolean isTermsCheckboxDisplayed() {
        return wait.visible(TERMS).isDisplayed();
    }

    public boolean isTermsLinkDisplayed() {
        return wait.visible(TERMS_LINK).isDisplayed();
    }

    public boolean isSignInDisplayed() {
        return wait.visible(SIGN_IN).isDisplayed();
    }

    public void enterUsername(String username) {
        AllureSteps.run(driver, "Enter username: " + displayed(username), () -> {
            WebElement field = wait.clickable(USERNAME);
            field.clear();
            if (!username.isEmpty()) {
                field.sendKeys(username);
            }
        });
    }

    public String getUsernameValue() {
        return driver.findElement(USERNAME).getAttribute("value");
    }

    public void enterPassword(String password) {
        AllureSteps.run(driver, "Enter password", () -> {
            WebElement field = wait.clickable(PASSWORD);
            field.clear();
            if (!password.isEmpty()) {
                field.sendKeys(password);
            }
        });
    }

    public String getPasswordValue() {
        return driver.findElement(PASSWORD).getAttribute("value");
    }

    public boolean isPasswordMasked() {
        return "password".equals(driver.findElement(PASSWORD).getAttribute("type"));
    }

    public boolean isAdminSelected() {
        return driver.findElement(ADMIN_RADIO).isSelected();
    }

    public boolean isUserSelected() {
        return driver.findElement(USER_RADIO).isSelected();
    }

    public void selectAdmin() {
        AllureSteps.run(driver, "Select Admin", () -> wait.click(ADMIN_LABEL));
    }

    public void selectUser() {
        AllureSteps.run(driver, "Select User", () -> wait.click(USER_LABEL));
    }

    public List<String> getRoleLabels() {
        return roleSelect().getOptions().stream().map(WebElement::getText).toList();
    }

    public List<String> getRoleValues() {
        return roleSelect().getOptions().stream().map(option -> option.getAttribute("value")).toList();
    }

    public void selectRole(String visibleText) {
        AllureSteps.run(driver, "Select role: " + visibleText, () -> roleSelect().selectByVisibleText(visibleText));
    }

    public String getSelectedRole() {
        return roleSelect().getFirstSelectedOption().getText();
    }

    public String getSelectedRoleValue() {
        return roleSelect().getFirstSelectedOption().getAttribute("value");
    }

    public void setTermsAccepted(boolean accepted) {
        AllureSteps.run(driver, "Set terms and conditions to " + accepted, () -> {
            WebElement checkbox = wait.clickable(TERMS);
            if (checkbox.isSelected() != accepted) {
                checkbox.click();
            }
        });
    }

    public boolean isTermsAccepted() {
        return driver.findElement(TERMS).isSelected();
    }

    public String getTermsLinkText() {
        return wait.visible(TERMS_LINK).getText().replaceAll("\\s+", " ").trim();
    }

    public String getTermsLinkHref() {
        return driver.findElement(TERMS_LINK).getAttribute("href");
    }

    public void clickTermsLink() {
        AllureSteps.run(driver, "Open the terms and conditions link", () -> wait.click(TERMS_LINK));
    }

    public void clickSignIn() {
        AllureSteps.run(driver, "Click Sign In", () -> wait.click(SIGN_IN));
    }

    public ShopPage signIn(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
        return AllureSteps.run(driver, "Wait until the shopping page opens", () -> {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")))
                    .until(ExpectedConditions.urlContains("/angularpractice/shop"));
            return new ShopPage(driver);
        });
    }

    public String getSignInLabel() {
        return driver.findElement(SIGN_IN).getAttribute("value");
    }

    public String waitForAlertContaining(String fragment) {
        return AllureSteps.run(driver, "Wait for alert containing: " + fragment, () -> {
            wait.textContains(ALERT, fragment);
            return driver.findElement(ALERT).getText().replaceAll("\\s+", " ").trim();
        });
    }

    public String getUserModalMessage() {
        wait.visible(USER_MODAL_MESSAGE);
        return driver.findElement(USER_MODAL_MESSAGE).getText().trim();
    }

    public void acceptUserLimitation() {
        AllureSteps.run(driver, "Confirm the user limitation dialog", () -> dismissUserModal(OKAY_BUTTON));
    }

    public void cancelUserLimitation() {
        AllureSteps.run(driver, "Cancel the user limitation dialog", () -> dismissUserModal(CANCEL_BUTTON));
    }

    private void dismissUserModal(By button) {
        // This dialog ignores Okay and Cancel while its fade-in transition is still running.
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")))
                .pollingEvery(Duration.ofMillis(100))
                .until(webDriver -> Boolean.TRUE.equals(((JavascriptExecutor) webDriver).executeScript(
                        "var modal = document.getElementById('myModal');"
                                + "var instance = window.jQuery ? jQuery(modal).data('bs.modal') : null;"
                                + "return !!(modal && modal.classList.contains('show')"
                                + " && instance && instance._isTransitioning === false);")));
        wait.click(button);
        wait.invisible(USER_MODAL);
        wait.invisible(USER_MODAL_BACKDROP);
    }

    public String getCredentialHint() {
        return wait.visible(CREDENTIAL_HINT).getText().replaceAll("\\s+", " ").trim();
    }

    private Select roleSelect() {
        return new Select(wait.visible(ROLE_DROPDOWN));
    }

    private static String displayed(String value) {
        return value.isEmpty() ? "<blank>" : value;
    }
}
