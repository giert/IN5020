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

	static Profiler servant;
	static PrintWriter writer;
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
		openFile(outputFile);
	}
	
	private static void establishConnection(String[] args) {
		try{
			String name = "Profiler";
			ORB orb = ORB.init(args, null);
	
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			servant = ProfilerHelper.narrow(ncRef.resolve_str(name));
		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
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
		try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {
			stream.forEach((line) ->
	        {
	        	remoteCall(line.split("\\s"));
	        });
		} catch (IOException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}
	
	private static void remoteCall(String[] params) {
		long start, finish;
		switch (params[0])
		{
		case "getTimesPlayed":
			start = System.nanoTime();
			int response1 = servant.getTimesPlayed(params[1]);
			finish = System.nanoTime();
			printOutput(String.format("Song %s played %d times. (%d ms)", params[1], response1, finish - start));
			break;
		case "getTimesPlayedByUser":
			start = System.nanoTime();
			int response2 = servant.getTimesPlayedByUser(params[1], params[2]);
			finish = System.nanoTime();
			printOutput(String.format("Song %s played %d times by user %s. (%d ms)", params[1], response2, params[2], finish - start));
			break;
		case "getTopThreeUsersBySong":
			start = System.nanoTime();
			TopThreeUsers response3 = servant.getTopThreeUsersBySong(params[1]);
			finish = System.nanoTime();
			printOutput(String.format("Song %s played most by users %s. (%d ms)", params[1], response3, finish - start));
			break;
		case "getTopThreeSongsByUser":
			start = System.nanoTime();
			String response4 = servant.getTopThreeSongsByUser(params[1]);
			finish = System.nanoTime();
			printOutput(String.format("User %s has songs %s as top songs. (%d ms)", params[1], response4, finish - start));
			break;
		default: 
            System.out.println("no match");
		}
	}
	
	private static void printOutput(String output) {
		System.out.println(output);
		writer.println(output);
	}
	
	private static void cleanup() {
		writer.close();
	}
}
