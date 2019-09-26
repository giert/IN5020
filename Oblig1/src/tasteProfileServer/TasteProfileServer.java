package tasteProfileServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import TasteProfile.*;

public class TasteProfileServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
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

			
			System.out.println("go");
			// get the root naming context
			org.omg.CORBA.Object objRef =
					orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			System.out.println("go");
			// bind the Object Reference in Naming
			String name = "Profiler";
			NameComponent path[] = ncRef.to_name( name );
			ncRef.rebind(path, href);

			System.out.println("TasteProfileServer ready and waiting ...");
			
			
			HashMap<String,Song> songProfiles = new HashMap<String,Song>();
			HashMap<String,Integer> userPopularity = new HashMap<String, Integer>();
			HashMap<String, User> userProfiles = new HashMap<String, User>();
			
			
			startupCache(songProfiles, userPopularity, userProfiles);
			// wait for invocations from clients
			orb.run();
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("TasteProfileServer Exiting ...");	}

	private static void startupCache(
			HashMap<String,Song> songProfiles, 
			HashMap<String,Integer> userPopularity,
			HashMap<String, User> userProfiles)
		{
		int databaseNum = 0;
		ArrayList<String> databases = new ArrayList<String>();
		databases.add("train_triplets_1.txt");
		databases.add("train_triplets_2.txt");
		String fileName;
		String line = null;
		
		while(databases.size() > databaseNum){
			fileName = databases.get(databaseNum);
			
			System.out.println(fileName);
			
			try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
				stream.forEach((rline) ->
		        {
		        	lineCall(rline.split("	"),songProfiles, userPopularity, userProfiles);
		        });
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
			
			
			databaseNum ++;
		}
		System.out.println("chache complete");
		
	}
	
	private static void lineCall(String[] params,
			HashMap<String,Song> songProfiles, 
			HashMap<String,Integer> userPopularity,
			HashMap<String, User> userProfiles) {
		if(! songProfiles.containsKey(params[0])){
			songProfiles.put(params[0], new Song());
			
		}
		if(! userPopularity.containsKey(params[1])){
			userPopularity.put(params[1], 0);
		}
		//System.out.println(params[0] + " " + params[2]);
		songProfiles.get(params[0]).total_play_count += Integer.parseInt(params[2]);
		//System.out.print(userPopularity.get(params[1]));
		userPopularity.put(params[1],userPopularity.get(params[1]) + Integer.parseInt(params[2]));
		//System.out.println("->" + userPopularity.get(params[1]));
	}
}

class Song extends SongProfile{
	int total_play_count;
	public Song(){
		this.total_play_count = 0;
	}
}

class User extends UserProfile{
	public User(String id){
		this.total_play_count = 0;
		this.user_id = id;
	}
}
