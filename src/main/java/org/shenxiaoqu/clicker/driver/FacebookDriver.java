package org.shenxiaoqu.clicker.driver;

import org.shenxiaoqu.clicker.util.PropertyUtil;

/**
 * User: longhengyu
 * Date: 12-12-7
 */
public class FacebookDriver extends AbstractDriver {

    private final String loginUrl = PropertyUtil.getProperty("login.url.facebook");

    public void login(String username, String password) {
        openUrl(loginUrl);
        findElementByXPath("//input[contains(@id,'email')]").sendKeys(username);
        findElementByXPath("//input[contains(@id,'pass')]").sendKeys(password);
        findElementByXPath("//*[@id=\"loginbutton\"]").click();
    }
}
