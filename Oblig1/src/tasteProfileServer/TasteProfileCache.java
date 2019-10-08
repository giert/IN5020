package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

import TasteProfile.SongCounter;
import TasteProfile.SongCounterImpl;
import TasteProfile.SongProfile;
import TasteProfile.SongProfileImpl;
import TasteProfile.TopThreeSongsImpl;
import TasteProfile.TopThreeUsersImpl;
import TasteProfile.UserCounter;
import TasteProfile.UserCounterImpl;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

public class TasteProfileCache {
    public static String[] databases = {"train_triplets_1.txt", "train_triplets_2.txt"};

	public static HashMap<String,SongProfile> songProfiles = new HashMap<String,SongProfile>();
	public static HashMap<String,Integer> userPopularity = new HashMap<String, Integer>();
	public static HashMap<String, UserProfile> userProfiles = new HashMap<String, UserProfile>();

    //startupCache organizes and calls the neccesary methods to perform the cahcing, it also reports on the progress.
	public static void startupCache(){
		System.out.println("Starting first pass...");
        firstPass();
		System.out.println("Sorting users...");
        sortUsers();
		System.out.println("Starting second pass...");
        secondPass();
		System.out.println("Cache complete!");
		putCache();
    }
	
	//firstPass runs the first pass through the databases
    private static void firstPass(){
        for(String database : databases){
        	//reads every line of the file with file streamer
            try (Stream<String> stream = Files.lines(Paths.get(database))) {
                stream.forEach((line) ->
                {
                    firstPassLine(line.split("\\s+"));
                });
                stream.close();
            } catch (IOException e) {
                System.out.println("ERROR : " + e) ;
                e.printStackTrace(System.out);
            }
        }
    }
    
    //firstPassLine is a helper that is called on every line in the databases to make song profiles and get all users
	private static void firstPassLine(String[] params) {
        String song_id = params[0];
        int plays = Integer.parseInt(params[2]);
        
        //check if the cache has the song, if not make a new song profile object and insert it
		if (! songProfiles.containsKey(song_id)) makeSong(song_id);
        getSong(song_id).total_play_count += plays;
        
        //insert a user into the popularity map, which is used to find the top 1000 users
        userPopularity.putIfAbsent(params[1], 0);
		userPopularity.put(params[1], userPopularity.get(params[1]) + plays );
        
		//check if the user in the current line is one of the top three users for the current song
        shuffleTopUsers(song_id, params[1], plays);
    }

	//sortUsers finds the top 1000 users
	private static void sortUsers(){
		//make an array of the popularity map and sort in reverse order
		Integer[] values = userPopularity.values().toArray(new Integer[0]);
		Arrays.sort(values, Collections.reverseOrder());
		
		//the smallest popularity is in the 1000th spot in the array
		Integer least = values[999];
		int leastcount = 0; //to keep track of how many shares the smallest value
		while (values[999-leastcount] == least) leastcount++;
		
		//go through the entire user popularity map and find every user with a popularity larger than the smallest of the top 1000
		//we have to limit the number of smallest popularity to keep the stored users to 1000 and not more
		for (HashMap.Entry<String, Integer> entry : userPopularity.entrySet()) {
			if (entry.getValue() >= least){
                makeUser(entry.getKey());
                getUser(entry.getKey()).total_play_count = entry.getValue();
                if (entry.getValue() == least && leastcount > 0) leastcount -= 1;
			}
		}
    }

	//the second pass fills in the songs list and the top three songs for the top 1000 users
    private static void secondPass(){
        for(String database : databases){
            try (Stream<String> stream = Files.lines(Paths.get(database))) {
                stream.forEach((line) ->
                {
                    secondPassLine(line.split("\\s+"));
                });
                stream.close();
            } catch (IOException e) {
                System.out.println("ERROR : " + e) ;
                e.printStackTrace(System.out);
            }
        } 
    }

    //secondPassLine is a helper for the second pass that is called on each line. If the given user is one of top 1000, it will
    //add each song to its array of song plays, and checks the song against each user's top three songs,
    //to see if this song has more views
	private static void secondPassLine(String[] params){
		if(userProfiles.containsKey(params[1])){
            addSongCounter(params[0], params[1], Integer.parseInt(params[2]));
            shuffleTopSongs(params[0], params[1], Integer.parseInt(params[2]));
        }
    }
    
