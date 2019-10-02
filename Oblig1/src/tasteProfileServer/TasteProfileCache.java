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

    private static void firstPass(){
        for(String database : databases){
            try (Stream<String> stream = Files.lines(Paths.get(database))) {
                stream.forEach((line) ->
                {
                    //firstPassLine(line.split("  "));
                    firstPassLine(line.split("\\s+"));
                });
                stream.close();
            } catch (IOException e) {
                System.out.println("ERROR : " + e) ;
                e.printStackTrace(System.out);
            }
        }
    }
	
	private static void firstPassLine(String[] params) {
        String song_id = params[0];
        int plays = Integer.parseInt(params[2]);
        
		if (! songProfiles.containsKey(song_id)) makeSong(song_id);
        getSong(song_id).total_play_count += plays;
        
        userPopularity.putIfAbsent(params[1], 0);
		userPopularity.put(params[1], userPopularity.get(params[1]) + plays );
        
        shuffleTopUsers(0, song_id, params[1], plays);
    }

	private static void sortUsers(){
		Integer[] values = userPopularity.values().toArray(new Integer[0]);
		Arrays.sort(values, Collections.reverseOrder());
		
		Integer least = values[999];
		int leastcount = 0;
		while (values[999-leastcount] == least) leastcount++;
		
		for (HashMap.Entry<String, Integer> entry : userPopularity.entrySet()) {
			if (entry.getValue() >= least){
                makeUser(entry.getKey());
                getUser(entry.getKey()).total_play_count = entry.getValue();
                if (entry.getValue() == least && leastcount > 0) leastcount -= 1;
			}
		}
    }

    private static void secondPass(){
        for(String database : databases){
            try (Stream<String> stream = Files.lines(Paths.get(database))) {
                stream.forEach((line) ->
                {
                    secondPassLine(line.split("	"));
                    //songProfiles.get(rline.split("\\s+")[0]);
                });
                stream.close();
            } catch (IOException e) {
                System.out.println("ERROR : " + e) ;
                e.printStackTrace(System.out);
            }
        } 
    }

	private static void secondPassLine(String[] params){
		if(userProfiles.containsKey(params[1])){
            shuffleTopSongs(0, params[0], params[1], Integer.parseInt(params[2]));
        }
	}

    private static void shuffleTopUsers(int i, String song_id, String user_id, int plays){
    	if(i >= 3) {
    		return;
    	} else if(plays > getTopThreeUser(song_id, i).songid_play_time) {
            shuffleTopUsers(i+1, song_id, getTopThreeUser(song_id, i).user_id, getTopThreeUser(song_id, i).songid_play_time);
            getTopThreeUser(song_id, i).user_id = user_id;
            getTopThreeUser(song_id, i).songid_play_time = plays;
        } else {
            shuffleTopUsers(i+1, song_id, user_id, plays);
        }
    }

    private static void shuffleTopSongs(int i, String song_id, String user_id, int plays){
    	if(i >= 3) {
    		return;
    	} else if(plays > getTopThreeSong(user_id, i).songid_play_time){
            shuffleTopSongs(i+1, getTopThreeSong(user_id, i).song_id, user_id, plays);
            getTopThreeSong(user_id, i).song_id = song_id;
            getTopThreeSong(user_id, i).songid_play_time = plays;
        } else {
            shuffleTopSongs(i+1, song_id, user_id, plays);
        }
    }
    
    public static void makeUser(String user_id){
        userProfiles.put(user_id, new UserProfileImpl(){});
        getUser(user_id).user_id = user_id;
        getUser(user_id).top_three_songs = new TopThreeSongsImpl();
        getUser(user_id).top_three_songs.topThreeSongs = new SongCounterImpl[3];
		for (int i = 0; i<3; i++) setTopThreeSong(user_id, i, new SongCounterImpl() {});
    }
    
    public static void makeSong(String song_id){
        songProfiles.put(song_id, new SongProfileImpl() {});
        getSong(song_id).top_three_users = new TopThreeUsersImpl() {};
        getSong(song_id).top_three_users.topThreeUsers = new UserCounterImpl[3];
		for (int i = 0; i<3; i++) setTopThreeUser(song_id, i, new UserCounterImpl() {});
    }

    public static UserProfile getUser(String user_id){
        return userProfiles.get(user_id);
    }

    public static SongProfile getSong(String song_id){
        return songProfiles.get(song_id);
    }

    public static void setTopThreeUser(String song_id, int position, UserCounter user){
        getSong(song_id).top_three_users.topThreeUsers[position] = user;
    }

    public static void setTopThreeSong(String user_id, int position, SongCounter song){
        getUser(user_id).top_three_songs.topThreeSongs[position] = song;
    }

    public static UserCounter getTopThreeUser(String song_id, int position){
        return getSong(song_id).top_three_users.topThreeUsers[position];
    }

    public static SongCounter getTopThreeSong(String user_id, int position){
        return getUser(user_id).top_three_songs.topThreeSongs[position];
    }
    
    private static void putCache() {
    	TasteProfileServant.databases = databases;
    	TasteProfileServant.songProfiles = songProfiles;
    	TasteProfileServant.userProfiles = userProfiles;
    }
}
