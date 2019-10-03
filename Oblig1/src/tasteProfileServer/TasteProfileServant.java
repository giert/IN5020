package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import TasteProfile.ProfilerPOA;
import TasteProfile.SongCounter;
import TasteProfile.SongCounterImpl;
import TasteProfile.SongProfile;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeSongsImpl;
import TasteProfile.TopThreeUsers;
import TasteProfile.TopThreeUsersImpl;
import TasteProfile.UserCounterImpl;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

public class TasteProfileServant extends ProfilerPOA {
	private static int result;
	private static UserProfile userProfile;
	private static TopThreeSongsImpl topSongs;
	private static TopThreeUsersImpl topUsers;

	//
    public static String[] databases = {"train_triplets_1.txt", "train_triplets_2.txt"};
	public static HashMap<String,SongProfile> songProfiles = new HashMap<String,SongProfile>();
	public static HashMap<String, UserProfile> userProfiles = new HashMap<String, UserProfile>();
	//
	
    //public static String[] databases;
	//public static HashMap<String,SongProfile> songProfiles;
	//public static HashMap<String, UserProfile> userProfiles;
	
	@Override
	public int getTimesPlayed(String song_id) {
		delay();
		if(songProfiles.containsKey(song_id)) {
			return songProfiles.get(song_id).total_play_count;
		}else{
			getSongFromDB(song_id);
			return result;
		}
	}

	@Override
	public int getTimesPlayedByUser(String user_id, String song_id) {
		delay();
		if(userProfiles.containsKey(user_id)) {
			for (SongCounter song : userProfiles.get(user_id).songs) {
				if(song.song_id.equals(song_id)) {
					return song.songid_play_time;
				}
			}
		}else{
			getUserPlaysFromDB(song_id, user_id);
			return result;
		}
		return 0;
	}

	@Override
	public TopThreeUsers getTopThreeUsersBySong(String song_id) {
		delay();
		if(songProfiles.containsKey(song_id)) {
			return songProfiles.get(song_id).top_three_users;
		}else{
			getTopThreeUsersFromDB(song_id);
			return topUsers;
		}
	}

	@Override
	public TopThreeSongs getTopThreeSongsByUser(String user_id) {
		delay();
		if(userProfiles.containsKey(user_id)) {
			return userProfiles.get(user_id).top_three_songs;
		}else{
			getTopThreeSongsFromDB(user_id);
			return topSongs;
		}
	}

	@Override
	public UserProfile getUserProfile(String user_id) {
		delay();
		if(userProfiles.containsKey(user_id)) {
			return userProfiles.get(user_id);
		}else{
			getUserFromDB(user_id);
			return userProfile;
		}
	}

	private static void getSongFromDB(String song_id){
		result = 0;
		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[0].equals(song_id)) result += Integer.parseInt(splitline[2]);
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
		}
	}

	private static void getUserFromDB(String user_id){
		userProfile = new UserProfileImpl() {};
		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[1].equals(user_id)){
						//HERE
					};
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
		}
	}

	private static void getUserPlaysFromDB(String song_id, String user_id){
		result = 0;
		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[0].equals(song_id) && splitline[1].equals(user_id))
						result += Integer.parseInt(splitline[2]);
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
		}
	}

	private static void getTopThreeUsersFromDB(String song_id){
		topUsers = new TopThreeUsersImpl();
        topUsers.topThreeUsers = new UserCounterImpl[3];
		for (int i = 0; i<3; i++) topUsers.topThreeUsers[i] = new UserCounterImpl();

		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[0].equals(song_id))
						getTopThreeUsersFromDBHelper(0, splitline[1], Integer.parseInt(splitline[2]));
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
		}
	}

    private static void getTopThreeUsersFromDBHelper(int i, String user_id, int plays){
    	if(i >= 3) {
    		return;
    	} else if(plays > topUsers.topThreeUsers[i].songid_play_time){
            getTopThreeUsersFromDBHelper(i+1, topUsers.topThreeUsers[i].user_id, topUsers.topThreeUsers[i].songid_play_time);
            topUsers.topThreeUsers[i].user_id = user_id;
            topUsers.topThreeUsers[i].songid_play_time = plays;
        } else {
            getTopThreeUsersFromDBHelper(i+1, user_id, plays);
        }
	}

	private static void getTopThreeSongsFromDB(String user_id){
		topSongs = new TopThreeSongsImpl();
        topSongs.topThreeSongs = new SongCounterImpl[3];
		for (int i = 0; i<3; i++) topSongs.topThreeSongs[i] = new SongCounterImpl();

		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[1].equals(user_id))
						getTopThreeSongsFromDBHelper(0, splitline[0], Integer.parseInt(splitline[2]));
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
		}
	}

    private static void getTopThreeSongsFromDBHelper(int i, String song_id, int plays){
    	if(i >= 3) {
    		return;
    	} else if(plays > topSongs.topThreeSongs[i].songid_play_time){
            getTopThreeSongsFromDBHelper(i+1, topSongs.topThreeSongs[i].song_id, topSongs.topThreeSongs[i].songid_play_time);
            topSongs.topThreeSongs[i].song_id = song_id;
            topSongs.topThreeSongs[i].songid_play_time = plays;
        } else {
            getTopThreeSongsFromDBHelper(i+1, song_id, plays);
        }
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
