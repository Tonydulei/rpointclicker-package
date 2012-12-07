package org.shenxiaoqu.clicker.point;
import org.shenxiaoqu.clicker.driver.RpointCampaignDriver;
import org.shenxiaoqu.clicker.site.simulator.RpointCampaignSimulator;

import java.io.Console;
import java.io.PrintStream;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String facebookUser, facebookPass, rakutenUser, rakutenPass;
        PrintStream out = System.out;
        Scanner in = new Scanner(System.in);
        try {

            Console console = System.console();
            if (console == null) {
                System.out.println("Couldn't get Console instance");
                System.exit(0);
            }

            console.printf("###### welcome to shenxiaoqu ###### \n\n");


            System.out.print("Facebook UserName (email): ");
            facebookUser = in.nextLine();
            char fPasswordArray[] = console.readPassword("Facebook password: ");
            facebookPass = new String(fPasswordArray);
            System.out.print("R UserName (email): ");
            rakutenUser = in.nextLine();
            char rPasswordArray[] = console.readPassword("R password: ");
            rakutenPass = new String(rPasswordArray);

            RpointCampaignSimulator simulator = new RpointCampaignSimulator(facebookUser, facebookPass, rakutenUser, rakutenPass);

            // set click condition.
            out.print("Set Find Element Timeout in second (default=5): ");
            try {
                simulator.setImplicitlyWaitSecond(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                out.println("timeout format wrong. use default ...");
            }
            out.print("Set Page Load Timeout in second (default=45): ");
            try {
                simulator.setPageLoadTimeoutSecond(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                out.println("timeout format wrong. use default ...");
            }
            out.print("Set Javascript Timeout in second (default=10): ");
            try {
                simulator.setScriptTimeoutSecond(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                out.println("timeout format wrong. use default ...");
            }

            
            // set click order
            simulator.printSelectOrderDescription();
            out.print("\nPlease choose click order (default=0): ");
            try {
                simulator.setClickOrder(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                out.println("click order wrong. use default ...");
            }

            out.print("Start Page (default=1): ");
            try {
                simulator.setStartPage(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                out.println("page format wrong. use default ...");
            }

            out.print("\nStart ...");
            simulator.run();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Bye!");
        }
    }
}
