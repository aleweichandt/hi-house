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
	
	public UserSession createSessionForUser(String userid, String password) {
		//TODO chequear existencia de usuario
		//TODO chequear validez de password
		UserSession session = new UserSession(userid);
		String tkn = session.getToken();
		mTknSessionMap.put(tkn, session);
		return session;
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
