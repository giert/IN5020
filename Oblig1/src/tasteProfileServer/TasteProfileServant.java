package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import TasteProfile.ProfilerPOA;
import TasteProfile.SongCounter;
import TasteProfile.SongCounterImpl;
import TasteProfile.SongProfile;
import TasteProfile.SongProfileImpl;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeSongsImpl;
import TasteProfile.TopThreeUsers;
import TasteProfile.TopThreeUsersImpl;
import TasteProfile.UserCounter;
import TasteProfile.UserCounterImpl;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

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
		if(!users.containsKey(user_id)) {
			cacheUser(user_id);
		}
		if(users.get(user_id).songs == null) {
			cacheUser(user_id);
		}
		for (SongCounter song : users.get(user_id).songs) {
			if(song.song_id.equals(song_id)) {
				return song.songid_play_time;
			}
		}
		return 0;
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
		if(!users.containsKey(user_id)) {
			cacheUser(user_id);
		}
		return users.get(user_id).top_three_songs;
	}

	private static void cacheSong(String song_id){
		for (String database : databases) {
			processDatabase(database, 0, song_id);
		}
	}

	private static void cacheUser(String user_id){
		for (String database : databases) {
			processDatabase(database, 1, user_id);
		}
	}

	private static void processDatabase(String database, int type, String target) {
		try (Stream<String> stream = Files.lines(Paths.get(database))) {
			stream.forEach((line) ->
	        {
	        	String[] splitline = line.split("\\s");
	        	if(splitline[type].equals(target)) processLine(splitline);
	        });
		} catch (IOException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}
	
	private static void processLine(String[] splitline) {
		if (songs.putIfAbsent(splitline[0], new SongProfileImpl() {}) == null) {
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
		
		
		if (users.putIfAbsent(splitline[1], new UserProfileImpl() {}) == null) {
			users.get(splitline[1]).top_three_songs = new TopThreeSongsImpl() {};
			users.get(splitline[1]).top_three_songs.topThreeSongs = new SongCounterImpl[3];
		}
		
		users.get(splitline[1]).total_play_count += Integer.parseInt(splitline[2]);

		for (int i = 0; i<3; i++) {
			if(users.get(splitline[1]).top_three_songs.topThreeSongs[i] == null) {
				users.get(splitline[1]).top_three_songs.topThreeSongs[i] = new SongCounterImpl() {};
			}
			if(users.get(splitline[1]).top_three_songs.topThreeSongs[i].songid_play_time < Integer.parseInt(splitline[2])) {
				users.get(splitline[1]).top_three_songs.topThreeSongs[i].song_id = splitline[1];
				users.get(splitline[1]).top_three_songs.topThreeSongs[i].songid_play_time = Integer.parseInt(splitline[2]);
			}
		}
		
		if (users.get(splitline[1]).songs == null) {
			users.get(splitline[1]).songs = new SongCounterImpl[] {};
		}
		users.get(splitline[1]).songs = Arrays.copyOf(users.get(splitline[1]).songs, users.get(splitline[1]).songs.length + 1);
		users.get(splitline[1]).songs[users.get(splitline[1]).songs.length - 1] = new SongCounterImpl() {};
		users.get(splitline[1]).songs[users.get(splitline[1]).songs.length - 1].song_id = splitline[0];
		users.get(splitline[1]).songs[users.get(splitline[1]).songs.length - 1].songid_play_time = Integer.parseInt(splitline[2]);
	}
}
