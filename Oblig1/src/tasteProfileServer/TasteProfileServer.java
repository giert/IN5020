package tasteProfileServer;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
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
	 * 
	 */

	public static HashMap<String,SongProfile> songProfiles = new HashMap<String,SongProfile>();
	public static HashMap<String,Integer> userPopularity = new HashMap<String, Integer>();
	public static HashMap<String, UserProfile> userProfiles = new HashMap<String, UserProfile>();
	
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
			
			//startupCache(); DISABLED STARTUP CACHE

			// wait for invocations from clients
			System.out.println("TasteProfileServer ready and waiting ...");
			orb.run();
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("TasteProfileServer Exiting ...");	}

	private static void startupCache()
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
		        	lineCall(rline.split("	"));
		        });
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
			
			
			databaseNum ++;
		}
		databaseNum = 0;
		
		//System.out.println("second pass");
		
		//while(databases.size() > databaseNum){
		//	fileName = databases.get(databaseNum);
		//	// build list of top songs by user	
		//	try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
		//		stream.forEach((rline) ->
		//        {
		//        	userCall(rline.split("	"));
		//        });
		//	} catch (IOException e) {
		//		System.out.println("ERROR : " + e) ;
		//		e.printStackTrace(System.out);
		//	}	
		//databaseNum ++;
		//}
		
		//sort most popular users 
		sortUsers();
		
		System.out.println("chache complete");
		
	}
	private static void userCall(String[] params){
		if(userProfiles.containsKey(params[1])){
			int value = Integer.parseInt(params[2]);
			if(userProfiles.get(params[1]).top_three_songs.topThreeSongs[0].songid_play_time < value){
				userProfiles.get(params[1]).top_three_songs.topThreeSongs[0].songid_play_time = value;
				return;
			}
			else if(userProfiles.get(params[1]).top_three_songs.topThreeSongs[1].songid_play_time < value){
				userProfiles.get(params[1]).top_three_songs.topThreeSongs[1].songid_play_time = value;
				return;
			}
			else if(userProfiles.get(params[1]).top_three_songs.topThreeSongs[2].songid_play_time < value){
				userProfiles.get(params[1]).top_three_songs.topThreeSongs[2].songid_play_time = value;
				return;
			}
		}		
	}
	
	private static void sortUsers(){
		System.out.println("user sort");
		Integer[] values = userPopularity.values().toArray(new Integer[0]);
		Integer[] importantValues = new Integer[1000];
		Arrays.sort(values, Collections.reverseOrder());
		System.arraycopy(values, 0, importantValues, 0, 1000);
		
		Integer least = importantValues[999];
		int leastcount = 0;
		while(importantValues[999-leastcount] == least){
			leastcount++;
		}
		System.out.println("least " + least + " greatest " + importantValues[0]);
		
		for (HashMap.Entry<String, Integer> entry : userPopularity.entrySet()) {
			
			if(entry.getValue() == least && leastcount > 0){
				userProfiles.put(entry.getKey(), new UserProfile(){});
				userProfiles.get(entry.getKey()).user_id = entry.getKey();
				userProfiles.get(entry.getKey()).total_play_count = entry.getValue();

				leastcount = leastcount - 1;
				System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());

			}
			else if(entry.getValue() > least){
				userProfiles.put(entry.getKey(), new UserProfile(){});
				userProfiles.get(entry.getKey()).user_id = entry.getKey();
				userProfiles.get(entry.getKey()).total_play_count = entry.getValue();
				System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());


			}
		}
	
		
	}
	
	private static void lineCall(String[] params) {
		if (songProfiles.putIfAbsent(params[0], new SongProfileImpl() {}) == null) {
			songProfiles.get(params[0]).top_three_users = new TopThreeUsersImpl() {};
			songProfiles.get(params[0]).top_three_users.topThreeUsers = new UserCounterImpl[3];
		}
		
		if(! userPopularity.containsKey(params[1])){
			userPopularity.put(params[1], 0);
		}
		
		//System.out.println(params[0] + " " + params[2]);
		songProfiles.get(params[0]).total_play_count += Integer.parseInt(params[2]);
		//System.out.print(userPopularity.get(params[1]));
		userPopularity.put(params[1],userPopularity.get(params[1]) + Integer.parseInt(params[2]));
		//System.out.println("->" + userPopularity.get(params[1]));
		

		for (int i = 0; i<3; i++) {
			if(songProfiles.get(params[0]).top_three_users.topThreeUsers[i] == null) {
				songProfiles.get(params[0]).top_three_users.topThreeUsers[i] = new UserCounterImpl() {};
			}
			if(songProfiles.get(params[0]).top_three_users.topThreeUsers[i].songid_play_time < Integer.parseInt(params[2])) {
				songProfiles.get(params[0]).top_three_users.topThreeUsers[i].user_id = params[1];
				songProfiles.get(params[0]).top_three_users.topThreeUsers[i].songid_play_time = Integer.parseInt(params[2]);
			}
		}
	}
}