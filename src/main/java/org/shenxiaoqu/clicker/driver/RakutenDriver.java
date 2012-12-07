package org.shenxiaoqu.clicker.driver;
import org.shenxiaoqu.clicker.util.PropertyUtil;

/**
 * User: longhengyu
 * Date: 12-12-7
 */
public class RakutenDriver extends AbstractDriver {

    private final String loginUrl = PropertyUtil.getProperty("login.url.rakuten");

    public void login(String username, String password) {
        openUrl(loginUrl);
        findElementByXPath("//*[@id=\"userid\"]").sendKeys(username);
        findElementByXPath("//*[@id=\"passwd\"]").sendKeys(password);
        findElementByXPath("/html/body/div/div[2]/div/form/div/p/input").click();
    }
}
