package tasteProfileServer;

import TasteProfile.ProfilerPOA;

public class TasteProfileServant extends ProfilerPOA {

	@Override
	public int getTimesPlayed(String song_id) {
		// TODO Auto-generated method stub
		return 1001;
	}

	@Override
	public int getTimesPlayedByUser(String user_id, String song_id) {
		// TODO Auto-generated method stub
		return 1001;
	}

	@Override
	public String getTopThreeUsersBySong(String song_id) {
		// TODO Auto-generated method stub
		return "tusen-og-en";
	}

	@Override
	public String getTopThreeSongsByUser(String user_id) {
		// TODO Auto-generated method stub
		return "tusen-og-en";
	}
}
