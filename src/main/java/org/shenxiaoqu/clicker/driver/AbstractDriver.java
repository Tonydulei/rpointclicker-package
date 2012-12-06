package org.shenxiaoqu.clicker.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDriver {

    protected WebDriver driver;
    int implicitlyWaitSecond = 5;
    int scriptTimeoutSecond = 10;
    int pageLoadTimoutSecond = 30;

    protected void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setImplicitlyWaitSecond(int implicitlyWaitSecond) {
        this.implicitlyWaitSecond = implicitlyWaitSecond;
    }

    public void setScriptTimeoutSecond(int scriptTimeoutSecond) {
        this.scriptTimeoutSecond = scriptTimeoutSecond;
    }

    public void setPageLoadTimoutSecond(int pageLoadTimoutSecond) {
        this.pageLoadTimoutSecond = pageLoadTimoutSecond;
    }

    protected void setUp() throws Exception {
        FirefoxProfile profile = new FirefoxProfile();
        setDriver(new FirefoxDriver(profile));
        driver.manage().timeouts().implicitlyWait(implicitlyWaitSecond, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(scriptTimeoutSecond, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimoutSecond, TimeUnit.SECONDS);
    }

    /**
     * This is the main functional method of the driver
     * @throws Exception
     */

    public abstract void run() throws Exception;

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
        WebDriverWait wait = new WebDriverWait(driver, implicitlyWaitSecond);
        return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
    }

    /**
     * click an element which opens a new window, and
     * switch the driver to this new window.
     * @param e
     */
    protected void clickAndSwitchToThatWindow(WebElement e) {
        clickAndSwitchToThatWindow(e, pageLoadTimoutSecond);
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

