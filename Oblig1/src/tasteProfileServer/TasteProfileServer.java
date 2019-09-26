package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import TasteProfile.Profiler;
import TasteProfile.ProfilerHelper;

public class TasteProfileServer {

	static String[] databases = {"train_triplets_1.txt", "train_triplets_2.txt"};
	static HashMap<String,Song> songmap = new HashMap<String,Song>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);

			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			TasteProfileServant servant = new TasteProfileServant();

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
			Profiler href = ProfilerHelper.narrow(ref);

			// get the root naming context
			org.omg.CORBA.Object objRef =
					orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			String name = "Profiler";
			NameComponent path[] = ncRef.to_name( name );
			ncRef.rebind(path, href);

			startupCache();
			
			System.out.println("TasteProfileServer ready and waiting ...");
			
			// wait for invocations from clients
			orb.run();
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("TasteProfileServer Exiting ...");
	}


	private static void startupCache(){
		for (String database : databases) {
			processDatabase(database);
		}
	}

	private static void processDatabase(String database) {
		try (Stream<String> stream = Files.lines(Paths.get(database))) {
			stream.forEach((line) ->
	        {
	        	processLine(line);
	        });
		} catch (IOException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
		
	}
	
	private static void processLine(String line) {
		System.out.println(line);
		//Do shit
	}

	class Song{
		String id;
		long plays;
		public Song(String id){
				this.id = id;
		}
	}
}