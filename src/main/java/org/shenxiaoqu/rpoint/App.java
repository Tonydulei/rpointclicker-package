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
        	System.out.println("Facebook UserName (email): ");
        	facebookUser = in.nextLine();
        	System.out.println("Facebook password: ");
        	facebookPass = in.nextLine();
        	System.out.println("R UserName (email): ");
        	rakutenUser = in.nextLine();
        	System.out.println("R password: ");
        	rakutenPass = in.nextLine();
			RpointClicker r = new RpointClicker(facebookUser, facebookPass, rakutenUser, rakutenPass);
			r.getPoint();
		} catch (Exception e) {
			System.out.println("Bye!");
		}
    }
}
