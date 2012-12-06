package org.shenxiaoqu.clicker.site.simulator;

import org.openqa.selenium.WebDriver;

/**
 * User: longhengyu
 * Date: 12-12-6
 */
public class CommonSiteSimulator {

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    protected WebDriver driver;

}
