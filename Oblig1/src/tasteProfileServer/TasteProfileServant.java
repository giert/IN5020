package tasteProfileServer;

import TasteProfile.ProfilerPOA;
import TasteProfile.TopThreeSongs;
import TasteProfile.TopThreeUsers;

public class TasteProfileServant extends ProfilerPOA {

	@Override
	public int getTimesPlayed(String song_id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTimesPlayedByUser(String user_id, String song_id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TopThreeUsers getTopThreeUsersBySong(String song_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopThreeSongs getTopThreeSongsByUser(String user_id) {
		// TODO Auto-generated method stub
		return null;
	}
	
}