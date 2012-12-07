package org.shenxiaoqu.clicker.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.shenxiaoqu.clicker.util.PropertyUtil;

import java.io.PrintStream;

public abstract class AbstractDriver {

    private int waitConditionSecond = PropertyUtil.getPropertyInt("driver.wait.condition.second");

    protected PrintStream log = System.out;
    protected WebDriver driver;

    public void setLog(PrintStream log) {
        this.log = log;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    protected void openUrl(String url) {
        driver.get(url);
    }

    protected void refreshWindow() {
        driver.navigate().refresh();
    }

    protected void switchToFrameByXPath(String xPath) throws NoSuchElementException {
        switchToFrame(findElementByXPath(xPath));
    }

    protected void switchToFrame(WebElement frame) {
        driver.switchTo().frame(frame);
    }

    protected void switchToWindow(String windowHandler) {
        driver.switchTo().window(windowHandler);
    }

    protected void clickElementByXPath(String xPath) throws NoSuchElementException {
        clickElement(findElementByXPath(xPath));
    }

    protected void clickElement(WebElement e) {
        e.click();
    }

    protected WebElement findElementByXPath(String xPath) {
        return driver.findElement(By.xpath(xPath));
    }

    protected WebElement findElementByXPathUntilClickable(String xPath) {
        WebDriverWait wait = new WebDriverWait(driver, waitConditionSecond);
        return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
    }

    /**
     * click an element which opens a new window, and
     * switch the driver to this new window.
     * @param e
     */
    protected void clickAndSwitchToThatWindow(WebElement e) {
        clickAndSwitchToThatWindow(e, waitConditionSecond);
    }

    protected void clickAndSwitchToThatWindow(WebElement e, int waitSecond) {
        final int windowsBefore = driver.getWindowHandles().size();
        e.click();

        ExpectedCondition<Boolean> windowCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getWindowHandles().size() == windowsBefore + 1;
            }
        };

        WebDriverWait waitForWindow = new WebDriverWait(driver, waitSecond);
        waitForWindow.until(windowCondition);

        switchToNewestWindow();
    }

    /**
     * switch to the newest opened window
     */
    protected void switchToNewestWindow() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

}

