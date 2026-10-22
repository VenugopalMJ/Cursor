package com.academy.login.tests;

import com.academy.login.base.BaseTest;
import com.academy.login.config.ConfigReader;
import com.academy.login.driver.DriverFactory;
import com.academy.login.pages.ShopPage;
import com.academy.login.utils.AllureSteps;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Feature("Shopping page")
public class ShoppingPageTest extends BaseTest {

    @Test(description = "Valid credentials open the shopping page")
    @Story("Successful login")
    @Severity(CRITICAL)
    public void verifyLoginSucceedsWithValidCredentials() {
        ShopPage shopPage = signIn();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm the shopping page opened", () -> {
            Assert.assertEquals(shopPage.getTitle(), "ProtoCommerce");
            Assert.assertTrue(shopPage.getCurrentUrl().contains("/angularpractice/shop"), shopPage.getCurrentUrl());
            Assert.assertTrue(shopPage.isShopHeadingDisplayed());
        });
    }

    @Test(description = "The top of the shopping page shows Home, Shop, and Checkout")
    @Story("Top menu")
    @Severity(CRITICAL)
    public void verifyShoppingPageTopMenuOptions() {
        ShopPage shopPage = signIn();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm Home, Shop, and Checkout", () -> {
            Assert.assertEquals(shopPage.getPrimaryMenuOptions(), List.of("Home", "Shop"));
            Assert.assertTrue(shopPage.isHomeDisplayed());
            Assert.assertTrue(shopPage.isShopDisplayed());
            Assert.assertTrue(shopPage.isCheckoutDisplayed());
            Assert.assertTrue(shopPage.getCheckoutLabel().startsWith("Checkout"));
            Assert.assertEquals(shopPage.getCheckoutCount(), 0);
        });
    }

    @Test(description = "Adding iPhone sets the checkout count to 1")
    @Story("Cart count")
    @Severity(CRITICAL)
    public void verifyAddingIphoneSetsCheckoutCountToOne() {
        ShopPage shopPage = signIn();

        shopPage.addProductToCart("iphone");

        AllureSteps.run(DriverFactory.getDriver(), "Confirm the checkout count is 1", () ->
                Assert.assertEquals(shopPage.getCheckoutCount(), 1));
    }

    @Test(description = "Adding iPhone and Samsung sets the checkout count to 2")
    @Story("Cart count")
    @Severity(CRITICAL)
    public void verifyAddingIphoneAndSamsungSetsCheckoutCountToTwo() {
        ShopPage shopPage = signIn();

        shopPage.addProductToCart("iphone");
        shopPage.addProductToCart("Samsung");

        AllureSteps.run(DriverFactory.getDriver(), "Scroll to the top and confirm the checkout count is 2", () -> {
            shopPage.scrollToTop();
            Assert.assertEquals(shopPage.getCheckoutCount(), 2);
        });
    }

    private ShopPage signIn() {
        return loginPage().signIn(ConfigReader.get("username"), ConfigReader.get("password"));
    }
}
