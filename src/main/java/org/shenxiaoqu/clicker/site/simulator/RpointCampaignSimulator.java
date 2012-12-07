package org.shenxiaoqu.clicker.site.simulator;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.shenxiaoqu.clicker.driver.FacebookDriver;
import org.shenxiaoqu.clicker.driver.RpointCampaignDriver;
import org.shenxiaoqu.clicker.exception.ServerErrorException;
import org.shenxiaoqu.clicker.site.logic.CampaignListOrder;

import java.io.PrintStream;

/**
 * User: longhengyu
 * Date: 12-12-6
 *
 * This simulator handles the user simulator combinations like an action graph.
 * the actual event action should be in XXX Driver.
 */
public class RpointCampaignSimulator extends SiteSimulator {

    FacebookDriver facebookDriver;
    RpointCampaignDriver rpointCampaignDriver;

    PrintStream out = System.out;

    String facebookUser, facebookPass, rakutenUser, rakutenPass;

    private CampaignListOrder clickOrder = CampaignListOrder.NEW_ARRIVAL;


    // constant
    int maxPageNum = 1000;
    int numOfEventsOnePage = 20;


    // user define
    int startPage = 1;

    // counters for printing the status
    int counterTimeout = 0;
    int counterAlreadyApplied = 0;
    int counterNewApplied = 0;
    int counterServerError = 0;
    int counterUnknownError = 0;

    /**
     * constructor contains the mandatory fields
     * @param facebookUser
     * @param facebookPass
     * @param rakutenUser
     * @param rarkutenPass
     */
    public RpointCampaignSimulator(String facebookUser, String facebookPass, String rakutenUser, String rarkutenPass) {
        this.facebookUser = facebookUser;
        this.facebookPass = facebookPass;
        this.rakutenUser = rakutenUser;
        this.rakutenPass = rarkutenPass;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    /**
     * set order to click
     * @param i
     * @throws NumberFormatException
     */
    public void setClickOrder(int i) throws NumberFormatException {
        if (i < 0 || i >= CampaignListOrder.values().length) {
            throw new NumberFormatException("click order out of range");
        }
        clickOrder = CampaignListOrder.values()[i];
    }

    protected void setUp() throws Exception {
        super.setUp();
        rpointCampaignDriver = new RpointCampaignDriver();
        facebookDriver = new FacebookDriver();
        rpointCampaignDriver.setDriver(webDriver);
        facebookDriver.setDriver(webDriver);
    }

    public void printSelectOrderDescription() {
        out.print("\n-------------------------");
        out.print("\n--- 0. new arival");
        out.print("\n--- 1. deadline");
        out.print("\n--- 2. old arival");
        out.print("\n--- 3. point");
        out.print("\n--- 4. number of winers");
        out.print("\n-------------------------");
    }

    /**
     * This is the main functional method of the Rpoint Clicker
     * @throws Exception
     */

    public void run() throws Exception {
        setUp();
        facebookDriver.login(facebookUser, facebookPass);
        rpointCampaignDriver.login(rakutenUser, rakutenPass);
        rpointCampaignDriver.openRakutenIchibaFacebookEventPage();
        rpointCampaignDriver.recordEventMainWindow();

        // select an order to click
        rpointCampaignDriver.selectClickOrder(clickOrder);

        for (int page = 1; page < startPage + maxPageNum; page++) {
            if (page >= startPage) {
                for (int i = 1; i <= numOfEventsOnePage; i++) {
                    rpointCampaignDriver.logStart();
                    try {
                        rpointCampaignDriver.switchToEventFrame();
                        rpointCampaignDriver.applyShopCampaign(i);
                        rpointCampaignDriver.switchToEventMainWindow();
                        counterNewApplied++;
                    } catch (NoSuchElementException e) {
                        rpointCampaignDriver.log("Timeout, wait " + implicitlyWaitSecond + " and didn't find the element.");
                        counterTimeout++;
                        rpointCampaignDriver.backToNormal();
                    } catch (TimeoutException e) {
                        if (rpointCampaignDriver.isCampaignAlreadyApplied()) {
                            rpointCampaignDriver.log("You already applied this campaign.");
                            counterAlreadyApplied++;
                        } else {
                            rpointCampaignDriver.log("Timeout, wait " + implicitlyWaitSecond + " and didn't find the element.");
                            counterTimeout++;
                        }
                        rpointCampaignDriver.backToNormal();
                    } catch (ServerErrorException e) {
                        counterServerError++;
                        rpointCampaignDriver.log(e.getMessage());
                    } catch (Exception e) {
                        rpointCampaignDriver.log("unknown error");
                        e.printStackTrace();
                        counterUnknownError++;
                        rpointCampaignDriver.backToNormal();
                    }
                    rpointCampaignDriver.logStatus(page, i,
                            counterNewApplied,
                            counterAlreadyApplied,
                            counterTimeout,
                            counterServerError,
                            counterUnknownError);
                }
            }

            // go to next page.
            try {
                rpointCampaignDriver.log("go to page " + nextPage(page));
                rpointCampaignDriver.switchToEventFrame();
                Thread.sleep(2000);
                rpointCampaignDriver.goToNextPage(page);
                rpointCampaignDriver.switchToEventMainWindow();
            } catch (NoSuchElementException e) {
                rpointCampaignDriver.log("switch page Error.");
                rpointCampaignDriver.backToNormal();
            } catch (TimeoutException e) {
                rpointCampaignDriver.log("timeout: pageload ?");
                rpointCampaignDriver.backToNormal();
            }
        }
    }

    private int nextPage(int page) {
        return page + 1;
    }
}
