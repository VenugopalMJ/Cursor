package com.academy.login.tests;

import com.academy.login.base.BaseTest;
import com.academy.login.config.ConfigReader;
import com.academy.login.driver.DriverFactory;
import com.academy.login.pages.CartLine;
import com.academy.login.pages.CartPage;
import com.academy.login.pages.PurchasePage;
import com.academy.login.pages.ShopPage;
import com.academy.login.utils.AllureSteps;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@Feature("Checkout")
public class CheckoutPageTest extends BaseTest {

    private static final String SUCCESS_MESSAGE =
            "Success! Thank you! Your order will be delivered in next few weeks :-).";

    @Test(description = "Checkout quantities match the items added on the shop page")
    @Story("Checkout quantity")
    @Severity(CRITICAL)
    public void verifyCheckoutQuantitiesMatchAddedItems() {
        ShopPage shopPage = signInAndAddIphoneAndSamsung();
        int itemsAdded = shopPage.getCheckoutCount();
        CartPage cartPage = shopPage.openCheckout();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm checkout quantities match the added items", () -> {
            cartPage.scrollToCart();
            Assert.assertEquals(cartPage.sumOfQuantities(), itemsAdded);
            Assert.assertEquals(
                    cartPage.getLines().stream().map(CartLine::quantity).toList(),
                    List.of(1, 1));
        });
    }

    @Test(description = "The checkout page shows Remove, Continue Shopping, and Checkout")
    @Story("Checkout buttons")
    @Severity(CRITICAL)
    public void verifyCheckoutPageButtons() {
        CartPage cartPage = openCartWithIphoneAndSamsung();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm the checkout page buttons", () -> {
            cartPage.scrollToCart();
            List<String> buttons = cartPage.getButtonLabels();
            Assert.assertTrue(buttons.contains("Remove"), buttons.toString());
            Assert.assertTrue(buttons.contains("Continue Shopping"), buttons.toString());
            Assert.assertTrue(buttons.contains("Checkout"), buttons.toString());
        });
    }

    @Test(description = "The checkout total matches the selected quantities")
    @Story("Checkout total")
    @Severity(CRITICAL)
    public void verifyTotalMatchesSelectedQuantities() {
        CartPage cartPage = openCartWithIphoneAndSamsung();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm the total matches the selected quantities", () -> {
            cartPage.scrollToCart();
            int expectedTotal = 0;
            for (CartLine line : cartPage.getLines()) {
                Assert.assertEquals(line.lineTotal(), line.unitPrice() * line.quantity(), line.productName());
                expectedTotal += line.lineTotal();
            }
            Assert.assertEquals(cartPage.getGrandTotal(), expectedTotal);
        });
    }

    @Test(description = "Checkout accepts Rochester, the terms, and Purchase")
    @Story("Purchase")
    @Severity(CRITICAL)
    public void verifyPurchaseWithRochesterDelivery() {
        PurchasePage purchasePage = openCartWithIphoneAndSamsung().proceedToCheckout();

        purchasePage.enterDeliveryLocation("Rochester");
        purchasePage.acceptTerms();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm Rochester and the terms are selected", () -> {
            Assert.assertEquals(purchasePage.getDeliveryLocation(), "Rochester");
            Assert.assertTrue(purchasePage.isTermsAccepted());
        });

        purchasePage.purchase();
    }

    @Test(description = "Purchase shows the successful order message")
    @Story("Purchase confirmation")
    @Severity(CRITICAL)
    public void verifySuccessfulPurchaseMessage() {
        PurchasePage purchasePage = openCartWithIphoneAndSamsung().proceedToCheckout();
        purchasePage.enterDeliveryLocation("Rochester");
        purchasePage.acceptTerms();
        purchasePage.purchase();

        AllureSteps.run(DriverFactory.getDriver(), "Confirm the successful order message", () -> {
            String message = purchasePage.getSuccessMessage().replace("×", "").trim();
            Assert.assertEquals(message, SUCCESS_MESSAGE);
        });
    }

    private CartPage openCartWithIphoneAndSamsung() {
        return signInAndAddIphoneAndSamsung().openCheckout();
    }

    private ShopPage signInAndAddIphoneAndSamsung() {
        WebDriver driver = DriverFactory.getDriver();
        ShopPage shopPage = loginPage().signIn(ConfigReader.get("username"), ConfigReader.get("password"));
        shopPage.addProductToCart("iphone");
        shopPage.addProductToCart("Samsung");
        AllureSteps.run(driver, "Scroll to the top and confirm two items were added", () -> {
            shopPage.scrollToTop();
            Assert.assertEquals(shopPage.getCheckoutCount(), 2);
        });
        return shopPage;
    }
}
