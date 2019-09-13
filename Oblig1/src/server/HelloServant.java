package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import HelloApp.HelloPOA;

public class HelloServant extends HelloPOA {

	@Override
	public String sayHello(String message) {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String now = sdf.format(cal.getTime());

        System.out.println("Message from client: " + message);
        return "Hello from Server at " + now;

	}
	
}
