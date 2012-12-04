package org.shenxiaoqu.rpoint;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {

	public static void main( String[] args )
    {
		String facebookUser,facebookPass,rakutenUser,rakutenPass;
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
        	System.out.print("Set Timeout in second (default=5): ");
        	int timeout = 5;
        	try {
        		timeout = Integer.parseInt(in.nextLine());
        	} catch (NumberFormatException e) {
        		System.out.println("timeout format wrong.");
        	}
			RpointClicker r = new RpointClicker(facebookUser, facebookPass, rakutenUser, rakutenPass, timeout);
			r.getPoint();
		} catch (Exception e) {
			System.out.println("Bye!");
		}
    }
}
