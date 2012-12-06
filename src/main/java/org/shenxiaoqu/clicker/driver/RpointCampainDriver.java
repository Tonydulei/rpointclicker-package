package org.shenxiaoqu.clicker.driver;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.shenxiaoqu.clicker.site.simulator.FacebookSimulator;
import org.shenxiaoqu.clicker.site.simulator.RakutenSimulator;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class RpointCampainDriver extends AbstractDriver {

    PrintStream logger;

    RakutenSimulator rakutenSimulator;
    FacebookSimulator facebookSimulator;

    public RpointCampainDriver(String facebookUser, String facebookPass, String rakutenUser, String rakutenPass) throws Exception {
        this.facebookUser = facebookUser;
        this.facebookPass = facebookPass;
        this.rakutenUser = rakutenUser;
        this.rakutenPass = rakutenPass;
        logger = System.out;
    }

    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }

    public enum ClickOrder {NEW_ARRIVAL, DEADLINE, OLD_ARRIVAL, POINT_AMOUT, WINNER_NUMBER};
	
	private ClickOrder clickOrder = ClickOrder.NEW_ARRIVAL;
	
	String facebookUser;
    String facebookPass;
    String rakutenUser;
    String rakutenPass;

    // counters for printing the status
    int counterTimeout = 0;
    int counterAlreadyApplied = 0;
    int counterNewApplied = 0;
    int counterServerError = 0;
    int counterUnknownError = 0;

    int startPage = 1;
    
    // wait timeout if the "Apply" button does not present
    int timeoutSecond = 5;

    int maxPageNum = 1000;
    int numOfEventsOnePage = 20;

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
    
    /**
	 * set order to click
	 * @param i
	 * @throws NumberFormatException
	 */
	public void setClickOrder(int i) throws NumberFormatException {
		if (i < 0 || i >= ClickOrder.values().length) {
			throw new NumberFormatException("click order out of range");
		}
		clickOrder = ClickOrder.values()[i];
	}

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public void setTimeoutSecond(int timeoutSecond) {
        this.timeoutSecond = timeoutSecond;
    }
    


    protected void setUp() throws Exception {
        super.setUp();
    	setForwardOrder();
        rakutenSimulator = new RakutenSimulator(driver);
        facebookSimulator = new FacebookSimulator(driver);
    }
    
    /**
     * This is the main functional method of the Rpoint Clicker
     * @throws Exception
     */

    public void run() throws Exception {
        setUp();
        facebookSimulator.login(facebookUser, facebookPass);
        rakutenSimulator.login(rakutenUser, rakutenPass);
        rakutenSimulator.openRakutenIchibaFacebookEventPage();
        
        // record the main window
        mainWindow = driver.getWindowHandle();
        
        // select an order to click
        selectClickOrder();
        
        for (int page = 1; page < startPage + maxPageNum; page++) {
            if (page >= startPage) {
                for (int i = 1; i <= numOfEventsOnePage; i++) {
                    try {
                        log("\n-------------------------------------------");
                        switchToEventFrame();
                        applyShopCampaign(i);
                        driver.switchTo().window(mainWindow);
                    } catch (NoSuchElementException e) {
                        log("Timeout, wait " + timeoutSecond + " and didn't find the element.");
                        counterTimeout++;
                        backToNormal();
                    } catch (TimeoutException e) {
                        if (driver.getPageSource().contains("応募完了しました")) {
                            log("You already applied this campaign.");
                            counterAlreadyApplied++;
                        } else {
                            log("Timeout, wait " + timeoutSecond + " and didn't find the element.");
                            counterTimeout++;
                        }
                        backToNormal();
                    } catch (Exception e) {
                        log("unknown error");
                        e.printStackTrace();
                        counterUnknownError++;
                        backToNormal();
                    }
                    logStatus(page, i);
                }
            }

            // go to next page.
            try {
                log("go to page " + nextPage(page));
                switchToEventFrame();
                Thread.sleep(2000);
                int nextIndex = pageMap.containsKey(page) ? pageMap.get(page) : pageMap.get(0);
                clickElementByXPath("/html/body/div/div[3]/div/div/div/a[" + nextIndex + "]");
                switchToEventMainWindow();
            } catch (NoSuchElementException e) {
                log("switch page Error.");
                backToNormal();
            } catch (TimeoutException e) {
                log("timeout: pageload ?");
                backToNormal();
            }
        }
    }
    
    private void switchToEventFrame() {
    	switchToFrameByXPath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe");
    }
    
    private void switchToEventMainWindow() {
    	switchToWindow(mainWindow);
    }

    /**
     *  there are 5 types of order for listing the campaign
     *  this method selects one specified by User. Default is 0.
     */
    private void selectClickOrder() {
    	if (clickOrder != ClickOrder.NEW_ARRIVAL) {
			switchToEventFrame();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            clickElementByXPath("/html/body/div[1]/div[2]/table/tbody/tr/td[2]/div/button[" + (clickOrder.ordinal() + 1) + "]");
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
    private void applyShopCampaign(int i) {
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
            log("server error for " + shopTitle);
            counterServerError++;
        }
        driver.close();
    }

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
            counterNewApplied++;
        } else {
            log("server error for campaign " + campaignName);
            counterServerError++;
        }
        driver.close();
        driver.switchTo().window(parent);
    }

    /**
     * if some error happened, set the driver back to MainWindow
     * before continue working.
     */
    private void backToNormal() {
        for (String winHandle : driver.getWindowHandles()) {
            if (!winHandle.equals(mainWindow)) {
                driver.switchTo().window(winHandle);
                driver.close();
            }
        }
        switchToEventMainWindow();
    }

    private void log(String s) {
        logger.println(s);
    }

    private void logStatus(int page, int i) {
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

    private int nextPage(int page) {
        return page + 1;
    }
}
