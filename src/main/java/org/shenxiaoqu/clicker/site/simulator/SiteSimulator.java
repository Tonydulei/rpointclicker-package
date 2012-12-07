package org.shenxiaoqu.clicker.site.simulator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.shenxiaoqu.clicker.util.PropertyUtil;

import java.util.concurrent.TimeUnit;

/**
 * User: longhengyu
 * Date: 12-12-6
 */
public abstract class SiteSimulator {

    protected WebDriver webDriver;

    public void setImplicitlyWaitSecond(int implicitlyWaitSecond) {
        this.implicitlyWaitSecond = implicitlyWaitSecond;
    }

    public void setScriptTimeoutSecond(int scriptTimeoutSecond) {
        this.scriptTimeoutSecond = scriptTimeoutSecond;
    }

    public void setPageLoadTimeoutSecond(int pageLoadTimeoutSecond) {
        this.pageLoadTimeoutSecond = pageLoadTimeoutSecond;
    }

    protected void setUp() throws Exception {
        webDriver = new FirefoxDriver((new FirefoxProfile()));
        webDriver.manage().timeouts().implicitlyWait(implicitlyWaitSecond, TimeUnit.SECONDS);
        webDriver.manage().timeouts().setScriptTimeout(scriptTimeoutSecond, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(pageLoadTimeoutSecond, TimeUnit.SECONDS);
    }

    int implicitlyWaitSecond = PropertyUtil.getPropertyInt("driver.default.timeout.implicitly");
    int scriptTimeoutSecond = PropertyUtil.getPropertyInt("driver.default.timeout.script");
    int pageLoadTimeoutSecond = PropertyUtil.getPropertyInt("driver.default.timeout.pageload");

    abstract public void run() throws Exception;

}
