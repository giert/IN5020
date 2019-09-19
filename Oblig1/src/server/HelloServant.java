package server;

import HelloApp.HelloPOA;

public class HelloServant extends HelloPOA {

	@Override
	public String getTimesPlayedByUser(String message) {
		String[] messages = message.split(" ");
		String songID = messages[0];
		String userID = messages[1];
        
		System.out.println("Message from client: " + userID + songID);
        return "Hello from Server " + message;
	}
	
	@Override
	public String getTimesPlayed(String message) {
		// TODO Auto-generated method stub
		return null;
	}
}
