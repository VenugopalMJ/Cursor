package com.academy.login.base;

import com.academy.login.config.ConfigReader;
import com.academy.login.driver.DriverFactory;
import com.academy.login.pages.LoginPage;
import com.academy.login.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        DriverFactory.createDriver().get(ConfigReader.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        try {
            if (driver != null) {
                byte[] png = ScreenshotUtil.capture(driver);
                if (png.length > 0) {
                    String name = result.getStatus() == ITestResult.FAILURE
                            ? "Failure screenshot"
                            : "Page screenshot";
                    Allure.attachment(
                            name,
                            "image/png",
                            new ByteArrayInputStream(png),
                            AttachmentOptions.withFileExtension(".png"));
                    if (result.getStatus() == ITestResult.FAILURE) {
                        ScreenshotUtil.save(png, result.getName());
                    }
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }

    protected LoginPage loginPage() {
        return new LoginPage(DriverFactory.getDriver());
    }
}
