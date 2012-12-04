package org.shenxiaoqu.rpoint;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RpointClicker {
	
	protected WebDriver driver;
	protected String baseUrl;
	protected StringBuffer verificationErrors = new StringBuffer();
	
	RpointClicker(String facebookUser,String facebookPass,String rakutenUser,String rakutenPass) throws Exception {
		this.facebookUser = facebookUser;
		this.facebookPass = facebookPass;
		this.rakutenUser = rakutenUser;
		this.rakutenPass = rakutenPass;
	}
	
	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public StringBuffer getVerificationErrors() {
		return verificationErrors;
	}
	
	String facebookUser;
    String facebookPass;
    String rakutenUser;
    String rakutenPass;

    int counterAlreadyApplied = 0;
    int counterNewApplied = 0;
    int counterServerError = 0;

    int startPage = 1;
    int timeoutSecond = 5;

    int maxPageNum = 500;

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
	}	
    
    public void getPoint() throws Exception {
        setUp();
        loginFacebook();
        loginRakuten();
        openRakutenEventPage();
        String mainWindow = driver.getWindowHandle();
        for(int page = 1; page < startPage + maxPageNum; page++) {
            if (page >= startPage) {
                for (int i = 1; i <= 20; i++) {
                    try {
                        log("\n-------------------------------------------");
                        driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));
                        applyShopCampaign(i);
                        driver.switchTo().window(mainWindow);
                    } catch (NoSuchElementException e) {
                        log("unknown Error.");
                        backToNormal(mainWindow);
                    }
                    logStatus();
                }
            }

            try {
                log("go to page " + nextPage(page));
                driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));
                Thread.sleep(2000);
                int nextIndex = pageMap.containsKey(page) ? pageMap.get(page) : pageMap.get(0);
                driver.findElement(By.xpath("/html/body/div/div[3]/div/div/div/a[" + nextIndex + "]")).click();
                driver.switchTo().window(mainWindow);
            } catch (NoSuchElementException e) {
                log("switch page Error.");
                backToNormal(mainWindow);
            }
        }
	}

    private int nextPage(int page) {
        return page+1;
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
        driver.findElement(By.xpath("/html/body/div/ul/div/div[3]/li[" + i + "]/dl/dd[4]/a")).click();
        boolean serverOk = false;
        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
            if(driver.getTitle().contains(shopTitle)) {
                log("start " + shopTitle);
                likeThisShop(shopTitle);
                applyThisShop(winHandle);
                serverOk = true;
                break;
            }
        }
        if (!serverOk) {
            log("server error for " + shopTitle);
            counterServerError++;
        } else {
            log("apply " + shopTitle + " done!");
        }
        driver.close();
    }

    private void likeThisShop(String shopTitle) {
        WebElement likeButton = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div/div[2]/div/div/div/div[3]/span/span/span/label"));
        if (likeButton.isDisplayed()) {
            likeButton.click();
            log("This shop has been liked: " + shopTitle);
        }
    }

    private void applyThisShop(String parent) {
        log("focus iFrame.");
        driver.switchTo().frame(driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div[2]/div[2]/div[2]/div/div/div[2]/div/div/div/div/iframe")));
        log("Get into iFrame.");

        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutSecond);
        	WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/div[3]/div[2]/form/input")));
            //WebElement applyButton = driver.findElement(By.xpath("/html/body/div[3]/div[3]/div[2]/form/input"));
            String campaignName = driver.findElement(By.xpath("/html/body/div[3]/div[2]/p")).getText();
            log("campaign Name: " + campaignName);
            log("Click Apply");
            applyButton.click();
            for(String winHandle : driver.getWindowHandles()){
                driver.switchTo().window(winHandle);
                if(driver.getTitle().contains(campaignName)) {
                    log("apply for campaign " + campaignName);
                    driver.findElement(By.xpath("/html/body/div/table[2]/tbody/tr/td/form/button")).click();
                    log("apply for campaign " + campaignName + " success!");
                    counterNewApplied++;
                    driver.close();
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            log("this campaign is already applied!");
            counterAlreadyApplied++;
        } catch (TimeoutException e) {
        	log("try to get apply button but timeout --- (already applied).");
            counterAlreadyApplied++;
        }
        driver.switchTo().window(parent);
    }
    
    private void backToNormal(String mainWindow) {
    	for(String winHandle : driver.getWindowHandles()){
    		if(!winHandle.equals(mainWindow)) {
                driver.switchTo().window(winHandle);
                driver.close();
            }
        }
    	driver.switchTo().window(mainWindow);
    }

    private void log(String s) {
        System.out.println(s);
    }

    private void logStatus() {
        log("-------------------- status -------------------");
        log("--- New Applied: " + counterNewApplied);
        log("--- Already Applied: " + counterAlreadyApplied);
        log("--- Server Error: " + counterServerError);
        log("-----------------------------------------------");
    }
}
