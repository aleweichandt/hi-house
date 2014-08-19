package server.model;

import java.util.Date;

public class UserSession {
	
	private User mUser;
	private String mToken;
	private Date mExpirationTime;
	
	
	private static int counter = 0; //For testing purpose
	
	public UserSession(User user) {
		mUser = user;
		renewSessionTime();
		mToken = String.valueOf(counter++);//for testing purpose
	}
	
	public boolean isValid() {
		Date now = new Date();
		boolean ret = now.before(mExpirationTime);
		if(ret) renewSessionTime(); //renovar si es valido
		return ret;
	}
	
	public String getToken() {
		return mToken;
	}
	
	private void renewSessionTime() {
		mExpirationTime = new Date(System.currentTimeMillis() + (15*60*1000));//15 mins from now
	}
	
	public User getUser() {
		return mUser;
	}
}
