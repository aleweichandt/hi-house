package server.model;

public class SimulationMgr {
	// Singleton begin
	private static SimulationMgr sInstance = null;
	
	public static SimulationMgr getInstance() {
		if(sInstance == null) {
			sInstance = new SimulationMgr();
		}
		return sInstance;
	}
	// Singleton end
	
	public SimulationMgr() {
		
	}
	
	public void update(int dt) {
		
	}
}
