package server.model;

import java.util.Date;
import java.util.List;

public class UserSession {
	
	private User mUser = null;
	private AdminRights mAdmin = null;
	private String mToken;
	private Date mExpirationTime;
	
	
	private static int counter = 0; //For testing purpose
	
	public UserSession(User user) {
		mUser = user;
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
				userid.compareTo(mUser.getId()) == 0);
	}
	
	public User getUser() {
		return mUser;
	}
	
	public User getUser(String userid) {
		if(isMyUser(userid)) {
			return getUser();
		}
		else if(mAdmin != null) {
			return mAdmin.getUser(userid);
		}
		return null;
	}
	
	public List<String> listAllUsers() {
		if(mAdmin != null) {
			return mAdmin.listAllUsers();
		}
		return null;
	}
}
