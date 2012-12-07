package org.shenxiaoqu.clicker.driver;

import org.openqa.selenium.WebElement;
import org.shenxiaoqu.clicker.exception.ServerErrorException;
import org.shenxiaoqu.clicker.site.logic.CampaignListOrder;
import org.shenxiaoqu.clicker.util.PropertyUtil;

import java.util.HashMap;
import java.util.Map;

public class RpointCampaignDriver extends RakutenDriver {

    public RpointCampaignDriver() {
        super();
        setForwardOrder();
    }

    // the Event main page handler.
    String mainWindow;

    /**
     * This map is built to look for the "Next Page" button in the simulator iFrame
     */
    Map<Integer, Integer> pageMap = new HashMap<Integer, Integer>();

    private void setForwardOrder() {
        pageMap.put(1, 4);
        pageMap.put(2, 7);
        pageMap.put(3, 8);
        pageMap.put(0, 9);
    }

    public void openRakutenIchibaFacebookEventPage() {
        openUrl(PropertyUtil.getProperty("event.url.rakuten.facebook.point.campaign"));
    }
    
    // record the main window
    public void recordEventMainWindow() {
        mainWindow = driver.getWindowHandle();
    }

    public void switchToEventFrame() {
    	switchToFrameByXPath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe");
    }
    
    public void switchToEventMainWindow() {
    	switchToWindow(mainWindow);
    }

    /**
     *  there are 5 types of order for listing the campaign
     *  this method selects one specified by User. Default is 0.
     */
    public void selectClickOrder(CampaignListOrder order) {
    	if (order != CampaignListOrder.NEW_ARRIVAL) {
			switchToEventFrame();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            clickElementByXPath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/button[" + (order.ordinal() + 1) + "]");
            switchToEventMainWindow();
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
     * Apply one campaign on a page.
     * @param i : the i-th campaign on this page
     */
    public void applyShopCampaign(int i) {
        String shopTitle = findElementByXPath("/html/body/div/ul/div/div[3]/li[" + i + "]/dl/dd[3]/a").getText();
        log("click " + shopTitle);

        // click the campaign to open a new window
        clickAndSwitchToThatWindow(findElementByXPathUntilClickable("/html/body/div/ul/div/div[3]/li[" + i + "]/dl/dd[4]/a"));

        // apply for campaign if the shopName matches, or there might be a server error.
        String partShopName = shopTitle.substring(0, shopTitle.length() > 5 ? 5 : shopTitle.length());
        if (driver.getTitle().contains(partShopName)) {
            likeThisShop(shopTitle);
            applyThisShop(driver.getWindowHandle());
        } else {
            throw new ServerErrorException("server error for " + shopTitle);
        }
        driver.close();
    }

    public boolean isCampaignAlreadyApplied() {
        return driver.getPageSource().contains(PropertyUtil.getProperty("message.campaign.already.applied"));
    }

    /**
     * if some error happened, set the driver back to MainWindow
     * before continue working.
     */
    public void backToNormal() {
        for (String winHandle : driver.getWindowHandles()) {
            if (!winHandle.equals(mainWindow)) {
                driver.switchTo().window(winHandle);
                driver.close();
            }
        }
        switchToEventMainWindow();
    }

    public void goToNextPage(int page) {
        int nextIndex = pageMap.containsKey(page) ? pageMap.get(page) : pageMap.get(0);
        clickElementByXPath("/html/body/div/div[3]/div/div/div/a[" + nextIndex + "]");
    }

    public void log(String s) {
        log.println(s);
    }

    public void logStart() {
        log("\n-------------------------------------------");
    }

    public void logStatus(int page, int i,
                          int counterNewApplied,
                          int counterAlreadyApplied,
                          int counterTimeout,
                          int counterServerError,
                          int counterUnknownError) {
        log("----------------------------------------------");
        log("-----  finish page " + page + "     ---    campaign " + i);
        log("-------------------- status -------------------");
        log("-----  New Applied:           " + counterNewApplied);
        log("-----  Already Applied:       " + counterAlreadyApplied);
        log("-----  Timeout:               " + counterTimeout);
        log("-----  Server Error:          " + counterServerError);
        log("-----  Unknown Error:         " + counterUnknownError);
        log("-----------------------------------------------");
    }

    /**
     * Private Method
     */

    /**
     *
     * @param shopTitle
     */
    private void likeThisShop(String shopTitle) {
        WebElement likeButton = findElementByXPath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div[2]/div/div/div/div[3]/span/span/span/label");
        if (likeButton.isDisplayed()) {
            clickElement(likeButton);
            log("This shop has been liked: " + shopTitle);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    // apply the shop
    private void applyThisShop(String parent) {
        // switch to the campaign iframe
        switchToEventFrame();
        // log the campaign name
        String campaignName = findElementByXPath("/html/body/div[3]/div[2]/p").getText();
        log("campaign Name: " + campaignName);

        // wait a specified period of time for the "Apply" button
        clickAndSwitchToThatWindow(findElementByXPathUntilClickable("/html/body/div[3]/div[3]/div[2]/form/input"));

        if (driver.getTitle().contains(campaignName)) {
            clickElementByXPath("/html/body/div/table[2]/tbody/tr/td/form/button");
            log("apply for campaign " + campaignName + " success!");
        } else {
            throw new ServerErrorException("server error for campaign " + campaignName);
        }
        driver.close();
        driver.switchTo().window(parent);
    }

}
