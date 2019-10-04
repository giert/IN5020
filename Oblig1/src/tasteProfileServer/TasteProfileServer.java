package tasteProfileServer;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import TasteProfile.*;

public class TasteProfileServer {
	private static boolean startupcache = true;

	/**
	 * @param args
	 * 
	 */
	
	public static void main(String[] args) {
		for(String arg : args){
			if (arg.equals("-noMemory")) startupcache = false;
		}

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
			
			//Cache on startup
			if(startupcache){
				long start = System.currentTimeMillis();
				TasteProfileCache.startupCache();
				long end = System.currentTimeMillis();
				System.out.println("Caching took " + (end-start)/1000 + " seconds");
			}

			// wait for invocations from clients
			System.out.println("TasteProfileServer ready and waiting ...");
			orb.run();
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("TasteProfileServer Exiting ...");
	}
}