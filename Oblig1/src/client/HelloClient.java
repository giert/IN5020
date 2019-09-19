package client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import HelloApp.Hello;
import HelloApp.HelloHelper;

public class HelloClient {

	static Hello helloImpl;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);

			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is 
			// part of the Interoperable naming Service.  
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// resolve the Object Reference in Naming
			String name = "Hello";
			helloImpl = HelloHelper.narrow(ncRef.resolve_str(name));
			
			String userID = "55874081c91a71d9f7a13cd9e9f1538e23874370";
			String songID = "SOPSOHT12A67AE0235";

			String message = helloImpl.getTimesPlayedByUser(userID + " " + songID);
			
			System.out.println("Message from Server: " + message);

		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}	
	}

}