	//addSongCounter adds current song to the array of songs a user has played.
    private static void addSongCounter(String song_id, String user_id, int plays){
        int length = getUser(user_id).songs.length;
        getUser(user_id).songs = Arrays.copyOf(getUser(user_id).songs, length + 1);
        getUser(user_id).songs[length] = new SongCounterImpl(){};
        getUser(user_id).songs[length].song_id = song_id;
        getUser(user_id).songs[length].songid_play_time = plays;
    }
    
    //same as addSongCounter but applied to object user
    public static void addSongCounterUser(String song_id, UserProfile user, int plays){
        int length = user.songs.length;
        user.songs = Arrays.copyOf(user.songs, length + 1);
        user.songs[length] = new SongCounterImpl(){};
        user.songs[length].song_id = song_id;
        user.songs[length].songid_play_time = plays;
    }

    //shuffleTopUsers sorts the top users of a given song
    //is implemented recursively for compactness
    private static void shuffleTopUsers(String song_id, String user_id, int plays){
        shuffleTopUsers(2, song_id, user_id, plays);
    }
    private static void shuffleTopUsers(int i, String song_id, String user_id, int plays){
    	if(i < 0) {
    		return;
    	} else if(plays > getTopThreeUser(song_id, i).songid_play_time) {
            shuffleTopUsers(i-1, song_id, getTopThreeUser(song_id, i).user_id, getTopThreeUser(song_id, i).songid_play_time);
            getTopThreeUser(song_id, i).user_id = user_id;
            getTopThreeUser(song_id, i).songid_play_time = plays;
        } else {
            shuffleTopUsers(i-1, song_id, user_id, plays);
        }
    }

    //shuffleTopSongs sorts the top songs of a given user
    //is implemented recursively for compactness
    private static void shuffleTopSongs(String song_id, String user_id, int plays){
        shuffleTopSongs(2, song_id, user_id, plays);
    }
    private static void shuffleTopSongs(int i, String song_id, String user_id, int plays){
    	if(i < 0) {
    		return;
    	} else if(plays > getTopThreeSong(user_id, i).songid_play_time){
            shuffleTopSongs(i-1, getTopThreeSong(user_id, i).song_id, user_id, getTopThreeSong(user_id, i).songid_play_time);
            getTopThreeSong(user_id, i).song_id = song_id;
            getTopThreeSong(user_id, i).songid_play_time = plays;
        } else {
            shuffleTopSongs(i-1, song_id, user_id, plays);
        }
    }
    
    //makeUser creates a new instance of a userprofileimpl, and  initilizes all all the values,
    //this could have been done in a constructor, but fits just as well here
    public static void makeUser(String user_id){
        userProfiles.put(user_id, new UserProfileImpl());
        getUser(user_id).user_id = user_id;
        getUser(user_id).songs = new SongCounterImpl[0];
        getUser(user_id).top_three_songs = new TopThreeSongsImpl();
        getUser(user_id).top_three_songs.topThreeSongs = new SongCounterImpl[3];
		for (int i = 0; i<3; i++) setTopThreeSong(user_id, i, new SongCounterImpl());
    }
    
    //makeSong creates a new instance of a songprofileimpl, and initilizes all the values,
    public static void makeSong(String song_id){
        songProfiles.put(song_id, new SongProfileImpl());
        getSong(song_id).top_three_users = new TopThreeUsersImpl();
        getSong(song_id).top_three_users.topThreeUsers = new UserCounterImpl[3];
		for (int i = 0; i<3; i++) setTopThreeUser(song_id, i, new UserCounterImpl());
    }

    
    //basic get function
    public static UserProfile getUser(String user_id){
        return userProfiles.get(user_id);
    }

    //basic get function
    public static SongProfile getSong(String song_id){
        return songProfiles.get(song_id);
    }

    //basic get function
    public static void setTopThreeUser(String song_id, int position, UserCounter user){
        getSong(song_id).top_three_users.topThreeUsers[position] = user;
    }

    //basic get function
    public static void setTopThreeSong(String user_id, int position, SongCounter song){
        getUser(user_id).top_three_songs.topThreeSongs[position] = song;
    }

    //basic get function
    public static UserCounter getTopThreeUser(String song_id, int position){
        return getSong(song_id).top_three_users.topThreeUsers[position];
    }

    //basic get function
    public static SongCounter getTopThreeSong(String user_id, int position){
        return getUser(user_id).top_three_songs.topThreeSongs[position];
    }
    
    //inserts the cache maps into the map variables in the servant
    private static void putCache() {
    	TasteProfileServant.songProfiles = songProfiles;
    	TasteProfileServant.userProfiles = userProfiles;
    }
}
