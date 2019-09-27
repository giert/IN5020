package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import TasteProfile.ProfilerPOA;
import TasteProfile.SongProfile;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeUsers;
import TasteProfile.TopThreeUsersImpl;
import TasteProfile.UserCounter;
import TasteProfile.UserCounterImpl;
import TasteProfile.UserProfile;

public class TasteProfileServant extends ProfilerPOA {
	static String[] databases = {"train_triplets_1.txt", "train_triplets_2.txt"};
	
	static HashMap<String, SongProfile> songs = TasteProfileServer.songProfiles;
	static HashMap<String, UserProfile> users = TasteProfileServer.userProfiles;
	
	@Override
	public int getTimesPlayed(String song_id) {
		if(!songs.containsKey(song_id)) {
			cacheSong(song_id);
		}
		return songs.get(song_id).total_play_count;
	}

	@Override
	public int getTimesPlayedByUser(String user_id, String song_id) {
		if(users.containsKey(user_id)) {
			//return users.get(user_id).songs[song_id].songid_play_time;//search in songs for song_id
			return 1001;
		}else {
			return 1001; //cache miss
		}
	}

	@Override
	public TopThreeUsers getTopThreeUsersBySong(String song_id) {
		if(!songs.containsKey(song_id)) {
			cacheSong(song_id);
		}
		return songs.get(song_id).top_three_users;
	}

	@Override
	public TopThreeSongs getTopThreeSongsByUser(String user_id) {
		if(users.containsKey(user_id)) {
			return users.get(user_id).top_three_songs;
		}else {
			return null; //cache miss
		}
	}

	private static void cacheSong(String song_id){
		for (String database : databases) {
			processDatabase(database, song_id);
		}
	}

	private static void processDatabase(String database, String song_id) {
		try (Stream<String> stream = Files.lines(Paths.get(database))) {
			stream.forEach((line) ->
	        {
	        	String[] splitline = line.split("\\s");
	        	if(splitline[0].equals(song_id)) processLine(splitline);
	        });
		} catch (IOException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
		
	}
	
	private static void processLine(String[] splitline) {
		if (songs.putIfAbsent(splitline[0], new SongProfile() {}) == null) {
			songs.get(splitline[0]).top_three_users = new TopThreeUsersImpl() {};
			songs.get(splitline[0]).top_three_users.topThreeUsers = new UserCounterImpl[3];
		}
		
		songs.get(splitline[0]).total_play_count += Integer.parseInt(splitline[2]);
		for (int i = 0; i<3; i++) {
			if(songs.get(splitline[0]).top_three_users.topThreeUsers[i] == null) {
				songs.get(splitline[0]).top_three_users.topThreeUsers[i] = new UserCounterImpl() {};
			}
			if(songs.get(splitline[0]).top_three_users.topThreeUsers[i].songid_play_time < Integer.parseInt(splitline[2])) {
				songs.get(splitline[0]).top_three_users.topThreeUsers[i].user_id = splitline[1];
				songs.get(splitline[0]).top_three_users.topThreeUsers[i].songid_play_time = Integer.parseInt(splitline[2]);
			}
		}
		
		users.putIfAbsent(splitline[1], new UserProfile() {});
		users.get(splitline[1]).total_play_count += Integer.parseInt(splitline[2]);
		//users.get(splitline[1]).songs
	}
}
