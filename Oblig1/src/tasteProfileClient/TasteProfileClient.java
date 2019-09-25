package tasteProfileClient;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import TasteProfile.Profiler;
import TasteProfile.ProfilerHelper;

public class TasteProfileClient {

	static Profiler servant;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		establishConnection(args);
		processInput();
	}
	
	private static void establishConnection(String[] args) {
		try{
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);
	
			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is 
			// part of the Interoperable naming Service.  
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	
			// resolve the Object Reference in Naming
			String name = "Profiler";
			servant = ProfilerHelper.narrow(ncRef.resolve_str(name));
			
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}	
	}
	
	private static void processInput() {
		//String response;
		
		String user_id = "55874081c91a71d9f7a13cd9e9f1538e23874370";
		String song_id = "SOPSOHT12A67AE0235";

		System.out.println(servant.getTimesPlayed(song_id));
		System.out.println(servant.getTimesPlayedByUser(user_id, song_id));
		System.out.println(servant.getTopThreeUsersBySong(song_id));
		System.out.println(servant.getTopThreeSongsByUser(user_id));
	}

}
