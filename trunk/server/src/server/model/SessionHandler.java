package server.model;

import java.util.HashMap;
import java.util.Map;
 
public class SessionHandler {
	
	// Singleton begin
	private static SessionHandler sInstance = null;
	
	public static SessionHandler getInstance() {
		if(sInstance == null) {
			sInstance = new SessionHandler();
		}
		return sInstance;
	}
	// Singleton end
	
	private Map<String,UserSession> mTknSessionMap;
	protected SessionHandler() {
		mTknSessionMap = new HashMap<String,UserSession>();
	}
	
	public UserSession createSessionForUser(String username, String password) {
		User user = User.getFromDBName(username);
		if(user != null) {
			if(user.isValidPassword(password)) {
				UserSession session = new UserSession(user);
				String tkn = session.getToken();
				mTknSessionMap.put(tkn, session);
				return session;
			}
		}
		return null;
	}
	
	public UserSession getSession(String tkn) {
		if(mTknSessionMap.containsKey(tkn)) {
			UserSession session = mTknSessionMap.get(tkn);
			if(!session.isValid()) {
				mTknSessionMap.remove(tkn);
				return null;
			}
			return session;
		}
		return null;
	}
}
