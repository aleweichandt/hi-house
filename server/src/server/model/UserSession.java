package server.model;

public class UserSession {
	
	//private User mUser
	private String mToken;
	//private mExpirationTime
	
	
	private static int counter = 0; //For testing purpose
	
	public UserSession(String userid) {
		//generar usuario
		mToken = String.valueOf(counter++);//for testing purpose
	}
	
	public boolean isValid() {
		//TODO check expiration time
		return true;
	}
	
	public String getToken() {
		return mToken;
	}
}
