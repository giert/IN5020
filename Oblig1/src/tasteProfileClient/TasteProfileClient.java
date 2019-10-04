package tasteProfileClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import TasteProfile.Profiler;
import TasteProfile.ProfilerHelper;

public class TasteProfileClient {
	static TasteProfileClientHelper helper = new TasteProfileClientHelper();
	public static boolean userCaching;
	public static Profiler servant;
	public static PrintWriter writer;
	static String inputFile = "input.txt";
	static String outputFile = "output.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setup(args);
		process();
		cleanup();
	}
	
	private static void setup(String[] args) {
		establishConnection(args);
		processArgs(args);
		openFile(outputFile);
	}
	
	private static void establishConnection(String[] args) {
		try{
			ORB orb = ORB.init(args, null);
	
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	
			String name = "Profiler";
			servant = ProfilerHelper.narrow(ncRef.resolve_str(name));
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}	
	}

	private static void processArgs(String[] args){
		for(String arg : args){
			if (arg.equals("-noMemory")) userCaching = false;
		}
	}
	
	private static void openFile(String filename) {
		try {
			writer = new PrintWriter(outputFile, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}
	
	private static void process() {
		long start = System.currentTimeMillis();
		try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {
			stream.forEach((line) ->
	        {
	        	remoteCall(line.split("\\s"));
	        });
		} catch (IOException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
		long end = System.currentTimeMillis();
		System.out.println("Processing input took " + (end-start)/60000 + " minutes");
	}
	
	private static void remoteCall(String[] params) {
		switch (params[0])
		{
		case "getTimesPlayed":
			helper.getTimesPlayed(params[1]);
			break;
		case "getTimesPlayedByUser":
			helper.getTimesPlayedByUser(params[1], params[2]);
			break;
		case "getTopThreeUsersBySong":
			helper.getTopThreeUsersBySong(params[1]);
			break;
		case "getTopThreeSongsByUser":
			helper.getTopThreeSongsByUser(params[1]);
			break;
		default: 
            System.out.println("no match");
		}
	}
	
	private static void cleanup() {
		writer.close();
	}
}