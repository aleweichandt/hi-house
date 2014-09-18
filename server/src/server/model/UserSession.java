package server.model;

import java.util.Date;

public class UserSession {
	
	private String mUserId = null;
	private AdminRights mAdmin = null;
	private String mToken;
	private Date mExpirationTime;
	
	
	private static int counter = 0; //For testing purpose
	
	public UserSession(User user) {
		mUserId = user.getId();
		if(user.isAdmin()) {
			mAdmin = new AdminRights();
		}
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
	
	public boolean isMyUser(String userid){
		return (userid.compareTo("me") == 0 || 
				userid.compareTo(mUserId) == 0);
	}
	
	private User getUser() {
		return User.getFromDB(mUserId);
	}
	
	public AdminRights getAdmin() {
		return mAdmin;
	}
	
	public User getUser(String userid) {
		if(isMyUser(userid)) {
			return getUser();
		}
		return null;
	}
}
