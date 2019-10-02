package tasteProfileServer;

public class TasteProfileServerHelper {
/**
    public static SongProfile getSong(String song_id){
        SongProfile song = getSongFromCache(song_id);
        if(song_id == null){
            getUserFromDB(song_id);
        }
        return song;
    }

    public static SongProfile getSongFromDB(String song_id){
    }

    public static SongProfile getSongFromCache(String song_id){
        return TasteProfileServer.songProfiles.getOrDefault(song_id, null);
    }

    public static UserProfile getUser(String user_id){
        UserProfile user = getUserFromCache(user_id);
        if(user == null){
            getUserFromDB(user_id);
        }
        return user;
    }

    public static UserProfile getUserFromDB(String user_id){
    }

    public static UserProfile getUserFromCache(String user_id){
        return TasteProfileServer.userProfiles.getOrDefault(user_id, null);
    }
*/
}