package tasteProfileServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import TasteProfile.SongCounterImpl;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeSongsImpl;
import TasteProfile.TopThreeUsers;
import TasteProfile.TopThreeUsersImpl;
import TasteProfile.UserCounterImpl;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

public class TasteProfileServantHelper {
	private static int result;
	private static UserProfile userProfile;
	private static TopThreeSongsImpl topSongs;
    private static TopThreeUsersImpl topUsers;
    
    private static String[] databases = {"train_triplets_1.txt", "train_triplets_2.txt"};

	public int getSongPlaysFromDB(String song_id){
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
        return result;
	}

	public UserProfile getUserFromDB(String user_id){
		userProfile = new UserProfileImpl();
        userProfile.user_id = user_id;
        userProfile.songs = new SongCounterImpl[0];
        userProfile.top_three_songs = new TopThreeSongsImpl();
        userProfile.top_three_songs.topThreeSongs = new SongCounterImpl[3];
		for (int i = 0; i<3; i++) userProfile.top_three_songs.topThreeSongs[i] = new SongCounterImpl();
		
		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[1].equals(user_id)){
						int plays = Integer.parseInt(splitline[2]);
						TasteProfileCache.addSongCounterUser(splitline[0], userProfile, plays);
						getUserFromDBHelper(0, userProfile, splitline[0], plays );
						userProfile.total_play_count += plays;
					};
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
        }
        return userProfile;
    }
    
	private static void getUserFromDBHelper(int i, UserProfile user, String song_id, int plays){
    	if(i >= 3) {
    		return;
    	} else if(plays > user.top_three_songs.topThreeSongs[i].songid_play_time){
    		getUserFromDBHelper(i+1, user, user.top_three_songs.topThreeSongs[i].song_id, user.top_three_songs.topThreeSongs[i].songid_play_time);
            user.top_three_songs.topThreeSongs[i].song_id = song_id;
            user.top_three_songs.topThreeSongs[i].songid_play_time = plays;
        } else {
        	getUserFromDBHelper(i+1, user, song_id, plays);
        }
	}

	public int getUserPlaysFromDB(String song_id, String user_id){
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
        return result;
    }
    
    public TopThreeUsers getTopThreeUsersFromDB(String song_id){
		topUsers = new TopThreeUsersImpl();
        topUsers.topThreeUsers = new UserCounterImpl[3];
		for (int i = 0; i<3; i++) topUsers.topThreeUsers[i] = new UserCounterImpl();

		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[0].equals(song_id))
						getTopThreeUsersFromDBHelper(splitline[1], Integer.parseInt(splitline[2]));
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
			}
        }
        return topUsers;
	}

    private void getTopThreeUsersFromDBHelper(String user_id, int plays){
		getTopThreeUsersFromDBHelper(2, user_id, plays);
	}
	private void getTopThreeUsersFromDBHelper(int i, String user_id, int plays){
    	if(i < 0) {
    		return;
    	} else if(plays > topUsers.topThreeUsers[i].songid_play_time){
            getTopThreeUsersFromDBHelper(i-1, topUsers.topThreeUsers[i].user_id, topUsers.topThreeUsers[i].songid_play_time);
            topUsers.topThreeUsers[i].user_id = user_id;
            topUsers.topThreeUsers[i].songid_play_time = plays;
        } else {
            getTopThreeUsersFromDBHelper(i-1, user_id, plays);
        }
	}

	public TopThreeSongs getTopThreeSongsFromDB(String user_id){
		topSongs = new TopThreeSongsImpl();
        topSongs.topThreeSongs = new SongCounterImpl[3];
		for (int i = 0; i<3; i++) topSongs.topThreeSongs[i] = new SongCounterImpl();

		for (String database : databases) {
			try (Stream<String> stream = Files.lines(Paths.get(database))) {
				stream.forEach((line) ->
				{
					String[] splitline = line.split("\\s");
					if(splitline[1].equals(user_id))
						getTopThreeSongsFromDBHelper(splitline[0], Integer.parseInt(splitline[2]));
				});
			} catch (IOException e) {
				System.out.println("ERROR : " + e);
				e.printStackTrace(System.out);
			}
        }
        return topSongs;
	}

	private void getTopThreeSongsFromDBHelper(String song_id, int plays){
		getTopThreeSongsFromDBHelper(2, song_id, plays);
	}

    private void getTopThreeSongsFromDBHelper(int i, String song_id, int plays){
    	if(i < 0) {
    		return;
    	} else if(plays > topSongs.topThreeSongs[i].songid_play_time){
            getTopThreeSongsFromDBHelper(i-1, topSongs.topThreeSongs[i].song_id, topSongs.topThreeSongs[i].songid_play_time);
            topSongs.topThreeSongs[i].song_id = song_id;
            topSongs.topThreeSongs[i].songid_play_time = plays;
        } else {
            getTopThreeSongsFromDBHelper(i-1, song_id, plays);
        }
    }
}