package tasteProfileClient;

import TasteProfile.SongCounter;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeUsers;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

public class TasteProfileClientHelper {
    UserProfile cachedProfile = new UserProfileImpl() {};

    public void getTimesPlayed(String song_id){
        long start = System.currentTimeMillis();
        int response = TasteProfileClient.servant.getTimesPlayed(song_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("Song %s played %d times. (%d ms)", song_id, response, finish - start));
    }

    public void getTimesPlayedByUser(String user_id, String song_id){
        long start = System.currentTimeMillis();
        int response = getTimesPlayedByUserHelper(user_id, song_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("Song %s played %d times by user %s. (%d ms)", song_id, response, user_id, finish - start));
    }

    private int getTimesPlayedByUserHelper(String user_id, String song_id){
        if(TasteProfileClient.userCaching){
            if(! user_id.equals(cachedProfile.user_id)){
                cachedProfile = getUserProfile(user_id);
            }
            for (SongCounter song : cachedProfile.songs) {
                if(song.song_id.equals(song_id)) {
                    return song.songid_play_time;
                }
			}
        }else{
            return TasteProfileClient.servant.getTimesPlayedByUser(user_id, song_id);
        }
        return 0;
    }

    public void getTopThreeUsersBySong(String song_id){
        long start = System.currentTimeMillis();
        TopThreeUsers response = TasteProfileClient.servant.getTopThreeUsersBySong(song_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("Song %s played most by users %s, %s and %s. (%d ms)", song_id, response.topThreeUsers[0].user_id, response.topThreeUsers[1].user_id, response.topThreeUsers[2].user_id, finish - start));
    }
    
    public void getTopThreeSongsByUser(String user_id){
        long start = System.currentTimeMillis();
        TopThreeSongs response = getTopThreeSongsByUserHelper(user_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("User %s has songs %s, %s and %s as top songs. (%d ms)", user_id, response.topThreeSongs[0].song_id, response.topThreeSongs[1].song_id, response.topThreeSongs[2].song_id, finish - start));
    }

    public TopThreeSongs getTopThreeSongsByUserHelper(String user_id){
        if(TasteProfileClient.userCaching){
            if(! user_id.equals(cachedProfile.user_id)){
                cachedProfile = getUserProfile(user_id);
            }
			return cachedProfile.top_three_songs;
        }else{
            return TasteProfileClient.servant.getTopThreeSongsByUser(user_id);
        }
    }

    private UserProfile getUserProfile(String user_id){
        return TasteProfileClient.servant.getUserProfile(user_id);
    }

	private static void printOutput(String output) {
		System.out.println(output);
		TasteProfileClient.writer.println(output);
	}
}