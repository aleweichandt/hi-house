package server.model;

public class SecurityMgr {
	// Singleton begin
	private static SecurityMgr sInstance = null;
	
	public static SecurityMgr getInstance() {
		if(sInstance == null) {
			sInstance = new SecurityMgr();
		}
		return sInstance;
	}
	// Singleton end
	
	public SecurityMgr() {
		
	}
	
	public void update(int dt) {
		
	}
}
