package org.shenxiaoqu.clicker.point;
import org.shenxiaoqu.clicker.site.simulator.RpointCampaignSimulator;

import java.io.Console;
import java.io.PrintStream;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String facebookUser, facebookPass, rakutenUser, rakutenPass;
        try {
            PrintStream out = System.out;
            Console console = System.console();
            if (console == null) {
                System.out.println("Couldn't get Console instance");
                System.exit(0);
            }

            console.printf("###### welcome to shenxiaoqu ###### \n\n");

        	Scanner in = new Scanner(System.in);
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
            System.out.print("Set Timeout in second (default=5): ");
            try {
                simulator.setImplicitlyWaitSecond(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("timeout format wrong. use default ...");
            }
            System.out.print("Start Page (default=1): ");
            try {
                simulator.setStartPage(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("page format wrong. use default ...");
            }
            
            // set click order
            simulator.printSelectOrderDescription();
            out.print("\nPlease choose click order (default=0): ");
            try {
                simulator.setClickOrder(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("click order wrong. use default ...");
            }

            System.out.print("\nStart ...");
            simulator.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Bye!");
        }
    }
}
