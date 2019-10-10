package tasteProfileClient;

import TasteProfile.SongCounter;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeUsers;
import TasteProfile.UserCounter;
import TasteProfile.UserProfile;
import TasteProfile.UserProfileImpl;

//the client helper contains useful functions to declutter the main class
public class TasteProfileClientHelper {
		//a single user is cached, in case multiple calls on the same user is done.
    UserProfile cachedProfile = new UserProfileImpl() {};

    //getTimesPlayed calls the remote call to get the number of times in total that a given song has been played,
    //and times the response time.
    public void getTimesPlayed(String song_id){
        long start = System.currentTimeMillis();
        int response = TasteProfileClient.servant.getTimesPlayed(song_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("Song %s played %d times. (%d ms)", song_id, response, finish - start));
    }

    //getTimesPlayedByUser calls the helper to get the number of times the specified user has played a given song,
    //and times the response time.
    public void getTimesPlayedByUser(String user_id, String song_id){
        long start = System.currentTimeMillis();
        int response = getTimesPlayedByUserHelper(user_id, song_id);
        long finish = System.currentTimeMillis();
        printOutput(String.format("Song %s played %d times by user %s. (%d ms)", song_id, response, user_id, finish - start));
    }

    //getTimesPlayedByUser checks if the user is cached. If this is not the case, the user is cached for later use.
    //It then returns the number of times the specified user has played a given song,
    //and times the response time.
    //If client-side chaching is disabled, it will instead attempt to do a remote call
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

    //getTopThreeUsersBySong calls the remote call to get the top three users that has played the given song
    //the most times, and times the response time.
    public void getTopThreeUsersBySong(String song_id){
        long start = System.currentTimeMillis();
        TopThreeUsers response = TasteProfileClient.servant.getTopThreeUsersBySong(song_id); //Song ID was played INT times. User ID played INT times.
        long finish = System.currentTimeMillis();
        for(UserCounter user : response.topThreeUsers){
            printOutput(String.format("User %s played %d times.", user.user_id, user.songid_play_time));
        }
        printOutput(String.format("(%d ms)", finish - start));
    }

    //getTopThreeSongsByUser calls the remote call to get the top three songs that the given user
    //has played the most times, and times the response time.
    public void getTopThreeSongsByUser(String user_id){
        long start = System.currentTimeMillis();
        TopThreeSongs response = getTopThreeSongsByUserHelper(user_id);
        long finish = System.currentTimeMillis();
        for(int i = 2; i >= 0; i--){
            printOutput(String.format("Song %s was played %d times.", response.topThreeSongs[i].song_id,  response.topThreeSongs[i].songid_play_time));
        }
        printOutput(String.format("(%d ms)", finish - start));
    }
    
    //getTopThreeSongsByUserHelper checks if the user is cached. If this is not the case, the user is cached for later use.
    //It then returns the number of times the top three songs for the specified user,
    //and times the response time.
    //If client-side chaching is disabled, it will instead attempt to do a remote call
    private TopThreeSongs getTopThreeSongsByUserHelper(String user_id){
        if(TasteProfileClient.userCaching){
            if(! user_id.equals(cachedProfile.user_id)){
                cachedProfile = getUserProfile(user_id);
            }
			return cachedProfile.top_three_songs;
        }else{
            return TasteProfileClient.servant.getTopThreeSongsByUser(user_id);
        }
    }

    //getUserProfile does a remote call to get a complete UserPRofile object from the server
    private UserProfile getUserProfile(String user_id){
        return TasteProfileClient.servant.getUserProfile(user_id);
    }

    //printOutput prints the given string to both the terminal and to a text file
	private static void printOutput(String output) {
		System.out.println(output);
		TasteProfileClient.writer.println(output);
	}
}
