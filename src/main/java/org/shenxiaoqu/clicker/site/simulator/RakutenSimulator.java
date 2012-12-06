package org.shenxiaoqu.clicker.site.simulator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * User: longhengyu
 * Date: 12-12-6
 *
 * This simulator handles the user simulator combinations on rakuten site.
 * the actions related to specific situation should be in the derived classes.
 */
public class RakutenSimulator extends CommonSiteSimulator {

    public RakutenSimulator(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        driver.get("https://www.rakuten.co.jp/myrakuten/login.html");
        driver.findElement(By.xpath("//*[@id=\"userid\"]")).sendKeys(username);
        driver.findElement(By.xpath("//*[@id=\"passwd\"]")).sendKeys(password);
        driver.findElement(By.xpath("/html/body/div/div[2]/div/form/div/p/input")).click();
    }

    public void openRakutenIchibaFacebookEventPage() {
        driver.get("http://www.facebook.com/RakutenIchiba/app_260989120681773");
    }
}
