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

import TasteProfile.Profiler;
import TasteProfile.ProfilerHelper;
import TasteProfile.SongCounter;

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
			HashMap<String,Song> songmap = new HashMap<String,Song>();
			startupCache(songmap);
			// wait for invocations from clients
			orb.run();
		} 

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("TasteProfileServer Exiting ...");	}

	private static void startupCache(HashMap<String,Song> songMap){
		int databaseNum = 0;
		ArrayList<String> databases = new ArrayList<String>();
		databases.add("train_triplets_1.txt");
		databases.add("train_triplets_2.txt");
		String fileName;
		String line = null;
		
		while(databases.size() > databaseNum){
			fileName = databases.get(databaseNum);
			
			System.out.println(fileName);
			
			try {
	            // FileReader reads text files in the default encoding.
				System.out.println("1");
	            FileReader fileReader = new FileReader(fileName);

	            System.out.println("2");
	            // Always wrap FileReader in BufferedReader.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {
	                System.out.println(line);
	            }   

	            // Always close files.
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "Unable to open file '" + 
	                fileName + "'");                
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file '" 
	                + fileName + "'");                  
	            // Or we could just do this: 
	            // ex.printStackTrace();
	        }
			
			
			databaseNum ++;
		}
		
	}
}

class Song{
	String id;
	public Song(String id){
		this.id = id;
		
	}
}

