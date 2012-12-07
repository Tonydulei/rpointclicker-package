package org.shenxiaoqu.clicker.point;
import org.shenxiaoqu.clicker.driver.RpointCampainDriver;

import java.io.Console;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String facebookUser="2", facebookPass="2", rakutenUser="2", rakutenPass="2";
        try {
        	
        	
        	
        	
            Scanner in = new Scanner(System.in);
            /*System.out.print("Facebook UserName (email): ");
            facebookUser = in.nextLine();
            char fPasswordArray[] = console.readPassword("Facebook password: ");
            facebookPass = new String(fPasswordArray);
            System.out.print("R UserName (email): ");
            rakutenUser = in.nextLine();
            char rPasswordArray[] = console.readPassword("R password: ");
            rakutenPass = new String(rPasswordArray);*/

            RpointCampainDriver r = new RpointCampainDriver(facebookUser, facebookPass, rakutenUser, rakutenPass);

            // set click condition.
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
            
            // set click order
            System.out.print("\n-------------------------");
            System.out.print("\n--- 0. new arival");
            System.out.print("\n--- 1. deadline");
            System.out.print("\n--- 2. old arival");
            System.out.print("\n--- 3. point");
            System.out.print("\n--- 4. number of winers");
            System.out.print("\n-------------------------");
            System.out.print("\nPlease choose click order (default=0): ");
            try {
                r.setClickOrder(Integer.parseInt(in.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("click order wrong. use default ...");
            }

            System.out.print("\nStart ...");
            r.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Bye!");
        }
    }
}
