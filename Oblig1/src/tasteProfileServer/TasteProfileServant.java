package tasteProfileServer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import TasteProfile.ProfilerPOA;
import TasteProfile.SongCounter;
import TasteProfile.SongProfile;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeUsers;
import TasteProfile.UserProfile;

public class TasteProfileServant extends ProfilerPOA {
	public static HashMap<String,SongProfile> songProfiles = new HashMap<String,SongProfile>();
	public static HashMap<String, UserProfile> userProfiles = new HashMap<String, UserProfile>();
	
	TasteProfileServantHelper helper = new TasteProfileServantHelper();

	@Override
	public int getTimesPlayed(String song_id) {
		delay();
		if(songProfiles.containsKey(song_id)) {
			return songProfiles.get(song_id).total_play_count;
		}else{
			return helper.getSongPlaysFromDB(song_id);
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
			return helper.getUserPlaysFromDB(song_id, user_id);
		}
		return 0;
	}

	@Override
	public TopThreeUsers getTopThreeUsersBySong(String song_id) {
		delay();
		if(songProfiles.containsKey(song_id)) {
			return songProfiles.get(song_id).top_three_users;
		}else{
			return helper.getTopThreeUsersFromDB(song_id);
		}
	}

	@Override
	public TopThreeSongs getTopThreeSongsByUser(String user_id) {
		delay();
		if(userProfiles.containsKey(user_id)) {
			return userProfiles.get(user_id).top_three_songs;
		}else{
			return helper.getTopThreeSongsFromDB(user_id);
		}
	}

	@Override
	public UserProfile getUserProfile(String user_id) {
		delay();
		if(userProfiles.containsKey(user_id)) {
			return userProfiles.get(user_id);
		}else{
			return helper.getUserFromDB(user_id);
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
