package org.shenxiaoqu.rpoint;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RpointClicker {
	
	RpointClicker(String facebookUser, String facebookPass, String rakutenUser, String rakutenPass) throws Exception {
        this.facebookUser = facebookUser;
        this.facebookPass = facebookPass;
        this.rakutenUser = rakutenUser;
        this.rakutenPass = rakutenPass;
    }
	
	public enum ClickOrder {NEW_ARRIVAL, DEADLINE, OLD_ARRIVAL, POINT_AMOUT, WINNER_NUMBER};
	
	private ClickOrder clickOrder = ClickOrder.NEW_ARRIVAL;
	
	public void setClickOrder(int i) throws NumberFormatException {
		if (i < 0 || i >= ClickOrder.values().length) {
			throw new NumberFormatException("click order out of range");
		}
		clickOrder = ClickOrder.values()[i];
	}
	
	public void setClickOrder(ClickOrder order) {
		clickOrder = order;
	}

    protected WebDriver driver;
    
    String facebookUser;
    String facebookPass;
    String rakutenUser;
    String rakutenPass;

    int counterTimeout = 0;
    int counterAlreadyApplied = 0;
    int counterNewApplied = 0;
    int counterServerError = 0;
    int counterUnknownError = 0;

    int startPage = 1;
    int timeoutSecond = 5;

    int maxPageNum = 500;

    // the Event main page handler.
    String mainWindow;

    private void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public void setTimeoutSecond(int timeoutSecond) {
        this.timeoutSecond = timeoutSecond;
    }
    
    

    Map<Integer, Integer> pageMap = new HashMap<Integer, Integer>();

    public void setUp() throws Exception {
        pageMap.put(1, 4);
        pageMap.put(2, 7);
        pageMap.put(3, 8);
        pageMap.put(0, 9);
        FirefoxProfile profile = new FirefoxProfile();
        setDriver(new FirefoxDriver(profile));
        driver.manage().timeouts().implicitlyWait(timeoutSecond, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(45, TimeUnit.SECONDS);
    }

    public void getPoint() throws Exception {
        setUp();
        loginFacebook();
        loginRakuten();
        openRakutenEventPage();
        
        // record the main window
        mainWindow = driver.getWindowHandle();
        
        // select an order to click
        selectClickOrder();
        
        for (int page = 1; page < startPage + maxPageNum; page++) {
            if (page >= startPage) {
                for (int i = 1; i <= 20; i++) {
                    try {
                        log("\n-------------------------------------------");
                        switchToEventFrame();
                        applyShopCampaign(i);
                        
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

            try {
                log("go to page " + nextPage(page));
                driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));
                Thread.sleep(2000);
                int nextIndex = pageMap.containsKey(page) ? pageMap.get(page) : pageMap.get(0);
                driver.findElement(By.xpath("/html/body/div/div[3]/div/div/div/a[" + nextIndex + "]")).click();
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
    	driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));
    }
    
    private void switchToEventMainWindow() {
    	driver.switchTo().window(mainWindow);
    }

    private void selectClickOrder() {
    	
    	if (clickOrder != ClickOrder.NEW_ARRIVAL) {
			switchToEventFrame();
			driver.findElement(By.xpath("//*[@id=\"campaignCondition\"]/tbody/tr/td[2]/div/button[" + (clickOrder.ordinal() + 1) + "]")).click();
			switchToEventMainWindow();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int nextPage(int page) {
        return page + 1;
    }

    private void loginFacebook() {
        driver.get("http://www.facebook.com");
        driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(facebookUser);
        driver.findElement(By.xpath("//input[contains(@id,'pass')]")).sendKeys(facebookPass);
        driver.findElement(By.xpath("//*[@id=\"loginbutton\"]")).click();
    }

    private void loginRakuten() {
        driver.get("https://www.rakuten.co.jp/myrakuten/login.html");
        driver.findElement(By.xpath("//*[@id=\"userid\"]")).sendKeys(rakutenUser);
        driver.findElement(By.xpath("//*[@id=\"passwd\"]")).sendKeys(rakutenPass);
        driver.findElement(By.xpath("/html/body/div/div[2]/div/form/div/p/input")).click();
    }

    private void openRakutenEventPage() {
        driver.get("http://www.facebook.com/RakutenIchiba/app_260989120681773");
    }

    private void applyShopCampaign(int i) {
        String shopTitle = driver.findElement(By.xpath("/html/body/div/ul/div/div[3]/li[" + i + "]/dl/dd[3]/a")).getText();
        log("click " + shopTitle);

        // click the campaign to open a new window
        WebElement campaignLink = driver.findElement(By.xpath("/html/body/div/ul/div/div[3]/li[" + i + "]/dl/dd[4]/a"));
        clickAndSwitchToThatWindow(campaignLink);

        // apply for campaign
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

    private void clickAndSwitchToThatWindow(WebElement e) {
        final int windowsBefore = driver.getWindowHandles().size();
        e.click();

        ExpectedCondition<Boolean> windowCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getWindowHandles().size() == windowsBefore + 1;
            }
        };

        int waitSecond = 10;
        WebDriverWait waitForWindow = new WebDriverWait(driver, waitSecond);
        waitForWindow.until(windowCondition);

        switchToNewWindow();
    }

    private void switchToNewWindow() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    private void likeThisShop(String shopTitle) {
        WebElement likeButton = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div[2]/div/div/div/div[3]/span/span/span/label"));
        if (likeButton.isDisplayed()) {
            likeButton.click();
            log("This shop has been liked: " + shopTitle);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //driver.navigate().refresh();
        }
    }

    private void applyThisShop(String parent) {

        // switch to the campaign iframe
        driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));

        // log the campaign name
        String campaignName = driver.findElement(By.xpath("/html/body/div[3]/div[2]/p")).getText();
        log("campaign Name: " + campaignName);

        // wait a specified period of time for the "Apply" button
        WebDriverWait wait = new WebDriverWait(driver, timeoutSecond);
        WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/div[3]/div[2]/form/input")));

        clickAndSwitchToThatWindow(applyButton);

        if (driver.getTitle().contains(campaignName)) {
            WebElement applyButtonFinal = wait.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div/table[2]/tbody/tr/td/form/button"))));
            applyButtonFinal.click();
            log("apply for campaign " + campaignName + " success!");
            counterNewApplied++;
        } else {
            log("server error for campaign " + campaignName);
            counterServerError++;
        }
        driver.close();
        driver.switchTo().window(parent);
    }

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
        System.out.println(s);
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
}
