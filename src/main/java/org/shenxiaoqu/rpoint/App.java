package org.shenxiaoqu.rpoint;

import java.io.Console;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String facebookUser, facebookPass, rakutenUser, rakutenPass;
        try {
        	
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

            RpointClicker r = new RpointClicker(facebookUser, facebookPass, rakutenUser, rakutenPass);

            System.out.print("Set Timeout in second (default=5): ");
            try {
                r.setTimeoutSecond(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("timeout format wrong. use default ...");
            }
            System.out.print("Start Page (default=1): ");
            try {
                r.setStartPage(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("page format wrong. use default ...");
            }

            r.getPoint();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Bye!");
        }
    }
}
