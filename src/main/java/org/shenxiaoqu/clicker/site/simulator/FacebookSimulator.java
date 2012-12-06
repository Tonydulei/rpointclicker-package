package org.shenxiaoqu.clicker.site.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 *
 * User: longhengyu
 * Date: 12-12-6
 *
 * This simulator handles the user simulator combinations on facebook site.
 * the actions related to specific situation should be in the derived classes.
 */
public class FacebookSimulator extends CommonSiteSimulator {

    public FacebookSimulator(WebDriver driver) {
        setDriver(driver);
    }

    public void login(String username, String password) {
        driver.get("http://www.facebook.com");
        driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(username);
        driver.findElement(By.xpath("//input[contains(@id,'pass')]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"loginbutton\"]")).click();
    }
}
