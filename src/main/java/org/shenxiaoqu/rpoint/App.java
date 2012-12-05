package org.shenxiaoqu.rpoint;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String facebookUser, facebookPass, rakutenUser, rakutenPass;
        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Facebook UserName (email): ");
            facebookUser = in.nextLine();
            System.out.print("Facebook password: ");
            facebookPass = in.nextLine();
            System.out.print("R UserName (email): ");
            rakutenUser = in.nextLine();
            System.out.print("R password: ");
            rakutenPass = in.nextLine();

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
