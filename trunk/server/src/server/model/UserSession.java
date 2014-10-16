package server.model;

import java.util.Date;
import java.util.Random;

public class UserSession {
	
	private String mUserId = null;
	private AdminRights mAdmin = null;
	private String mToken;
	private Date mExpirationTime;
	
	
	public UserSession(User user) {
		mUserId = user.getId();
		if(user.isAdmin()) {
			mAdmin = new AdminRights();
		}
		renewSessionTime();
		mToken = generateToken();
	}
	
	private String generateToken() {
		if(mUserId == null) 
			return null;
		
		Random r = new Random();
		String text = Integer.toString(r.nextInt()) + mUserId;
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(text.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
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
	
	public User getUser() {
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
