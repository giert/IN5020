package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
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
    public static String[] databases;
	public static HashMap<String,SongProfile> songProfiles;
	public static HashMap<String, UserProfile> userProfiles;
	
	@Override
	public int getTimesPlayed(String song_id) {
		delay();
		if(!songProfiles.containsKey(song_id)) {
			cacheSong(song_id);
		}
		return songProfiles.get(song_id).total_play_count;
	}

	@Override
	public int getTimesPlayedByUser(String user_id, String song_id) {
		delay();
		if(!userProfiles.containsKey(user_id)) {
			cacheUser(user_id);
		}
		if(userProfiles.get(user_id).songs == null) {
			cacheUser(user_id);
		}
		for (SongCounter song : userProfiles.get(user_id).songs) {
			if(song.song_id.equals(song_id)) {
				return song.songid_play_time;
			}
		}
		return 0;
	}

	@Override
	public TopThreeUsers getTopThreeUsersBySong(String song_id) {
		delay();
		if(!songProfiles.containsKey(song_id)) {
			cacheSong(song_id);
		}
		return songProfiles.get(song_id).top_three_users;
	}

	@Override
	public TopThreeSongs getTopThreeSongsByUser(String user_id) {
		delay();
		if(!userProfiles.containsKey(user_id)) {
			cacheUser(user_id);
		}
		return userProfiles.get(user_id).top_three_songs;
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
		if (songProfiles.putIfAbsent(splitline[0], new SongProfileImpl() {}) == null) {
			songProfiles.get(splitline[0]).top_three_users = new TopThreeUsersImpl() {};
			songProfiles.get(splitline[0]).top_three_users.topThreeUsers = new UserCounterImpl[3];
		}
		
		songProfiles.get(splitline[0]).total_play_count += Integer.parseInt(splitline[2]);
		
		for (int i = 0; i<3; i++) {
			if(songProfiles.get(splitline[0]).top_three_users.topThreeUsers[i] == null) {
				songProfiles.get(splitline[0]).top_three_users.topThreeUsers[i] = new UserCounterImpl() {};
			}
			if(songProfiles.get(splitline[0]).top_three_users.topThreeUsers[i].songid_play_time < Integer.parseInt(splitline[2])) {
				songProfiles.get(splitline[0]).top_three_users.topThreeUsers[i].user_id = splitline[1];
				songProfiles.get(splitline[0]).top_three_users.topThreeUsers[i].songid_play_time = Integer.parseInt(splitline[2]);
			}
		}
		
		
		if (userProfiles.putIfAbsent(splitline[1], new UserProfileImpl() {}) == null) {
			userProfiles.get(splitline[1]).top_three_songs = new TopThreeSongsImpl() {};
			userProfiles.get(splitline[1]).top_three_songs.topThreeSongs = new SongCounterImpl[3];
		}
		
		userProfiles.get(splitline[1]).total_play_count += Integer.parseInt(splitline[2]);

		for (int i = 0; i<3; i++) {
			if(userProfiles.get(splitline[1]).top_three_songs.topThreeSongs[i] == null) {
				userProfiles.get(splitline[1]).top_three_songs.topThreeSongs[i] = new SongCounterImpl() {};
			}
			if(userProfiles.get(splitline[1]).top_three_songs.topThreeSongs[i].songid_play_time < Integer.parseInt(splitline[2])) {
				userProfiles.get(splitline[1]).top_three_songs.topThreeSongs[i].song_id = splitline[1];
				userProfiles.get(splitline[1]).top_three_songs.topThreeSongs[i].songid_play_time = Integer.parseInt(splitline[2]);
			}
		}
		
		if (userProfiles.get(splitline[1]).songs == null) {
			userProfiles.get(splitline[1]).songs = new SongCounterImpl[] {};
		}
		userProfiles.get(splitline[1]).songs = Arrays.copyOf(userProfiles.get(splitline[1]).songs, userProfiles.get(splitline[1]).songs.length + 1);
		userProfiles.get(splitline[1]).songs[userProfiles.get(splitline[1]).songs.length - 1] = new SongCounterImpl() {};
		userProfiles.get(splitline[1]).songs[userProfiles.get(splitline[1]).songs.length - 1].song_id = splitline[0];
		userProfiles.get(splitline[1]).songs[userProfiles.get(splitline[1]).songs.length - 1].songid_play_time = Integer.parseInt(splitline[2]);
	}
	
	private static void delay() {
		try {
			TimeUnit.MILLISECONDS.sleep(80);
		} catch (InterruptedException e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}
}
