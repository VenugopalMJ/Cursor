package com.academy.login.tests;

import com.academy.login.base.BaseTest;
import com.academy.login.config.ConfigReader;
import com.academy.login.pages.LoginPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Param;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.model.Parameter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;

/**
 * Covers every control on the practice login page.
 * Valid username and password are never submitted together, because that leaves this page.
 */
@Epic("Practice login page")
@Feature("Login form")
@Link(name = "Login page", url = "https://rahulshettyacademy.com/loginpagePractise/")
public class LoginPageTest extends BaseTest {

    @Test(description = "Login page opens at the practice URL with the expected title")
    @Story("Page load")
    @Severity(CRITICAL)
    public void verifyLoginPageIdentity() {
        LoginPage loginPage = loginPage();

        Assert.assertEquals(loginPage.getTitle(), "LoginPage Practise | Rahul Shetty Academy");
        Assert.assertTrue(loginPage.getCurrentUrl().startsWith(ConfigReader.get("base.url")));
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Username, password, user type, role, terms, and Sign In are all present")
    @Story("Field presence")
    @Severity(CRITICAL)
    public void verifyAllLoginFieldsArePresent() {
        LoginPage loginPage = loginPage();
        SoftAssert softly = new SoftAssert();

        softly.assertTrue(loginPage.isUsernameDisplayed(), "Username field");
        softly.assertTrue(loginPage.isPasswordDisplayed(), "Password field");
        softly.assertTrue(loginPage.isPasswordMasked(), "Password field is masked");
        softly.assertTrue(loginPage.isAdminOptionDisplayed(), "Admin option");
        softly.assertTrue(loginPage.isUserOptionDisplayed(), "User option");
        softly.assertTrue(loginPage.isAdminSelected(), "Admin is the default user type");
        softly.assertFalse(loginPage.isUserSelected(), "User is not selected by default");
        softly.assertTrue(loginPage.isRoleDropdownDisplayed(), "Role dropdown");
        softly.assertEquals(loginPage.getRoleLabels(), List.of("Student", "Teacher", "Consultant"));
        softly.assertEquals(loginPage.getRoleValues(), List.of("stud", "teach", "consult"));
        softly.assertEquals(loginPage.getSelectedRole(), "Student");
        softly.assertTrue(loginPage.isTermsCheckboxDisplayed(), "Terms checkbox");
        softly.assertFalse(loginPage.isTermsAccepted(), "Terms checkbox starts unchecked");
        softly.assertTrue(loginPage.isTermsLinkDisplayed(), "Terms link");
        softly.assertEquals(loginPage.getTermsLinkText(), "terms and conditions");
        softly.assertTrue(loginPage.isSignInDisplayed(), "Sign In button");
        softly.assertEquals(loginPage.getSignInLabel(), "Sign In");
        softly.assertTrue(loginPage.getCredentialHint().contains(ConfigReader.get("username")));
        softly.assertTrue(loginPage.getCredentialHint().contains(ConfigReader.get("password")));
        softly.assertAll();
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Username accepts the configured value and can be cleared")
    @Story("Username")
    @Severity(NORMAL)
    public void verifyUsernameField() {
        LoginPage loginPage = loginPage();
        String username = ConfigReader.get("username");

        loginPage.enterUsername(username);
        Assert.assertEquals(loginPage.getUsernameValue(), username);

        loginPage.enterUsername("");
        Assert.assertEquals(loginPage.getUsernameValue(), "");
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Password stays masked and accepts the configured value")
    @Story("Password")
    @Severity(NORMAL)
    public void verifyPasswordField() {
        LoginPage loginPage = loginPage();
        String password = ConfigReader.get("password");

        Assert.assertTrue(loginPage.isPasswordMasked());
        loginPage.enterPassword(password);
        Assert.assertEquals(loginPage.getPasswordValue(), password);

        loginPage.enterPassword("");
        Assert.assertEquals(loginPage.getPasswordValue(), "");
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Choosing User opens the limitation dialog, and Cancel returns to Admin")
    @Story("User type")
    @Severity(NORMAL)
    public void verifyUserOptionCancelRestoresAdmin() {
        LoginPage loginPage = loginPage();

        loginPage.selectUser();
        Assert.assertEquals(
                loginPage.getUserModalMessage(),
                "You will be limited to only fewer functionalities of the app. Proceed?");

        loginPage.cancelUserLimitation();
        Assert.assertTrue(loginPage.isAdminSelected());
        Assert.assertFalse(loginPage.isUserSelected());
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Confirming the limitation dialog keeps User selected")
    @Story("User type")
    @Severity(NORMAL)
    public void verifyUserOptionOkayKeepsUserSelected() {
        LoginPage loginPage = loginPage();

        loginPage.selectUser();
        loginPage.acceptUserLimitation();

        Assert.assertTrue(loginPage.isUserSelected());
        Assert.assertFalse(loginPage.isAdminSelected());
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Admin can be selected again after User was confirmed")
    @Story("User type")
    @Severity(NORMAL)
    public void verifyAdminCanBeSelectedAfterUser() {
        LoginPage loginPage = loginPage();

        loginPage.selectUser();
        loginPage.acceptUserLimitation();
        loginPage.selectAdmin();

        Assert.assertTrue(loginPage.isAdminSelected());
        Assert.assertFalse(loginPage.isUserSelected());
        assertRemainsOnLoginPage(loginPage);
    }

    @DataProvider(name = "roles")
    public Object[][] roles() {
        return new Object[][] {
                {"Student", "stud"},
                {"Teacher", "teach"},
                {"Consultant", "consult"}
        };
    }

    @Test(dataProvider = "roles", description = "Each role in the dropdown can be selected")
    @Story("Role")
    @Severity(NORMAL)
    public void verifyRoleDropdown(@Param("role") String label, @Param("value") String value) {
        LoginPage loginPage = loginPage();

        loginPage.selectRole(label);

        Assert.assertEquals(loginPage.getSelectedRole(), label);
        Assert.assertEquals(loginPage.getSelectedRoleValue(), value);
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Terms checkbox can be checked and unchecked")
    @Story("Terms")
    @Severity(NORMAL)
    public void verifyTermsCheckbox() {
        LoginPage loginPage = loginPage();

        loginPage.setTermsAccepted(true);
        Assert.assertTrue(loginPage.isTermsAccepted());

        loginPage.setTermsAccepted(false);
        Assert.assertFalse(loginPage.isTermsAccepted());
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Terms link keeps the browser on the login page")
    @Story("Terms")
    @Severity(NORMAL)
    public void verifyTermsLinkStaysOnLoginPage() {
        LoginPage loginPage = loginPage();

        Assert.assertTrue(loginPage.getTermsLinkHref().endsWith("#"));
        loginPage.clickTermsLink();

        assertRemainsOnLoginPage(loginPage);
        Assert.assertEquals(loginPage.getTitle(), "LoginPage Practise | Rahul Shetty Academy");
    }

    @DataProvider(name = "rejectedCredentials")
    public Object[][] rejectedCredentials() {
        String username = ConfigReader.get("username");
        String password = ConfigReader.get("password");
        return new Object[][] {
                {"", "", "Empty username/password."},
                {username, "", "Empty username/password."},
                {"", password, "Empty username/password."},
                {"invalidUser", "invalidPass", "Incorrect username/password."},
                {username, "invalidPass", "Incorrect username/password."},
                {"invalidUser", password, "Incorrect username/password."},
                {username, "learning", "Old password \"learning\" is no longer valid."}
        };
    }

    @Test(dataProvider = "rejectedCredentials",
            description = "Sign In rejects blank, incorrect, and retired credentials without leaving the login page")
    @Story("Sign in validation")
    @Severity(CRITICAL)
    public void verifyRejectedSignInStaysOnLoginPage(
            @Param("username") String username,
            @Param(value = "password", mode = Parameter.Mode.MASKED) String password,
            @Param("expected message") String expectedMessage) {
        LoginPage loginPage = loginPage();

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        if (expectedMessage.startsWith("Empty")) {
            Assert.assertEquals(loginPage.getSignInLabel(), "Sign In");
        } else {
            Assert.assertEquals(loginPage.getSignInLabel(), "Signing ..");
        }

        String alert = loginPage.waitForAlertContaining(expectedMessage);
        Assert.assertTrue(alert.contains(expectedMessage), "Unexpected alert: " + alert);

        if (!expectedMessage.startsWith("Empty")) {
            Assert.assertEquals(loginPage.getSignInLabel(), "Sign In");
        }
        assertRemainsOnLoginPage(loginPage);
    }

    @Test(description = "Every login field can be completed while the browser stays on the login page")
    @Story("Complete form")
    @Severity(CRITICAL)
    public void verifyAllFieldsCanBeCompletedOnTheLoginPage() {
        LoginPage loginPage = loginPage();

        loginPage.enterUsername(ConfigReader.get("username"));
        loginPage.enterPassword(ConfigReader.get("password"));
        loginPage.selectRole("Consultant");
        loginPage.setTermsAccepted(true);

        Assert.assertEquals(loginPage.getUsernameValue(), ConfigReader.get("username"));
        Assert.assertEquals(loginPage.getPasswordValue(), ConfigReader.get("password"));
        Assert.assertTrue(loginPage.isAdminSelected());
        Assert.assertEquals(loginPage.getSelectedRole(), "Consultant");
        Assert.assertEquals(loginPage.getSelectedRoleValue(), "consult");
        Assert.assertTrue(loginPage.isTermsAccepted());
        Assert.assertEquals(loginPage.getSignInLabel(), "Sign In");
        assertRemainsOnLoginPage(loginPage);
    }

    private void assertRemainsOnLoginPage(LoginPage loginPage) {
        Allure.step("Confirm the browser is still on the login page", () -> {
            Assert.assertTrue(loginPage.isAtLoginPage(), "Browser left the login page: " + loginPage.getCurrentUrl());
            Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login form is no longer displayed");
        });
    }
}
